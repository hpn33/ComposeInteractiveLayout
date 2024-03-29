package inter

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize


/*

# Ability
-  move
- zoom
- mouse position
- add item by position

*/

const val RepeatOffset = 50f

@Composable
inline fun rememberInteractiveState() = remember { InteractiveState() }

data class InteractiveState(
    val scale: MutableState<Float> = mutableStateOf(1f),
    val offsetX: MutableState<Float> = mutableStateOf(0f),
    val offsetY: MutableState<Float> = mutableStateOf(0f),
    val screenSize: MutableState<IntSize> = mutableStateOf(IntSize(1, 1))
)

// state
//

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun InteractiveBox(
    state: InteractiveState = rememberInteractiveState(),
    onViewClick: (position: Offset, ViewState) -> Unit = { _, _ -> },
    content: @Composable (ViewState) -> Unit = {},
) {

    var mousePositionOnBorder by remember { mutableStateOf(Offset.Zero) }

//    var screenSize by remember { state.screenSize }
    val screenSizeAnimate by animateIntSizeAsState(state.screenSize.value)

    val scaleAnimate by animateFloatAsState(state.scale.value)
    val offsetXAnimate by animateFloatAsState(state.offsetX.value)
    val offsetYAnimate by animateFloatAsState(state.offsetY.value)


    val viewState = ViewState(
        environmentOffset = Offset(-offsetXAnimate, -offsetYAnimate),
        screenSize = screenSizeAnimate,
        scale = scaleAnimate
    )

//    println(viewState)

    // Box/Border Layout
    boxBorder(frameSizeChange = { state.screenSize.value = it }) {

//        GuildLineBackground(viewState)

        // Touch Control Layout
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        state.apply {
                            scale.value *= zoom

                            offsetX.value -= pan.x / scale.value
                            offsetY.value -= pan.y / scale.value
                        }
                    }
                }
                .onMouseScroll { scrollDelta ->

//                    //                 Zoom with mouse scroll
                    state.scale.value =
                        (state.scale.value * 1f - (scrollDelta / 10f))
                            .let { climb(value = it, min = 0.1f, max = 10f) }

                }
                .onPointerEvent(PointerEventType.Move) {
                    mousePositionOnBorder = it.changes.first().position
                }
                .onClick {
                    onViewClick(mousePositionOnBorder, viewState)

                }
