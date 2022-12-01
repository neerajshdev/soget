package com.njsh.reelssaver

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

private const val TAG = "Pager.kt"

/**
 * @see
 * <h2>Specification</h2>
 * <ul>
 *  <li>The selected item will be centered on the container.</li>
 *  <li>The pager can scroll horizontally or vertically.</li>
 *  <li>Items can occupy a fraction of the container size, so that we can have neighbouring items peaking on the sides.</li>
 *  <li>It’s possible to overshoot items so that the user can scroll past the first or last item, but the pager then resets back to centering the item.</li>
 *  <li>We can specify a separation between items.</li>
 *  <li>We can indicate which item will be initially centered.</li>
 * </ul>
 *
 * @param modifier : Optional modifier object
 * @param state: Pager state
 * @contentBuilder: You will compose your item in it.
 * @onItemSelect: callback for page change, you will get index and item in lambda params.
 */

@Composable
fun <T : Any> Pager(
    modifier: Modifier = Modifier,
    state: PagerState<T>,
    initialIndex: Int = 0,
    onItemSelect: (Int, Int, T) -> Unit = { _, _, _ -> },
    contentBuilder: @Composable (Int, T) -> Unit,
) {
    Log.d(TAG, "Pager()")

    val orientation = state.orientation
    val itemFraction = state.itemFraction
    val numberOfItems = state.numberOfItems
    val itemSpacing = state.itemSpacing
    state.listener =
        { oldIndex, newIndex -> onItemSelect(oldIndex, newIndex, state.items[newIndex]) }
    state.scope = rememberCoroutineScope()

    Layout(
        content = {
            for (i in 0 until state.items.size) {
                Box(
                    modifier = when (orientation) {
                        Orientation.Horizontal -> Modifier.fillMaxWidth()
                        Orientation.Vertical -> Modifier.fillMaxHeight()
                    },
                    contentAlignment = Alignment.Center,
                ) {
                    contentBuilder(i, state.items[i])
                }
            }
        },
        modifier = modifier
            .clipToBounds()
            .then(state.inputModifier),
    ) { measurables, constraints ->
        val dimension = constraints.dimension(orientation)
        val looseConstraints = constraints.toLooseConstraints(orientation, itemFraction)
        val placeables = measurables.map { measurable -> measurable.measure(looseConstraints) }
        val size = placeables.getSize(orientation, dimension)
        val itemDimension = (dimension * state.itemFraction).roundToInt()

        state.itemDimension = itemDimension
        val halfItemDimension = itemDimension / 2

        layout(size.width, size.height) {
            if (state.items.isEmpty()) {
                return@layout
            }

            val centerTranslation = dimension / 2 - halfItemDimension
            val dragTranslation = state.dragTranslation.value.roundToInt()
            val itemDimensionWithSpace = (itemDimension + itemSpacing)
            val dragMinusCenterTx = (dragTranslation - centerTranslation)

            var firstVisibleItemIx = dragMinusCenterTx / itemDimensionWithSpace
            if (dragMinusCenterTx % itemDimensionWithSpace > itemDimension) {
                firstVisibleItemIx++
            }

            firstVisibleItemIx = firstVisibleItemIx.coerceIn(0, numberOfItems)

            val lastVisibleItemIx =
                ((dragMinusCenterTx + dimension) / itemDimensionWithSpace).coerceIn(
                    0 until numberOfItems
                )

            Log.d(
                TAG,
                "Pager: visible items: ($firstVisibleItemIx to $lastVisibleItemIx), dragTx: $dragTranslation"
            )

            for (i in firstVisibleItemIx..lastVisibleItemIx) {
                when (orientation) {
                    Orientation.Horizontal -> {
                        val placeable = placeables[i]
                        val posX =
                            (itemDimensionWithSpace * i) + centerTranslation - dragTranslation
                        val posY = size.height / 2f - placeable.height / 2f
                        placeable.place(posX, posY.roundToInt())
                    }
                    Orientation.Vertical -> {
                        val placeable = placeables[i]
                        val posY =
                            (itemDimensionWithSpace * i) + centerTranslation - dragTranslation
                        val posX = size.width / 2f - placeable.width / 2f
                        placeable.place(posX.roundToInt(), posY)
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = state.items, key2 = initialIndex) {
        state.snapTo(initialIndex)
    }
}

@Composable
fun <T : Any> rememberPagerState(items: List<T>): PagerState<T> = remember { PagerState(items) }

private fun Constraints.dimension(orientation: Orientation) = when (orientation) {
    Orientation.Horizontal -> maxWidth
    Orientation.Vertical -> maxHeight
}


class PagerState<T : Any>(
    items: List<T>,
) {
    val items = SnapshotStateList<T>().apply { addAll(items) }
    var itemFraction by mutableStateOf(1f)
    var currentIndex = 0
    val numberOfItems get() = items.size
    var overshootFraction by mutableStateOf(0f)
    var itemSpacing by mutableStateOf(0)
    var orientation by mutableStateOf(Orientation.Horizontal)
    var listener: (Int, Int) -> Unit = { _, _ -> }
    val dragTranslation = Animatable(0f)

    internal var itemDimension by mutableStateOf(0)
    internal lateinit var scope: CoroutineScope

    private lateinit var dragTxConstrain: ValueConstraint


    private val animationSpec = SpringSpec<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow,
    )

    suspend fun snapTo(index: Int) {
        dragTranslation.snapTo(index.toFloat() * (itemDimension + itemSpacing))
    }

    val inputModifier
        get() = Modifier.pointerInput(numberOfItems) {
            fun selectedItemIndex(dragTx: Int): Int {
                val index = dragTx / (itemDimension + itemSpacing)
                return index.coerceIn(0, numberOfItems)
            }

            fun updateCurrentItemIndex(dragX: Float) {
                val index = selectedItemIndex(dragX.roundToInt())
                if (index != currentIndex) {
                    listener(currentIndex, index)
                    currentIndex = index
                }
            }


            fun dragTranslateConstraint(): ValueConstraint {
                val dimension = when (orientation) {
                    Orientation.Horizontal -> size.width
                    Orientation.Vertical -> size.height
                }
                val itemSideMargin = (dimension - itemDimension) / 2f
                return ValueConstraint(
                    min = -dimension * overshootFraction + itemSideMargin,
                    max = ((itemDimension + itemSpacing) * (numberOfItems - 1)) + (overshootFraction * dimension) - itemSideMargin
                )
            }


            dragTxConstrain = dragTranslateConstraint()
            Log.d(TAG, "inputModifier: $dragTxConstrain ")
            forEachGesture {
                Log.d(TAG, "forEachGesture()")
                awaitPointerEventScope {
                    Log.d(TAG, "awaitPointerEventScope()")
                    val tracker = VelocityTracker()
                    val decay = splineBasedDecay<Float>(this)
                    val down = awaitFirstDown()

                    val dragHandler = { change: PointerInputChange ->
                        scope.launch {
                            val dragChange = change.calculateDragChange(orientation)
                            dragTranslation.snapTo(
                                (dragTranslation.value - dragChange).coerceIn(
                                    dragTxConstrain.min, dragTxConstrain.max
                                )
                            )
                            updateCurrentItemIndex(dragTranslation.value)
                        }
                        tracker.addPosition(change.uptimeMillis, change.position)
                    }

                    when (orientation) {
                        Orientation.Horizontal -> horizontalDrag(down.id, dragHandler)
                        Orientation.Vertical -> verticalDrag(down.id, dragHandler)
                    }

                    val velocity = tracker.calculateVelocity(orientation)
                    scope.launch {
                        var dragTx = decay.calculateTargetValue(dragTranslation.value, -velocity)
                        val itemDimenPlusSpacing = itemDimension + itemSpacing
                        dragTx = dragTx.coerceIn(
                            0f, (numberOfItems - 1) * itemDimenPlusSpacing.toFloat()
                        )
                        val nextItemIndex = dragTx.toInt() / itemDimension
                        dragTx = nextItemIndex * itemDimenPlusSpacing.toFloat()

                        dragTranslation.animateTo(
                            animationSpec = animationSpec,
                            targetValue = dragTx,
                            initialVelocity = -velocity
                        )

                        updateCurrentItemIndex(dragTranslation.value)
                    }
                }
            }
        }
}

private fun VelocityTracker.calculateVelocity(orientation: Orientation) = when (orientation) {
    Orientation.Horizontal -> calculateVelocity().x
    Orientation.Vertical -> calculateVelocity().y
}

private fun PointerInputChange.calculateDragChange(orientation: Orientation) = when (orientation) {
    Orientation.Horizontal -> positionChange().x
    Orientation.Vertical -> positionChange().y
}


private fun Constraints.toLooseConstraints(
    orientation: Orientation,
    itemFraction: Float,
): Constraints {
    val dimension = dimension(orientation)
    return when (orientation) {
        Orientation.Horizontal -> copy(
            minWidth = (dimension * itemFraction).roundToInt(),
            maxWidth = (dimension * itemFraction).roundToInt(),
            minHeight = 0,
        )
        Orientation.Vertical -> copy(
            minWidth = 0,
            minHeight = (dimension * itemFraction).roundToInt(),
            maxHeight = (dimension * itemFraction).roundToInt(),
        )
    }
}

private fun List<Placeable>.getSize(
    orientation: Orientation,
    dimension: Int,
): IntSize {
    return when (orientation) {
        Orientation.Horizontal -> IntSize(
            dimension, maxByOrNull { it.height }?.height ?: 0
        )
        Orientation.Vertical -> IntSize(
            maxByOrNull { it.width }?.width ?: 0, dimension
        )
    }
}


data class ValueConstraint(
    val min: Float,
    val max: Float,
)


/**** NEW PAGER IMPLEMENTATION **** /
 *
 */


@Composable
fun Pager(
    modifier: Modifier = Modifier,
    initialIndex: Int = 0,
    indexSelection: Float = 0.5f,
    itemFraction: Float = 1f,
    itemSpacing: Int = 0,
    state: NewPagerState = rememberPagerState(),
    orientation: Orientation = Orientation.Vertical,
    content: PagerScope.() -> Unit
) {
    val pagerScope = PagerScope()
    pagerScope.content()

    state.scope = rememberCoroutineScope()
    state.first = pagerScope.pagerItemScope.first
    state.last = pagerScope.pagerItemScope.last
    state.itemSpacing = itemSpacing
    state.firstVisibleItemIndex = initialIndex

    var lastVisibleItemIndex = 0

    val inputModifier = Modifier.pointerInput(Unit) {

        fun updateIndex() {
            with(state) {
                val selectorPoint = dimension * indexSelection
                val relativeDis = selectorPoint - centerOffset + scroll.value
                // index relative to first visible item index
                val index = firstVisibleItemIndex + floor( relativeDis / (itemDimension + itemSpacing)).toInt().coerceIn(first, last)
                if (currentIndex != index) {
                    pagerScope.onItemDeSelect(currentIndex)
                    currentIndex = index
                    pagerScope.onItemSelect(currentIndex)
                }
            }
        }

        detectDragGestures { change, dragAmount ->
            state.scope.launch {
                val dragAmt = when (orientation) {
                    Orientation.Vertical -> dragAmount.y
                    Orientation.Horizontal -> dragAmount.x
                }

                val itemWithSpacing = state.itemSpacing + state.itemDimension
                var value = state.scroll.value - dragAmt

                // increment or decrement firstVisibleItemIndex on scroll
                var deltaIndex = floor((value - state.centerOffset) / itemWithSpacing).toInt()
                deltaIndex = deltaIndex.coerceIn(
                    state.first - state.firstVisibleItemIndex,
                    state.last - state.firstVisibleItemIndex
                )
                state.firstVisibleItemIndex += deltaIndex
                value -= deltaIndex * itemWithSpacing

                state.scroll.snapTo(value)
               /* Log.d(
                    TAG,
                    "Pager: itemDimen with spacing = $itemWithSpacing," + " dragAmt = $dragAmt, " + "delta = $deltaIndex, " + "firstItemIndex = ${state.firstVisibleItemIndex}, " + "lastVisibleItemIndex = $lastVisibleItemIndex " + "scroll = ${state.scroll.value}, " + "centerOffset = ${state.centerOffset}"
                )*/
                updateIndex()
            }
        }
    }

    val measurePolicy = MeasurePolicy { measurables, constraints ->
        val itemConstraint = constraints.toLooseConstraints(orientation, itemFraction)
        val placeables = measurables.map { it.measure(itemConstraint) }
        state.dimension = constraints.dimension(orientation)
        val itemDimension = (state.dimension * itemFraction)
        val size = placeables.getSize(orientation, state.dimension)
        val centerOffset = (state.dimension / 2f - itemDimension / 2f)

        state.itemDimension = itemDimension
        state.centerOffset = centerOffset

        layout(width = size.width, height = size.height) {
            val itemDimenPlusSpacing = itemDimension + itemSpacing
            val firstVisibleItemIx = state.firstVisibleItemIndex
            lastVisibleItemIndex =
                firstVisibleItemIx + ceil((state.dimension - state.centerOffset) / itemDimenPlusSpacing).toInt()

            lastVisibleItemIndex = lastVisibleItemIndex.coerceIn(
                pagerScope.pagerItemScope.first, pagerScope.pagerItemScope.last
            )

            for (index in firstVisibleItemIx..lastVisibleItemIndex) {
                val placeable = placeables[index]
                val relativeIndex = index - firstVisibleItemIx
                val itemPos =
                    -state.scroll.value + centerOffset + itemDimenPlusSpacing * relativeIndex

                when (orientation) {
                    Orientation.Horizontal -> {
                        val posY = size.height / 2f - placeable.height / 2f
                        placeable.place(itemPos.roundToInt(), posY.roundToInt())
                    }
                    Orientation.Vertical -> {
                        val posX = size.width / 2f - placeable.width / 2f
                        placeable.place(posX.roundToInt(), itemPos.roundToInt())
                    }
                }
            }
        }
    }

    Layout(modifier = modifier.then(inputModifier), measurePolicy = measurePolicy, content = {
        for (index in pagerScope.pagerItemScope.first..pagerScope.pagerItemScope.last) {
            Box(
                modifier = when (orientation) {
                    Orientation.Vertical -> Modifier.fillMaxHeight()
                    Orientation.Horizontal -> Modifier.fillMaxWidth()
                }
            ) {
                pagerScope.pagerItemScope.item(index)
            }
        }
    })
}

class PagerScope {
    lateinit var pagerItemScope: PagerItemScope
    var onItemSelect: (index: Int) -> Unit = {}
    var onItemDeSelect: (index: Int) -> Unit = {}

    fun range(range: IntRange, content: @Composable (Int) -> Unit) {
        pagerItemScope = PagerItemScope(range.first, range.last, item = content)
    }
}

class PagerItemScope(val first: Int, val last: Int, val item: @Composable (item: Int) -> Unit)


class NewPagerState {
    lateinit var scope: CoroutineScope
    var scroll = Animatable(0f)
    var first = 0
    var last = 0
    var itemDimension = 0f
    var dimension = 0
    var itemSpacing = 0 // in pixels
    var currentIndex = 0
    var centerOffset = 0f

    var firstVisibleItemIndex by mutableStateOf(0)

    fun snapTo(index: Int) {
        if (index !in first..last) {
            throw IndexOutOfBoundsException("index: $index is out of bounds($first, $last)")
        }
        val value = currentIndex - index
        firstVisibleItemIndex = value
    }

    fun snapTo(value: Float) {
        val itemDimenPlusSpacing = itemSpacing + itemDimension
        val indexDelta = (value / itemDimenPlusSpacing).toInt()
        val scrollDelta = value.mod(itemDimenPlusSpacing.toDouble()).toFloat()

        scope.launch {
            firstVisibleItemIndex += indexDelta
            scroll.snapTo(scrollDelta)
        }
    }
}

@Composable
fun rememberPagerState() = remember { NewPagerState() }






