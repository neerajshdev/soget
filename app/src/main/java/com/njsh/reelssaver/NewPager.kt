/*
package com.njsh.reelssaver

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import kotlinx.coroutines.*
import kotlin.math.roundToInt
import kotlin.properties.Delegates
import kotlin.reflect.KProperty


private const val TAG = "NewPager.Kt"

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


class InfiniteListState<K, T> {
    private lateinit var datasource: Datasource<K, T>
    private lateinit var _scope: CoroutineScope
    val scope: CoroutineScope by lazy { _scope }

    var cacheSize by Delegates.notNull<Int>()
    var maxCacheSize by Delegates.notNull<Int>()

    private lateinit var firstNode: Node<K, T>
    private lateinit var lastNode: Node<K, T>

    lateinit var head: Node<K, T>
    lateinit var tail: Node<K, T>

    var scroll by mutableStateOf(0f)

    var scrollAnimator: ScrollAnimator = ScrollAnimatorImpl(this)

    fun assignScope(scope: CoroutineScope) {
        _scope = scope.apply {
            this.plus(CoroutineExceptionHandler() { coroutineContext, throwable ->
                throwable.printStackTrace()
            })
        }
    }

    fun assignDataSource(datasource: Datasource<K, T>) {
        this.datasource = datasource
    }

    fun assignFirstVisibleNode(node: Node<K, T>) {
        firstNode = node
    }

    fun assignLastVisibleNode(node: Node<K, T>) {
        lastNode = node
    }

    fun getFirstVisibleNode(): Node<K, T> {
        return firstNode
    }

    fun getLastVisibleNode(): Node<K, T> {
        return lastNode
    }

    fun createFirst(): Node<K, T> {
        return datasource.onCreateFirst().also {
            firstNode = it
            head = firstNode
            tail = firstNode
        }
    }

    fun getNext(node: Node<K, T>): Node<K, T>? = with(node) {
        next ?: datasource.onCreateNext(this)
    }?.also {
        tail = it
    }

    fun getPrev(node: Node<K, T>): Node<K, T>? = with(node) {
        prev ?: datasource.onCreatePrev(this)
    }?.also {
        head = it
    }
}


abstract class Datasource<K, T> {
    abstract fun onCreateFirst(): Node<K, T>
    abstract fun onCreateNext(current: Node<K, T>): Node<K, T>?
    abstract fun onCreatePrev(current: Node<K, T>): Node<K, T>?
}

data class PlacementInfo(
    var x: Int = 0, var y: Int = 0, var width: Int = 0, var height: Int = 0
)

data class Node<K, T>(
    val data: T,
    val key: K,
    var prev: Node<K, T>? = null,
    var next: Node<K, T>? = null,
    var placementInfo: PlacementInfo = PlacementInfo()
) {
    private val onPlacementChange by lazy { mutableListOf<(PlacementInfo, PlacementInfo) -> Unit>() }


    fun addPlacementListener(listener: (old: PlacementInfo, new: PlacementInfo) -> Unit) {
        onPlacementChange.add(listener)
    }

    fun removePlacementListener(listener: (old: PlacementInfo, new: PlacementInfo) -> Unit) {
        onPlacementChange.remove(listener)
    }

    */
/**
     * add the other node to end of the current node
     * and returning the tail node
     *//*
*/
/* fun addToEnd(content: T): Node<K, T> {
        return Node(index + 1, this, next, content).also { next = it }
    }
*//*

    */
/**
     * add the other node before the current node
     * add returns the head node
     *//*
*/
/* fun addToStart(content: T): Node<K, T> {
        return Node(index - 1, prev, this, content).also { prev = it }
    }
*//*

    fun dropFromStart(count: Int): Node<K, T> {
        var pointer = this
        for (i in 0 until count) {
            pointer = pointer.next ?: break
            pointer.prev = null
        }
        return pointer
    }

    fun dropFromEnd(count: Int): Node<K, T> {
        var pointer = this
        for (i in 0 until count) {
            pointer = pointer.prev ?: break
            pointer.next = null
        }
        return pointer
    }

    */
