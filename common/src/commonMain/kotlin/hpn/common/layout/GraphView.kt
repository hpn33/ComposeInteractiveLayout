package hpn.common.layout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import hpn.common.climb
import hpn.common.onMouseScroll


/*

# target
- [ ] simulate geometry
    - [ ] world
    - [ ] camera
- [ ] something like visual script
- [ ] optimize

# Ability
- [x] move
- [x] zoom
    - [ ] zoom to center of screen
- [ ] mouse position ( by zoom )
- [x] add item by position
    - [ ] scale

# draw
- [x] guild line background
- [ ] positioned item

- [ ] offset to camera ( be center )

# layers controller
    - [x] box/border
        - [ ] background
        - [x] world
    - [ ] camera?!

# todo
- [ ] calculate from center of camera
    - [ ] background
    - [ ] border
    - [x] mouse
        - click point position
    - [ ] zoom

 */


const val RepeatOffset = 50

@Composable
inline fun rememberGraphItems() = remember { mutableStateListOf<GraphItem>() }


@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun GraphView(
    onViewClick: (position: Offset, ViewState) -> Unit,
    content: @Composable() (GraphContentScope.() -> Unit) = {},
) {

    var mousePositionOnBorder by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var screenSize by remember { mutableStateOf(IntSize(1, 1)) }


    val scaleAnimate by animateFloatAsState(scale)
    val offsetXAnimate by animateFloatAsState(offsetX)
    val offsetYAnimate by animateFloatAsState(offsetY)

    // Center of World:: Top/Right
    val viewState = ViewState(
        worldOffset = Offset(-offsetXAnimate, -offsetYAnimate),
        screenSize = screenSize,
        scale = scaleAnimate
    )

//    println(viewState)


    // Box/Border Layout
    boxBorder(sizeChange = { screenSize = it }) {


        // background guild line
        // TODO: REMOVE Flicking, make exact
//        Box(modifier = Modifier.fillMaxSize()) {
//
//
//            val offsetEdge = (RepeatOffset * scaleAnimate).toInt()
//            val guildLineDraw = offsetEdge * 10
//
//            val alpha =
//                if (viewState.showScale > 1)
//                    1f
//                else
//                    (if (viewState.showScale <= .2f) 0f else viewState.showScale)
//
//
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .drawBehind {
//
//                        val viewScope = viewState.viewYRangeInt
//
//                        for (yIndex in viewScope) {
//
//                            if (yIndex % offsetEdge != 0) {
//                                continue
//                            }
//
//                            val y = yIndex.toFloat() - viewScope.start
//
//
//                            drawLine(
//                                Color.LightGray.copy(alpha = alpha),
//                                start = Offset(x = 0f, y = y),
//                                end = Offset(x = viewState.screenSize.width.toFloat(), y = y),
//                            )
//                        }
//                    }
//            )
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .drawBehind {
//
//                        val viewScope = viewState.viewXRangeInt
//
//                        for (xIndex in viewScope) {
//
//                            if (xIndex % offsetEdge != 0) {
//                                continue
//                            }
//
//                            val x = xIndex.toFloat() - viewScope.start
//
//                            drawLine(
//                                Color.LightGray.copy(alpha = alpha),
//                                start = Offset(y = 0f, x = x),
//                                end = Offset(y = viewState.screenSize.width.toFloat(), x = x),
//                            )
//                        }
//                    }
//            )
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .drawBehind {
//
//                        val viewScope = viewState.viewYRangeInt
//
//                        for (yIndex in viewScope) {
//
//                            if (yIndex % guildLineDraw != 0) {
//                                continue
//                            }
//
//                            val y = yIndex.toFloat() - viewScope.start
//
//                            drawLine(
//                                Color.Gray,
//                                start = Offset(x = 0f, y = y),
//                                end = Offset(x = viewState.screenSize.width.toFloat(), y = y),
//                            )
//                        }
//                    }
//            )
//
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .drawBehind {
//
//                        val viewScope = viewState.viewXRangeInt // with offset
//
//                        for (xIndex in viewScope) {
//
//                            if (xIndex % guildLineDraw != 0) {
//                                continue
//                            }
//
//                            val x = xIndex.toFloat() - viewScope.start
//
//                            drawLine(
//                                Color.Gray,
//                                start = Offset(y = 0f, x = x),
//                                end = Offset(y = viewState.screenSize.width.toFloat(), x = x),
//                            )
//                        }
//                    }
//            )
//
//        }

        // Touch Control Layout
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Zoom
                        scale *= zoom
                        // Move
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
                .onMouseScroll { scrollDelta ->
                    // Zoom with mouse scroll
                    scale *= 1f - (scrollDelta / 10f)

                    scale = climb(value = scale, min = 0.1f, max = 10f)

                }
                .onPointerEvent(PointerEventType.Move) {
                    mousePositionOnBorder = it.changes.first().position
                }
                .onClick {
                    onViewClick(mousePositionOnBorder, viewState)
                }
                .fillMaxSize()
        )

        // Content Layout
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .scale(scaleAnimate)
        ) {

            drawLayout(viewState) {
                graphContentLayer(viewState) {

                    content()

                }

                // Center to Center of Camera
                Box(
                    modifier = Modifier
                        .offset(
                            viewState.viewport.position.x.let { if (it < 0) it else 0f }.dp,
                            viewState.viewport.position.y.let { if (it < 0) it else 0f }.dp
                        )
//                        .scale(viewState.viewport.scale)
                        .size(
                            viewState.viewport.position.x.let { if (it < 0f) it * -1 else it }.dp,
                            viewState.viewport.position.y.let { if (it < 0f) it * -1 else it }.dp
                        )
                        .background(Color.Red.copy(alpha = 0.1f))
                )

                // View
                Box(
                    modifier = Modifier
                        .offset(
                            (viewState.viewport.position.x).dp,
                            (viewState.viewport.position.y).dp
                        )
//                        .scale(viewState.viewport.scale)
//                        .size(5.dp, 5.dp)
                        .background(Color.Red)
                ) {
                    Column {
//                        Text(viewState.viewport.position.toString())
                        Text((viewState.viewport.position / viewState.scale).toString())
                    }

                }

                // View
                Box(
                    modifier = Modifier
                        .offset(
                            (viewState.viewport.positionWithOffset.x).dp,
                            (viewState.viewport.positionWithOffset.y).dp
                        )
                        .size(
                            (viewState.viewport.size.width).dp,
                            (viewState.viewport.size.height).dp
                        )
                        .border(1.dp, Color.Black)
                        .scale(viewState.scale),
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

                }


                // MOUSE
                val mouse = viewState.mousePositionOnWorld(mousePositionOnBorder)

                Box(
                    modifier = Modifier
                        .offset(
                            (mouse.x).dp,
                            (mouse.y).dp
                        )
                ) {

                    Column {

//                        Text((mouse ).toString())
                        Text((mouse / viewState.scale).toString())
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
                IconButton(onClick = { scale += 0.25f }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }

                IconButton(onClick = { scale -= 0.25f }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }

                Divider(modifier = Modifier.size(width = 35.dp, height = 2.dp))

                IconButton(onClick = {
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f
                }) {
                    Icon(Icons.Default.Home, contentDescription = null)
                }

                Text(
                    "scale \n" + viewState.scale,
                    textAlign = TextAlign.Center
                )
                Text(
                    "size \n" + viewState.screenSize,
                    textAlign = TextAlign.Center
                )
            }

        }
    }


}

@Composable
inline fun boxBorder(
    crossinline sizeChange: (IntSize) -> Unit,
    function: @Composable () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RectangleShape)
            .onGloballyPositioned { layoutCoordinates ->
                sizeChange(layoutCoordinates.size)
            }
    ) {
        function()
    }
}

@Composable
inline fun drawLayout(viewState: ViewState, content: @Composable () -> Unit) {

    // draw Layer
    Box(
        Modifier
            // TODO: set by viewport position
            .offset(
                -viewState.worldOffset.x.dp,
                -viewState.worldOffset.y.dp
            )
    ) {

        content()

    }
}

//class CameraTemp(position: Offset, offset: Offset, zoom: Float) {
//
//}
//
//class ScreenTemp(val centerWorldOffset: Offset, var screenSize: IntSize) {
//
//}


data class ViewState(val worldOffset: Offset, var screenSize: IntSize, val scale: Float) {
//    inline fun positionOnScreenWithScale(mousePositionOnBorder: Offset): Offset {
//
//        val screenOffsetWithScale = Offset(screenXOffsetWithScale, screenYOffsetWithScale)
//
//        val offsetHalfScreenWithScale = Offset((halfScreenX * showScale), (halfScreenY * showScale))
//
//        return mousePositionOnBorder
//            .times(showScale)
//            .plus(screenOffsetWithScale)
//            .minus(offsetHalfScreenWithScale)
//    }

    // TODO: work with scale
    fun mousePositionOnWorld(mousePositionOnBorder: Offset) =
        (mousePositionOnBorder + worldOffset)

    val viewport = Viewport(worldOffset, screenSize.toSize(), scale)

//    val screenXOffsetWithScale
//        get() = (screenSize.width - screenWidthWithScale) / 2
//    val screenYOffsetWithScale
//        get() = (screenSize.height - screenHeightWithScale) / 2


//    val screenWidthWithScale
//        get() = screenSize.width * scale
//    val screenHeightWithScale
//        get() = screenSize.height * scale

//    val screen = ScreenTemp(centerWorldOffset, screenSize)
//    val camera = CameraTemp(centerWorldOffset, Offset.Zero, showScale)

//    val worldOffsetX get() = -worldOffset.x

    //    + halfScreenX
//    val worldOffsetY get() = -worldOffset.y
//    + halfScreenY

    val halfScreenX get() = screenSize.width / 2
    val halfScreenY get() = screenSize.height / 2


    val viewYRange get() = (worldOffset.y..(screenSize.height + worldOffset.y))
    val viewYRangeInt get() = ((worldOffset.y - halfScreenY).toInt()..(screenSize.height - halfScreenY + worldOffset.y).toInt())
    val viewXRange get() = (worldOffset.x..(screenSize.width + worldOffset.x))
    val viewXRangeInt get() = ((worldOffset.x - halfScreenX).toInt()..(screenSize.width - halfScreenX + worldOffset.x).toInt())


    val xStartView get() = worldOffset.x
    val xEndView get() = worldOffset.x + screenSize.width

    val yStartView get() = worldOffset.y
    val yEndView get() = worldOffset.y + screenSize.height

}

class Viewport(val worldOffset: Offset, val screenSize: Size, val scale: Float) {

    //    var offset: Offset
    val position: Offset
        get() {

            val halfSize = (screenSize) / 2f

            return Offset(worldOffset.x + halfSize.width, worldOffset.y + halfSize.height)

        }

    val positionWithOffset: Offset
        get() {

            val size = if (scale < 1f) screenSize * scale else screenSize

            val halfSize = (size / 2f).let { Offset(it.width, it.height) }

            return (position - halfSize)


        }

    val size
        get() = screenSize * scale


}


class GraphContentScope(val viewState: ViewState) {


    @Composable
    inline fun item(position: Offset, noinline content: @Composable () -> Unit) {

        item(
            item = GraphItem(position, content)
        )

    }

    @Composable
    inline fun item(x: Float, y: Float, noinline content: @Composable () -> Unit) {

        item(
            position = Offset(x, y),
            content = content
        )

    }

    @Composable
    inline fun item(item: GraphItem) {

        Box(
            modifier = Modifier
//                .offset(
//                    x = ((item.position.x - viewState.position.x) * viewState.zoom).dp,
//                    y = ((item.position.y - viewState.position.y) * viewState.zoom).dp
//                )
//                .offset(
//                    x = ((item.position.x * viewState.showScale) - viewState.centerWorldOffset.x + viewState.halfScreenX).dp,
//                    y = ((item.position.y * viewState.showScale) - viewState.centerWorldOffset.y + viewState.halfScreenY).dp
//                )

                .offset(
                    x = (item.position.x * viewState.scale).dp,
                    y = (item.position.y * viewState.scale).dp
                )
//                .offset(
//                    x = (item.position.x).dp,
//                    y = (item.position.y).dp
//                )
                .scale(viewState.scale)
                .size(10.dp)
                .background(Color.Gray)
        )
//        {
//            item.content()
//        }

    }

    @Composable
    inline fun items(items: List<GraphItem>) {
        for (item in items) {
            item(item)
        }
    }


}

@Composable
fun graphContentLayer(
    viewState: ViewState,
    function: @Composable GraphContentScope.() -> Unit,
) {

    GraphContentScope(viewState).function()

}


data class GraphItem(val position: Offset, val content: @Composable () -> Unit)

@Composable
inline fun Card() {
    Surface(
        elevation = 2.dp,
        shape = RoundedCornerShape(10)
    ) {
        Text(
            "Card",
            modifier = Modifier.padding(5.dp)
        )
    }
}