//                .clickable {
//                }
                .fillMaxSize()
        )

        viewportLayout(viewState) {
            worldBaseLayout(viewState) {

                content(viewState)


                // MOUSE
                val mouse = viewState.viewport.mousePositionOnWorld(mousePositionOnBorder)

                Box(modifier = Modifier.offset((mouse.x).dp, (mouse.y).dp)) {
                    Text((mouse).toString())
                }

                Box(modifier = Modifier.background(Color.Yellow.copy(alpha = .1f))) {
                    Text("World")
                }


            }
        }

        viewportLayout(viewState) {


            // center of screen view
            Box(
                modifier = Modifier
//                )
                    .offset(
                        ((viewState.screenSize.width - (viewState.viewport.size.width /* * viewState.scale*/)) / 2f).dp,
                        ((viewState.screenSize.height - (viewState.viewport.size.height /* * viewState.scale*/)) / 2f).dp
                    )
                    .size(
                        (viewState.viewport.size.width /* * viewState.scale*/).dp,
                        (viewState.viewport.size.height/* * viewState.scale*/).dp
                    )
                    .border(1.dp, Color.Black)
//            ,
//                    .scale(viewState.scale)
                ,
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .background(Color.Black)
                        .offset(50.dp, 1.dp)
                        .size(100.dp, 2.dp)
                )
                Box(
                    modifier = Modifier
                        .background(Color.Black)
                        .offset(1.dp, 50.dp)
                        .size(2.dp, 100.dp)
                )


                // ViewPort Position
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(Color.Red.copy(alpha = .1f))
                ) {
                    Column {
                        Text("Viewport")
                        Text((viewState.position).toString())
                    }

                }

            }


        }

        // Control Panel Layout
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd,
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                IconButton(onClick = { interactiveState.scale += 0.25f }) {
//                    Icon(Icons.Default.Add, contentDescription = null)
//                }
//
//                IconButton(onClick = { interactiveState.scale -= 0.25f }) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = null)
//                }
//
//                Divider(modifier = Modifier.size(width = 35.dp, height = 2.dp))

                IconButton(onClick = {
                    state.apply {
                        scale.value = 1f
                        offsetX.value = 0f
                        offsetY.value = 0f
                    }

                }) {
                    Icon(Icons.Default.Home, contentDescription = null)
                }

                Text(
                    "scale\n" + viewState.scale,
                    textAlign = TextAlign.Center
                )
                Text(
                    "size\n" + viewState.screenSize,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}

@Composable
private fun GuildLineBackground(viewState: ViewState) {

    val offsetEdge = (RepeatOffset * viewState.scale).toInt()
    val guildLineDraw = offsetEdge * 10

    val alpha =
        if (viewState.scale > 1)
            1f
        else
            (if (viewState.scale <= .2f) 0f else viewState.scale)

    val yRange = viewState.viewYRangeInt
    val xRange = viewState.viewXRangeInt


    // background guild line
    // TODO: not set with viewport
    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {

                    var yIndex = -viewState.environmentOffset.y.toInt()

                    while (yIndex >= yRange.first) {
                        yIndex -= 1

                        if (yIndex % offsetEdge != 0) {
                            continue
                        }

                        val y = (yIndex.toFloat() - yRange.first)

                        drawLine(
                            Color.LightGray.copy(alpha = alpha),
                            start = Offset(x = 0f, y = y),
                            end = Offset(x = viewState.screenSize.width.toFloat(), y = y),
                        )
                    }


                    for (yIndex in (-viewState.environmentOffset.y.toInt())..yRange.last) {

                        if (yIndex % offsetEdge != 0) {
                            continue
                        }

                        val y = (yIndex.toFloat() - yRange.first)

                        drawLine(
                            Color.LightGray.copy(alpha = alpha),
                            start = Offset(x = 0f, y = y),
                            end = Offset(x = viewState.screenSize.width.toFloat(), y = y),
                        )
                    }
//
//                    for (yIndex in yRange) {
//
//                        if (yIndex % offsetEdge != 0) {
//                            continue
//                        }
//
//                        val y = (yIndex.toFloat() - yRange.first) * viewState.scale
//
//                        drawLine(
//                            Color.LightGray.copy(alpha = alpha),
//                            start = Offset(x = 0f, y = y),
//                            end = Offset(x = viewState.screenSize.width.toFloat(), y = y),
//                        )
//                    }
                }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {

                    var xIndex = -viewState.environmentOffset.x.toInt()

                    while (xIndex >= xRange.first) {
                        xIndex -= 1

                        if (xIndex % offsetEdge != 0) {
                            continue
                        }

                        val x = (xIndex.toFloat() - xRange.first)

                        drawLine(
                            Color.LightGray.copy(alpha = alpha),
                            start = Offset(y = 0f, x = x),
                            end = Offset(y = viewState.screenSize.height.toFloat(), x = x),
                        )
                    }


                    for (xIndex in (-viewState.environmentOffset.x.toInt())..xRange.last) {

                        if (xIndex % offsetEdge != 0) {
                            continue
                        }

                        val x = (xIndex.toFloat() - xRange.first)

                        drawLine(
                            Color.LightGray.copy(alpha = alpha),
                            start = Offset(y = 0f, x = x),
                            end = Offset(y = viewState.screenSize.height.toFloat(), x = x),
                        )
                    }
                }
        )

//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .drawBehind {
//
//
//                    for (yIndex in yRange) {
//
//                        if (yIndex % guildLineDraw != 0) {
//                            continue
//                        }
//
//                        val y = yIndex.toFloat() - yRange.first
//
//                        drawLine(
//                            Color.Gray,
//                            start = Offset(x = 0f, y = y),
//                            end = Offset(x = viewState.screenSize.width.toFloat(), y = y),
//                        )
//                    }
//                }
//        )
//
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .drawBehind {
//
//                    for (xIndex in xRange) {
//
//                        if (xIndex % guildLineDraw != 0) {
//                            continue
//                        }
//
//                        val x = xIndex.toFloat() - xRange.first
//
//                        drawLine(
//                            Color.Gray,
//                            start = Offset(y = 0f, x = x),
//                            end = Offset(y = viewState.screenSize.width.toFloat(), x = x),
//                        )
//                    }
//                }
//        )

    }
}

