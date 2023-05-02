package hpn.common


fun climb(value: Float, min: Float, max: Float): Float {

    if (value < min) return min
    if (value > max) return max

    return value
}

