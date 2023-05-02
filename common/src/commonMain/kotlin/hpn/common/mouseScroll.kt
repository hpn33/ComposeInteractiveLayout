package hpn.common

import androidx.compose.ui.Modifier

expect fun Modifier.onMouseScroll(action: (Float) -> Unit): Modifier
