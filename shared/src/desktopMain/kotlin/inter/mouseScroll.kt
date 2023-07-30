package inter

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.onMouseScroll(action: (Float) -> Unit) =
    onPointerEvent(PointerEventType.Scroll) {
        action(it.changes.first().scrollDelta.y)
    }
