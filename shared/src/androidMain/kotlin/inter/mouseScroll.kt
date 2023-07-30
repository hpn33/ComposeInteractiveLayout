package inter

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.onMouseScroll(action: (Float) -> Unit) =
    this
