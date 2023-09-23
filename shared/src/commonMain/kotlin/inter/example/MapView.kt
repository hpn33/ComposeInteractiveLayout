package inter.example

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import inter.InteractiveBox
import inter.rememberInteractiveState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MapView() {


    val interactiveController = rememberInteractiveState()
    var selectedStateIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(selectedStateIndex) {
        if (selectedStateIndex != -1) {
            interactiveController.apply {
                val halfScreenSize = interactiveController.screenSize.value / 2

                scale.value = 1.8f
                offsetX.value = 1470f - halfScreenSize.width
                offsetY.value = 1745f - halfScreenSize.height
            }
        } else {
            interactiveController.apply {
                scale.value = 1f
            }
        }
    }

    val alphaAnimation by animateFloatAsState(
        (interactiveController.scale.value)
            .let { if (it >= 1f) ((it / 10f) - .1f - 1f) * -1 else 1f }
            .let { if (it >= 1f) it else .1f }
    )



    Box(
        modifier = Modifier
            .background(Color.Gray)
            .padding(50.dp)
            .background(Color.White)
    ) {


//        rememberScrollState()


        InteractiveBox(interactiveController) {


            Box(
                modifier = Modifier
//                    .scrollable()
//                    .wrapContentSize(Alignment.TopStart, unbounded = true)
                    .background(Color.LightGray),
//                    .size((500 * it.scale).dp)
//                    .scale(it.scale),
//                contentAlignment = Alignment.TopStart
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
                        .alpha(alphaAnimation)
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