package inter.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import inter.InteractiveBox
import inter.ViewState
import kotlin.math.absoluteValue
import kotlin.random.Random


@Composable
fun GraphView() {

    val items = rememberGraphItems()
    val connections = rememberGraphItemConnections()

    LaunchedEffect(Unit) {
        for (index in 1 until 20) {

            val isNegative1 = if (Random.nextBoolean()) 1 else -1
            val isNegative2 = if (Random.nextBoolean()) 1 else -1

            val item = GraphItem(
                position = Offset(
                    x = isNegative1 * Random.nextInt(index * 50).toFloat(),
                    y = isNegative2 * Random.nextInt(index * 50).toFloat()
                )
            ) {
                Card(index.toString())
            }


            if (items.isNotEmpty()) {
                connections.add(GraphItemConnection(items.last(), item))
            }

            items.add(item)

        }
    }


    InteractiveBox(
        onViewClick = { position: Offset, viewState ->

            val worldPosition = viewState.viewport.mousePositionOnWorld(position)

            val item = GraphItem(position = worldPosition) {
                Card(items.size.toString())
            }


            if (items.isNotEmpty()) {
                connections.add(GraphItemConnection(items.last(), item))
            }

            items.add(item)
        }

    ) {

        graphContentLayer(it) {

            connections(connections)

            items(items)

        }


//        val a = item(x = 100f, y = 100f) { Card() }
//        val b = item(x = 300f, y = 300f) { Card() }
//
//
//
//        a connectTo item(x = -100f, y = -100f) { Card() }
//        a connectTo b


    }
}


@Composable
inline fun rememberGraphItems() = remember { mutableStateListOf<GraphItem>() }

@Composable
inline fun rememberGraphItemConnections() = remember { mutableStateListOf<GraphItemConnection>() }


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

class GraphContentScope(val viewState: ViewState) {

    @Composable
    inline infix fun GraphItem.connectTo(target: GraphItem) {
        connection(GraphItemConnection(this, target))
    }

    @Composable
    inline fun connection(connectionItem: GraphItemConnection): GraphItemConnection {


        val start = connectionItem.start.position
        val target = connectionItem.target.position


        val ltPoint = Offset(
            if (start.x < target.x) start.x else target.x,
            if (start.y < target.y) start.y else target.y,
        )

        val size = (target - start).let { Size(it.x.absoluteValue, it.y.absoluteValue) }

        val halfSize = size / 2f


        val lineStart = (start - ltPoint).let { Offset(it.x, it.y) }
        val lineEnd = (target - ltPoint).let { Offset(it.x, it.y) }


        Canvas(
            modifier = Modifier
                .offset((ltPoint.x + halfSize.width).dp, (ltPoint.y + halfSize.height).dp)
                .size(size.width.dp, size.height.dp)
//                .background(Color.LightGray.copy(.5f))
        ) {

            drawLine(
                start = lineStart,
                end = lineEnd,
                color = Color.Gray,
            )

        }


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
//                .offset(
//                    x = (item.position.x * viewState.scale).dp,
//                    y = (item.position.y * viewState.scale).dp
//                ),
//                .background(Color.Gray.copy(alpha = .5f))
                .offset(
                    x = (item.position.x).dp,
                    y = (item.position.y).dp
                )
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



