package hpn.common.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hpn.common.InteractiveBox
import hpn.common.rememberInteractiveState

@Composable
fun MapView() {


    Box(
        modifier = Modifier
            .background(Color.Gray)
            .padding(50.dp)
            .background(Color.White)
    ) {

        val interactiveController = rememberInteractiveState()
        var selectedStateIndex by remember { mutableStateOf(-1) }

        println(selectedStateIndex)
        LaunchedEffect(selectedStateIndex) {
            println("here")
            if (selectedStateIndex != -1) {
                interactiveController.apply {
                    scale.value = 1.1f
                    offsetX.value = 1422f
                    offsetY.value = 1702f
                }
            } else {
                interactiveController.apply {
                    scale.value = 1f
                }
            }
        }

//        rememberScrollState()


        InteractiveBox(
            interactiveController
        ) {


            Box(
                modifier = Modifier
//                    .scrollable()
                    .wrapContentSize(Alignment.TopStart, unbounded = true)
                    .background(Color.LightGray)
//                    .size((500 * it.scale).dp)
                    .scale(it.scale),
                contentAlignment = Alignment.TopStart
            ) {

                // 1 2 10
                // .1 .2 1
                // -.9
                // 1 .9 .1
//                println(it.scale)
//                println((it.scale).let { if (it >= 1f) ((it / 10f) - .1f - 1f) * -1 else 1f })

                Image(
                    painterResource("Iran_location_map.png"),
                    contentDescription = null,
                    modifier = Modifier
//                        .size((500 * it.scale).dp)
                        .alpha(
                            (it.scale)
                                .let { if (it >= 1f) ((it / 10f) - .1f - 1f) * -1 else 1f }
                                .let { if (it >= 1f) it else .1f }
                        )
                )

//tehran.png
                Box(
                    modifier = Modifier
                        .alpha(if (selectedStateIndex in listOf(-1, 0)) 1f else 0f)
//                        .offset((1100 * it.scale).dp, (1510 * it.scale).dp)
                        .offset((1100).dp, (1510).dp),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painterResource("hormozgan.png"),
                        contentDescription = null,
                    )

                    Box(
                        Modifier
                            .clip(RoundedCornerShape(100))
                            .clickable {
                                println("Click")
                                selectedStateIndex = if (selectedStateIndex == 0) -1 else 0

                            }
                            .background(Color.Black.copy(alpha = .5f))
                            .offset(25.dp, 25.dp)
                            .size(50.dp)
                    )

                }

            }


        }

    }


}