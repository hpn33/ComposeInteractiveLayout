package hpn.common

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import hpn.common.layout.*
import kotlin.random.Random


@Composable
fun App() {

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
            ) { Card(index.toString()   ) }


            if (items.isNotEmpty()) {
                connections.add(GraphItemConnection(items.last(), item))
            }

            items.add(item)

        }
    }


    GraphView(

        onViewClick = { position: Offset, viewState ->

            val worldPosition = viewState.viewport.mousePositionOnWorld(position )

            // TODO: dont need to apply scale to position
            val item = GraphItem(
                position = worldPosition
//                    / viewState.scale
            ) { Card(items.size.toString()) }


            if (items.isNotEmpty()) {
                connections.add(GraphItemConnection(items.last(), item))
            }

            items.add(item)


        }

    ) {

        connections(connections)

        items(items)


//        val a = item(x = 100f, y = 100f) { Card() }
//        val b = item(x = 300f, y = 300f) { Card() }
//
//
//
//        a connectTo item(x = -100f, y = -100f) { Card() }
//        a connectTo b


    }
}

