package kr.ac.tukorea.ge.spgp2026.tudefence.game.common

interface IRadiusCollidable {
    val x: Float
    val y: Float
    val radius: Float
}

fun IRadiusCollidable.collides(other: IRadiusCollidable) : Boolean {
    val dx = x - other.x
    val dy = y - other.y
    val distanceSq = dx * dx + dy * dy
    val radiusSum = radius + other.radius
    return distanceSq <= radiusSum * radiusSum
}

