package inter

import androidx.compose.ui.Modifier

expect fun Modifier.onMouseScroll(action: (Float) -> Unit): Modifier
