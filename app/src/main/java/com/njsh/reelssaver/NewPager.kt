package com.njsh.reelssaver

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.properties.Delegates
import kotlin.reflect.KProperty


private const val TAG = "NewPager.Kt"

@Preview
@Composable
fun PreviewFun() {
    Pager(itemProvider = { index ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Red)
        )
    })
}

class ValueHolder<T>(var value: T) {
    operator fun component1(): T = value
    operator fun component2(): (T) -> Unit = {
        this.value = it
    }
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> ValueHolder<T>.setValue(thisObj: Any?, property: KProperty<*>, value: T) {
    this.value = value
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> ValueHolder<T>.getValue(thisObj: Any?, property: KProperty<*>): T = value

class MyPagerState(
    itemProvider: @Composable BoxScope.(index: Int) -> Unit
) {
    private lateinit var _scope: CoroutineScope
    val scope: CoroutineScope by lazy { _scope }

    var scroll by mutableStateOf(0f)
    var itemDimension by Delegates.notNull<Int>()
    var dimension by Delegates.notNull<Int>()
    lateinit var recomposeScope: RecomposeScope
    var first: Node<@Composable BoxScope.(Int) -> Unit>
    var last: Node<@Composable BoxScope.(Int) -> Unit>
    var head: Node<@Composable BoxScope.(Int) -> Unit>
    var tail: Node<@Composable BoxScope.(Int) -> Unit>
    var scrollAnimator = ScrollAnimatorImpl(this)

    init {
        first = Node(0, null, null, itemProvider)
        last = first
        head = first
        tail = first
    }

    fun assignScope(scope: CoroutineScope) {
        _scope = scope.apply {
            this.plus(CoroutineExceptionHandler() { coroutineContext, throwable ->
                throwable.printStackTrace()
            })
        }
    }
}


data class Node<T>(
    val index: Int, var prev: Node<T>?, var next: Node<T>?, val content: T
) {
    /**
     * add the other node to end of the current node
     * and returning the tail node
     */
    fun addToEnd(content: T): Node<T> {
        return Node(index + 1, this, next, content).also { next = it }
    }

    /**
     * add the other node before the current node
     * add returns the head node
     */
    fun addToStart(content: T): Node<T> {
        return Node(index - 1, prev, this, content).also { prev = it }
    }

    fun dropFromStart(count: Int): Node<T> {
        var pointer = this
        for (i in 0 until count) {
            pointer = pointer.next ?: break
            pointer.prev = null
        }
        return pointer
    }

    fun dropFromEnd(count: Int): Node<T> {
        var pointer = this
        for (i in 0 until count) {
            pointer = pointer.prev ?: break
            pointer.next = null
        }
        return pointer
    }

    fun shiftBy(steps: Int): Node<T> {
        var pointer = this
        var temp = steps

        when {
            steps > 0 -> {
                while (temp > 0) {
                    pointer = pointer.next
                        ?: throw java.lang.RuntimeException("Node pointer has hit a null: current node(${pointer.index})")
                    temp--
                }
            }
            steps < 0 -> {
                repeat(steps) {
                    while (temp < 0) {
                        pointer = pointer.next
                            ?: throw java.lang.RuntimeException("Node pointer has hit a null node: current node(${pointer.index})")
                        temp++
                    }
                }
            }
        }
        return pointer
    }
}


interface ScrollAnimator<T> {
    fun scrollBy(some: Float)
    fun scrollTo(some: Float)
    fun animatedScrollBy(some: Float, config: T.() -> Unit)
    fun animatedScrollTo(some: Float, config: T.() -> Unit)
}

class ScrollAnimatorImpl(private val state: MyPagerState) : ScrollAnimator<ScrollAnimatorImpl.Configuration> {
    class Configuration {
        var animationSpec = SpringSpec<Float>(
            dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow
        )
        var initialVelocity = 0f
        var onFinish: (MyPagerState) -> Unit = {}
    }

    override fun scrollBy(some: Float) {
        state.scroll += some
    }

    override fun scrollTo(some: Float) {
        state.scroll = some
    }

    override fun animatedScrollBy(some: Float, config: Configuration.() -> Unit) {
        animatedScrollTo(some + state.scroll, config)
    }

    override fun animatedScrollTo(some: Float, config: Configuration.() -> Unit) {
        state.scope.launch() {
            val animatable = Animatable(state.scroll)
            val c = Configuration().apply {
                config()
                if (initialVelocity == 0f) {
                    initialVelocity = animatable.velocity
                }
            }
            animatable.animateTo(
                targetValue = some,
                animationSpec = c.animationSpec,
                initialVelocity = c.initialVelocity
            ) {
                state.scroll = value
            }

            c.onFinish(state)
            Log.d(TAG, "animatedScrollTo: completes")
        }
    }
}

@Composable
fun Pager(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    elements: Int = Int.MAX_VALUE,
    itemProvider: @Composable BoxScope.(index: Int) -> Unit
) {
    val state: MyPagerState = rememberMyPagerState(itemProvider)
    state.recomposeScope = currentRecomposeScope
    state.assignScope(rememberCoroutineScope())

    SubcomposeLayout(modifier = modifier.then(inputModifier(state))) { constraints ->
        state.dimension = constraints.maxHeight
        var filled = 0
        val areaToFill = state.dimension - state.scroll
        val children = mutableListOf<Placeable>()
        state.last = state.first


        // fill items before the first node
        if (state.scroll > 0) {
            var temp = state.scroll
            while (temp > 0 && state.first.index > 0) {
                temp -= state.itemDimension
                state.first = state.first.prev ?: state.first.addToStart(itemProvider)
                    .also { state.head = it }
            }
            state.scroll = temp
        }


        while (true) {
            val measurables = subcompose(slotId = state.last.index) {
                Box(modifier = contentModifier) {
                    state.last.content(this, state.last.index)
                }
            }

            val placeables = measurables.map { it.measure(constraints) }
            children.addAll(placeables)

            filled = placeables.fold(filled) { acc, placeable ->
                acc + placeable.height
            }
            if (filled < areaToFill) {
                state.last =
                    state.last.next ?: state.last.addToEnd(itemProvider).also { state.tail = it }
            } else {
                break
            }
        }


        Log.d(TAG, "all nodes " + checkNode(state.head, state.tail))
        Log.d(TAG, "visible nodes " + checkNode(state.first, state.last))

        layout(constraints.maxWidth, constraints.maxHeight) {
            val x = 0
            var y = state.scroll
            for (child in children) {
                child.place(x, y.roundToInt())
                y += child.height
            }
            state.itemDimension = children[0].height
        }
    }
}


private fun updateFirstVisibleNode(state: MyPagerState) = with(state) {
    if (scroll > 0) scope.launch {
        val count = ceil(scroll / itemDimension).toInt()
        scroll -= count * itemDimension
    }
}


private fun doSlicing(state: MyPagerState) = with(state) {
    if (scroll < 0) state.scope.launch(CoroutineExceptionHandler { coroutineContext, throwable -> throwable.printStackTrace() }) {
        Log.d(TAG, "before doSlicing: scroll = $scroll")
        val count = (scroll / itemDimension).toInt().absoluteValue
        first = first.shiftBy(count)
        scroll += count * itemDimension
        Log.d(TAG, "after doSlicing: scroll = $scroll, first node = ${first.index}")
    }
    scope.launch {
        updateFirstVisibleNode(state)
    }
}

private fun inputModifier(state: MyPagerState) = Modifier.pointerInput(state) {
    val velocityTracker = VelocityTracker()
    val decay = splineBasedDecay<Float>(this)
    val dragHandler: (PointerInputChange, Float) -> Unit = { change, dragAmt ->
        state.scope.launch {
            velocityTracker.addPointerInputChange(change)
            state.scrollAnimator.scrollBy(dragAmt)
        }
    }

    val onDragEnd: () -> Unit = {
        val velocity = velocityTracker.calculateVelocity().y
        velocityTracker.resetTracking()
        val animator = state.scrollAnimator
        val target = decay.calculateTargetValue(state.scroll, velocity)

        Log.d(TAG, "inputModifier: onDragEnd: velocity = $velocity, target = $target")

        animator.animatedScrollTo(target) {
            initialVelocity = velocity
           /* onFinish = { doSlicing(it) }*/
        }
    }

    detectVerticalDragGestures(onVerticalDrag = dragHandler, onDragEnd = onDragEnd)
}

@Composable
fun rememberMyPagerState(itemProvider: @Composable BoxScope.(index: Int) -> Unit) =
    remember { MyPagerState(itemProvider) }


fun <T> checkNode(head: Node<T>, tail: Node<T>): String {
    val stringBuilder = StringBuilder()
    var current = head

    while (current != tail) {
        stringBuilder.append("node(${current.index}) ==> ")
        current = current.next ?: break
    }
    stringBuilder.append("node(${tail.index})")
    return stringBuilder.toString()
}
