package hpn.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import hpn.common.layout.Card
import hpn.common.layout.GraphItem
import hpn.common.layout.GraphView
import hpn.common.layout.rememberGraphItems
import kotlin.random.Random


@Composable
fun App() {

    val items = rememberGraphItems()

    LaunchedEffect(Unit) {
        for (index in 1 until 500) {

            val isNegative1 = if (Random.nextBoolean()) 1 else -1
            val isNegative2 = if (Random.nextBoolean()) 1 else -1

            val item = GraphItem(
                position = Offset(
                    x = isNegative1 * Random.nextInt(index).toFloat(),
                    y = isNegative2 * Random.nextInt(index).toFloat()
                )
            ) { Card() }


            items.add(item)
        }
    }


    GraphView(

        onViewClick = { position: Offset ->

            items.add(GraphItem(position = position) { Card() })

        }

    ) {

        items(items)
//        item(x = 100f, y = 100f) { Card() }
//        item(x = 300f, y = 300f) { Card() }

//        items(items)

    }
}