@Composable
inline fun viewportLayout(viewState: ViewState, content: @Composable () -> Unit) {

//    val offset = viewState.offset

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(unbounded = true)
            .scale(viewState.scale)
//            .offset(offset.x.dp, offset.y.dp)
    ) {

        content()

    }
}

@Composable
inline fun boxBorder(
    crossinline frameSizeChange: (IntSize) -> Unit,
    content: @Composable () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RectangleShape)
            .onGloballyPositioned { layoutCoordinates ->
                frameSizeChange(layoutCoordinates.size)
            }
    ) {
        content()
    }
}

@Composable
inline fun worldBaseLayout(viewState: ViewState, content: @Composable () -> Unit) {


    var size by remember { mutableStateOf(IntSize.Zero) }

    val offset = viewState.offset + (size / 2).let { Offset(it.width.toFloat(), it.height.toFloat()) }

    // draw Layer
    Box(
        Modifier
            .offset(offset.x.dp, offset.y.dp)
//            .background(Color.LightGray)
            .onSizeChanged {
                size = it
            }
    ) {

        content()

    }
}


data class ViewState(
    val environmentOffset: Offset,
    var screenSize: IntSize,
    val scale: Float,
) {

    val viewport = Viewport(
        environmentOffset,
        screenSize.toSize(),
        scale
    )

    val halfScreen get() = screenSize / 2
    val halfScreenOffset get() = halfScreen.let { Offset(it.width.toFloat(), it.height.toFloat()) }
//    val halfScreenX get() = screenSize.width / 2
//    val halfScreenY get() = screenSize.height / 2


    //    val viewYRange get() = (environmentOffset.y..(screenSize.height + environmentOffset.y))
    val viewYRangeInt: IntRange
        get() {
            val centerPoint = -environmentOffset.y
            val sideRange = halfScreen.height / scale

            return ((-sideRange + centerPoint).toInt()..(centerPoint + sideRange).toInt())
        }

    //    val viewYRangeInt get() = ((environmentOffset.y - halfScreenY).toInt()..(screenSize.height - halfScreenY + environmentOffset.y).toInt())
//    val viewXRange get() = (environmentOffset.x..(screenSize.width + environmentOffset.x))
    val viewXRangeInt: IntRange
        get() {
            val centerPoint = -environmentOffset.x
            val sideRange = halfScreen.width / scale

            return ((-sideRange + centerPoint).toInt()..(centerPoint + sideRange).toInt())
        }


//    val xStartView get() = environmentOffset.x
//    val xEndView get() = environmentOffset.x + screenSize.width
//
//    val yStartView get() = environmentOffset.y
//    val yEndView get() = environmentOffset.y + screenSize.height


    val position
        get() = -environmentOffset
    val offset
        get() = environmentOffset - halfScreenOffset

    val scaledOffset
        get() = environmentOffset * scale

    val scaledPosition
        get() = -environmentOffset * scale

}


data class Viewport(
    val worldOffset: Offset,
    val screenSize: Size,
    val scale: Float,
) {

    fun mousePositionOnWorld(mousePositionOnBorder: Offset): Offset {
        val halfScreen = Offset(screenSize.width, screenSize.height) / 2f

        val dividedMousePosition = (mousePositionOnBorder - halfScreen) / scale


        return (dividedMousePosition - worldOffset + halfScreen)
    }

    inline val size get() = screenSize

    val centerOffset: Offset
        get() =
            (size / 2f).let { Offset(it.width, it.height) }

    val position
        get() = -worldOffset

}