/*fun shiftBy(steps: Int): Node<K, T> {
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
    }*//*

}


interface ScrollAnimator {
    fun scrollBy(some: Float)
    fun scrollTo(some: Float)
    fun animatedScrollBy(some: Float, config: Configuration.() -> Unit)
    fun animatedScrollTo(some: Float, config: Configuration.() -> Unit)

    class Configuration {
        var animationSpec = SpringSpec<Float>(
            dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow
        )
        var initialVelocity = 0f
        var onFinish: () -> Unit = {}
    }
}

class ScrollAnimatorImpl<K, T>(private val state: InfiniteListState<K, T>) : ScrollAnimator {

    override fun scrollBy(some: Float) {
        state.scroll += some
    }

    override fun scrollTo(some: Float) {
        state.scroll = some
    }

    override fun animatedScrollBy(some: Float, config: ScrollAnimator.Configuration.() -> Unit) {
        animatedScrollTo(some + state.scroll, config)
    }

    override fun animatedScrollTo(some: Float, config: ScrollAnimator.Configuration.() -> Unit) {
        state.scope.launch() {
            val animatable = Animatable(state.scroll)
            val c = ScrollAnimator.Configuration().apply {
                config()
                if (initialVelocity == 0f) {
                    initialVelocity = animatable.velocity
                }
            }

            var prev = state.scroll

            animatable.animateTo(
                targetValue = some,
                animationSpec = c.animationSpec,
                initialVelocity = c.initialVelocity
            ) {
                scrollBy(value - prev)
                prev = value
            }

            c.onFinish()
        }
    }
}

@Composable
fun <K, T> InfiniteList(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    datasource: Datasource<K, T>,
    content: @Composable BoxScope.(Node<K, T>) -> Unit
) {
    val state = rememberInfiniteListState(datasource)
    SubcomposeLayout(modifier = modifier.then(inputModifier(state))) { constraints ->
        fun composeNode(node: Node<K, T>): List<Measurable> {
            return subcompose(slotId = node.key) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    content(node)
                }
            }
        }


        var filled = 0
        val areaToFill = constraints.maxHeight + state.scroll
        val children = mutableListOf<Placeable>()

        var nodeP1 = state.getFirstVisibleNode()

        // fill empty area above the first visible item
        if (state.scroll < 0) {
            var tempScroll = state.scroll
            while (tempScroll < 0) {
                val nodeP2 = state.getPrev(nodeP1) ?: break
                state.assignFirstVisibleNode(nodeP2)
                val placeables = composeNode(nodeP2).map { it.measure(constraints) }
                children.addAll(placeables)
                tempScroll += placeables.fold(0) { acc, p -> p.height + acc }
            }
            state.scroll = tempScroll
        }


        while (true) {
            val measurables = composeNode(nodeP1)

            val placeables = measurables.map { it.measure(constraints) }
            children.addAll(placeables)

            filled = placeables.fold(filled) { acc, placeable ->
                acc + placeable.height
            }
            if (filled < areaToFill) {
                nodeP1 = state.getNext(nodeP1) ?: break
            }
        }
        state.assignLastVisibleNode(nodeP1)

        state.scope.launch(Dispatchers.Default) {
            Log.d(
                TAG, "visible nodes " + checkNode(
                    state.getFirstVisibleNode(), state.getLastVisibleNode()
                )
            )
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            val x = 0
            var y = -state.scroll
            for (child in children) {
                child.place(x, y.roundToInt())
                y += child.height
            }
        }
    }
}


private fun <K, T> inputModifier(state: InfiniteListState<K, T>) = Modifier.pointerInput(state) {
    val velocityTracker = VelocityTracker()
    val decay = splineBasedDecay<Float>(this)

    val dragHandler: (PointerInputChange, Float) -> Unit = { change, dragAmt ->
        state.scope.launch {
            velocityTracker.addPointerInputChange(change)
            state.scrollAnimator.scrollBy(-dragAmt)
        }
    }

    val onDragEnd: () -> Unit = {
        val velocity = velocityTracker.calculateVelocity().y
        velocityTracker.resetTracking()
        val animator = state.scrollAnimator
        val target = decay.calculateTargetValue(state.scroll, -velocity)

        Log.d(TAG, "inputModifier: onDragEnd: velocity = $velocity, target = $target")

        animator.animatedScrollTo(target) {
            initialVelocity = velocity
        }
    }

    detectVerticalDragGestures(onVerticalDrag = dragHandler, onDragEnd = onDragEnd)
}


@Composable
fun <K, T> rememberInfiniteListState(datasource: Datasource<K, T>): InfiniteListState<K, T> {
    val scope = rememberCoroutineScope()
    return remember {
        InfiniteListState<K, T>().apply {
            assignDataSource(datasource)
            assignScope(scope)
            createFirst()
        }
    }
}


fun <K, T> checkNode(head: Node<K, T>, tail: Node<K, T>): String {
    val stringBuilder = StringBuilder()
    var current = head

    while (current != tail) {
        stringBuilder.append("node(${current.key}) ==> ")
        current = current.next ?: break
    }
    stringBuilder.append("node(${tail.key})")
    return stringBuilder.toString()
}
*/
