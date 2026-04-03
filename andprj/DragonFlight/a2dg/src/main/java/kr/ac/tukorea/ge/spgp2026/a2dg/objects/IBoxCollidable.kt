package kr.ac.tukorea.ge.spgp2026.a2dg.objects

import android.graphics.RectF

interface IBoxCollidable {
    val collisionRect: RectF
}

// 지금 단계에서는 사각형 충돌(AABB)만 먼저 공통화한다.
// 인터페이스를 구현한 두 객체가 서로 겹치는지만 보고 싶을 때
// RectF.intersects(...) 호출을 매번 직접 쓰지 않게 하려는 extension 이다.
fun IBoxCollidable.collidesWith(other: IBoxCollidable): Boolean {
    return RectF.intersects(collisionRect, other.collisionRect)
}
