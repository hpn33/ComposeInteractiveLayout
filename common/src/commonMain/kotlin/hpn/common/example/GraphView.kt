package hpn.common.layout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.times
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import hpn.common.climb
import hpn.common.onMouseScroll
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt


/*

# target
- [ ] simulate geometry
    - [ ] world
    - [ ] camera
- [ ] something like visual script
- [ ] optimize

# Ability
- [x] move
- [ ] zoom
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

@Composable
inline fun rememberGraphItemConnections() = remember { mutableStateListOf<GraphItemConnection>() }


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
        environmentOffset = Offset(-offsetXAnimate, -offsetYAnimate),
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
                        offsetX -= pan.x / scale
                        offsetY -= pan.y / scale
                    }
                }
                .onMouseScroll { scrollDelta ->
//                 Zoom with mouse scroll
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

        // Root Layout
        Box(modifier = Modifier.fillMaxSize()) {
            viewportLayout(viewState) {
                worldBaseLayout(viewState) {

                    graphContentLayer(viewState) {

                        content()

                    }


//                 ViewPort Position
                    Box(
                        modifier = Modifier
                            .offset(
                                (viewState.viewport.position.x
                                        * viewState.scale
                                        ).dp,
                                (viewState.viewport.position.y
                                        * viewState.scale
                                        ).dp
                            )
//                        .scale(viewState.viewport.scale)
//                        .size(5.dp, 5.dp)
                            .background(Color.Red.copy(alpha = .1f))
                    ) {
                        Column {
                            Text("Viewport")
                            Text((viewState.viewport.position).toString())
//                        Text((-viewState.viewport.position / viewState.viewport.scale).toString())
                        }

                    }

//                 View : FIX
//                Box(
//                    modifier = Modifier
//                        .offset(
//                            (-viewState.viewport.centerViewPosition.x).dp,
//                            (-viewState.viewport.centerViewPosition.y).dp
//                        )
//                        .size(
//                            (viewState.viewport.sizeWithScale.width).dp,
//                            (viewState.viewport.sizeWithScale.height).dp
//                        )
//                        .border(1.dp, Color.Black)
//                        .scale(viewState.scale),
//                    contentAlignment = Alignment.Center
//                ) {
//
//                    Box(
//                        modifier = Modifier
//                            .background(Color.Black)
//                            .offset(50.dp, 1.dp)
//                            .size(100.dp, 2.dp)
//                    )
//                    Box(
//                        modifier = Modifier
//                            .background(Color.Black)
//                            .offset(1.dp, 50.dp)
//                            .size(2.dp, 100.dp)
//                    )
//
//                }


                    // MOUSE
                    val mouse = viewState.viewport.mousePositionOnWorld(mousePositionOnBorder)
//                    val mouse = viewState.viewport.mousePositionOnWorld(Offset.Zero)

                    Box(
                        modifier = Modifier
                            .offset(
                                (mouse.x * viewState.scale).dp,
                                (mouse.y * viewState.scale).dp
                            )
                    ) {
                        Column {

                            Text((mouse).toString())

                        }
                    }

                    Box(modifier = Modifier.background(Color.Yellow.copy(alpha = .1f))) {
                        Text("World")
                    }


                }
            }
        }

        // center of screen view
        Box(
            modifier = Modifier
//                .offset(
//                    ((viewState.screenSize.width - viewState.viewport.sizeWithScale.width) / 2f).dp,
//                    ((viewState.screenSize.height - viewState.viewport.sizeWithScale.height) / 2f).dp
//                )
//                .size(
//                    (viewState.viewport.sizeWithScale.width).dp,
//                    (viewState.viewport.sizeWithScale.height).dp
//                )
                .offset(
                    ((viewState.screenSize.width - (viewState.viewport.size.width * viewState.scale)) / 2f).dp,
                    ((viewState.screenSize.height - (viewState.viewport.size.height * viewState.scale)) / 2f).dp
                )
                .size(
                    (viewState.viewport.size.width * viewState.scale).dp,
                    (viewState.viewport.size.height * viewState.scale).dp
                )
                .border(1.dp, Color.Black)
//            ,
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


        // Control Panel Layout
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd,
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                IconButton(onClick = { scale += 0.25f }) {
//                    Icon(Icons.Default.Add, contentDescription = null)
//                }
//
//                IconButton(onClick = { scale -= 0.25f }) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = null)
//                }

//                Divider(modifier = Modifier.size(width = 35.dp, height = 2.dp))

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
fun viewportLayout(viewState: ViewState, function: @Composable () -> Unit) {

    val offset = viewState.viewport.centerOffset
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(offset.x.dp, offset.y.dp)
    ) {
        function()
    }
}

fun Offset.distanceTo(target: Offset) =
    sqrt(
        (target.x - this.x).pow(2f) + (target.y - this.y).pow(2f)
    )

fun Offset.angleTo(target: Offset): Double {
    // calculate the angle theta from the deltaY and deltaX values
    // (atan2 returns radians values from [-PI,PI])
    // 0 currently points EAST.
    // NOTE: By preserving Y and X param order to atan2,  we are expecting
    // a CLOCKWISE angle direction.
    // calculate the angle theta from the deltaY and deltaX values
    // (atan2 returns radians values from [-PI,PI])
    // 0 currently points EAST.
    // NOTE: By preserving Y and X param order to atan2,  we are expecting
    // a CLOCKWISE angle direction.
    var theta = atan2(target.y - y, target.x - x).toDouble()

    // rotate the theta angle clockwise by 90 degrees
    // (this makes 0 point NORTH)
    // NOTE: adding to an angle rotates it clockwise.
    // subtracting would rotate it counter-clockwise

    // rotate the theta angle clockwise by 90 degrees
    // (this makes 0 point NORTH)
    // NOTE: adding to an angle rotates it clockwise.
    // subtracting would rotate it counter-clockwise
    theta += Math.PI / 2.0

    // convert from radians to degrees
    // this will give you an angle from [0->270],[-180,0]

    // convert from radians to degrees
    // this will give you an angle from [0->270],[-180,0]
    var angle = Math.toDegrees(theta)

    // convert to positive range [0-360)
    // since we want to prevent negative angles, adjust them now.
    // we can assume that atan2 will not return a negative value
    // greater than one partial rotation

    // convert to positive range [0-360)
    // since we want to prevent negative angles, adjust them now.
    // we can assume that atan2 will not return a negative value
    // greater than one partial rotation
    if (angle < 0) {
        angle += 360.0
    }

    return angle
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
inline fun worldBaseLayout(viewState: ViewState, content: @Composable () -> Unit) {


    val position = -viewState.viewport.position * viewState.viewport.scale

    // draw Layer
    Box(
        Modifier.offset(position.x.dp, position.y.dp)
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


data class ViewState(
    val environmentOffset: Offset,
    var screenSize: IntSize,
    val scale: Float,
) {
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

    val viewport = Viewport(
        environmentOffset,
        screenSize.toSize(),
        scale
    )

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


    val viewYRange get() = (environmentOffset.y..(screenSize.height + environmentOffset.y))
    val viewYRangeInt get() = ((environmentOffset.y - halfScreenY).toInt()..(screenSize.height - halfScreenY + environmentOffset.y).toInt())
    val viewXRange get() = (environmentOffset.x..(screenSize.width + environmentOffset.x))
    val viewXRangeInt get() = ((environmentOffset.x - halfScreenX).toInt()..(screenSize.width - halfScreenX + environmentOffset.x).toInt())


    val xStartView get() = environmentOffset.x
    val xEndView get() = environmentOffset.x + screenSize.width

    val yStartView get() = environmentOffset.y
    val yEndView get() = environmentOffset.y + screenSize.height

}

data class Viewport(
    val worldOffset: Offset,
    val screenSize: Size,
    val scale: Float,
) {

    fun mousePositionOnWorld(mousePositionOnBorder: Offset): Offset {
        val halfScreen = Offset(screenSize.width, screenSize.height) * 0.5f

        val fixedMousePosition = (mousePositionOnBorder - halfScreen) / scale

        return (fixedMousePosition - worldOffset)
    }

//    val offset
//        get() = (screenSize / 2f)

//    val sizeWithScale
//        get() = screenSize * scale

    inline val size get() = screenSize

    val centerViewPosition: Offset
        get() = position - offset

    val offset
        get() = centerOffset

    val centerOffset: Offset
        get() =
//            Offset.Zero
            (size / 2f).let { Offset(it.width, it.height) }
//            (sizeWithScale / 2f).let { Offset(it.width, it.height) }


    val position
        get() = -worldOffset

    val positionWithOffset: Offset
        get() {

//            val offset = offset.let { Offset(it.width, it.height) }
            val offset = offset.let { Offset(it.x, it.y) }

            return (position - offset)
        }

//    val sizeWithoutScale
//        get() = screenSize * scale


}


class GraphContentScope(val viewState: ViewState) {

    @Composable
    inline infix fun GraphItem.connectTo(target: GraphItem) {
        connection(GraphItemConnection(this, target))
    }

    @Composable
    inline fun connection(connectionItem: GraphItemConnection): GraphItemConnection {


        val scale = viewState.scale
//        val (start, target) =
        val start = connectionItem.start.position * scale
        val target = connectionItem.target.position * scale

        val point = Offset(
            if (start.x < target.x) start.x else target.x,
            if (start.y < target.y) start.y else target.y,
        )

        val size = (target - start).let { Size(it.x.absoluteValue, it.y.absoluteValue) }

        // Start
//        Box(
//            modifier = Modifier
//                .offset(point.x.dp, point.y.dp)
//                .size(10.dp)
//                .background(Color.Red)
//
//
//        ) { }

        // End
//        Box(
//            modifier = Modifier
//                .offset((point.x + size.width).dp, (point.y + size.height).dp)
//                .size(10.dp)
//                .background(Color.Red)
//
//
//        ) { }


        val lineStart = start.let { Offset(it.x - point.x, it.y - point.y) }
        val lineEnd = target.let { Offset(it.x - point.x, it.y - point.y) }

//        println(lineStart)
//        println(lineEnd)

        Canvas(
            modifier = Modifier
                .offset(point.x.dp, point.y.dp)
                .size(size.width.dp, size.height.dp)
        ) {

            drawLine(
                start = lineStart,
                end = lineEnd,
                color = Color.Gray,
            )

        }

//        Box(
//            modifier = Modifier
//                .offset(point.x.dp, point.y.dp)
//                .size(size.width.dp, size.height.dp)
//                .background(Color.Yellow.copy(alpha = 0.1f))
//
//
//        ) { }


        return connectionItem

    }


    @Composable
    inline fun item(position: Offset, noinline content: @Composable () -> Unit): GraphItem {

        return item(
            item = GraphItem(position, content)
        )

    }

    @Composable
    inline fun item(x: Float, y: Float, noinline content: @Composable () -> Unit): GraphItem {

        return item(
            position = Offset(x, y),
            content = content
        )

    }

    @Composable
    inline fun item(item: GraphItem): GraphItem {

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

//                .background(Color.Gray.copy(alpha = .5f))
                .offset(
                    x = (item.position.x * viewState.scale).dp,
                    y = (item.position.y * viewState.scale).dp
                ),
//                .background(Color.Gray.copy(alpha = .5f))
//                .offset(
//                    x = (item.position.x).dp,
//                    y = (item.position.y).dp
//                )
//                .size(10.dp),
//            contentAlignment = Alignment.TopStart
        )
        {

            Box(
                modifier = Modifier
//                    .background(Color.Gray.copy(alpha = .5f))
//                    .scale(viewState.scale),
//                    .background(Color.Gray.copy(alpha = .5f)),
//                contentAlignment = Alignment.TopStart
            ) {
                item.content()

            }

        }

        return item

    }

    @Composable
    inline fun items(items: List<GraphItem>): List<GraphItem> {
        for (item in items) {
            item(item)
        }

        return items
    }


    @Composable
    inline fun connections(connections: List<GraphItemConnection>): List<GraphItemConnection> {
        for (connection in connections) {
            connection.start connectTo (connection.target)
        }

        return connections
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
data class GraphItemConnection(val start: GraphItem, val target: GraphItem)

@Composable
inline fun Card(title: String = "Card") {
    Surface(
        elevation = 2.dp,
        shape = RoundedCornerShape(10)
    ) {
        Text(
            title,
            modifier = Modifier.padding(5.dp)
        )
    }
}


