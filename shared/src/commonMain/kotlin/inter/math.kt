package inter

import androidx.compose.ui.geometry.Offset
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt


fun climb(value: Float, min: Float, max: Float): Float {

    if (value < min) return min
    if (value > max) return max

    return value
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
