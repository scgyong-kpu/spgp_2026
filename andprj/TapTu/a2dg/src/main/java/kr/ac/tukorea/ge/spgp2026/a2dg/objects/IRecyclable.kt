package kr.ac.tukorea.ge.spgp2026.a2dg.objects

/**
 * World 가 "이 객체는 삭제할 때 버리지 말고 recycle bin 에 넣어도 된다"는 사실을
 * 알기 위해 붙이는 아주 작은 계약이다.
 *
 * 재활용 패턴의 목표는 Bullet, Enemy 처럼 자주 생성되고 자주 삭제되는 객체를
 * 매번 새로 만들지 않고 다시 쓰는 것이다.
 *
 * 이번 a2dg 에서는 다음 순서를 염두에 두고 이 인터페이스를 먼저 만든다.
 * 1. World.remove() 가 IRecyclable 인 객체를 recycle bin 으로 수거한다.
 * 2. World.obtain(SomeClass::class.java) 가 bin 에 있으면 꺼내고, 없으면 null 을 돌려준다.
 * 3. 객체의 companion object 나 factory 함수가 "재활용 객체를 쓸지 새로 만들지"를 결정한다.
 * 4. 마지막으로 init(...) 으로 필요한 상태만 다시 채운다.
 *
 * 여기서 중요한 점은 "World 는 recycle bin 조회만 맡고, 새 생성 여부는 객체 쪽 factory 가 결정한다"는 것이다.
 * 예를 들어 Bullet 을 쏠 때는:
 *
 * val bullet = world.obtain(Bullet::class.java) ?: Bullet(gctx)
 * bullet.init(gctx, x, y, power)
 *
 * 같은 흐름을 목표로 한다.
 *
 * 왜 factory lambda 대신 Class 를 넘기는 방식을 먼저 생각하느냐:
 * - 만일 obtain() 을 다음처럼 factory lambda 로 만들었다고 가정해 보자.
 *
 *   fun <T : IRecyclable> obtain(factory: () -> T): T
 *
 *   val bullet = world.obtain { Bullet(gctx) }
 *   bullet.init(x, y, power)
 *
 * - 이 방식은 문법상 더 Kotlin 답게 보일 수 있다.
 * - 하지만 "만일" inline 처리를 빼먹거나, lambda 가 바깥 값을 캡처하는 방식으로 남아 있으면
 *   작은 함수 객체라도 만들어질 수 있다.
 * - 이번 재활용 단계의 목표는 그런 중간 객체 생성 가능성까지 최대한 줄이는 것이다.
 * - 그래서 obtain() 은 일단 Class 를 key 로 recycle bin 에서 꺼내기만 하고,
 *   새로 만드는 코드는 Bullet(gctx), Enemy(gctx) 처럼 각 타입의 factory 에 명시적으로 남겨 둔다.
 *
 * onRecycle() 은 객체가 recycle bin 에 들어가기 직전에 마지막 정리를 할 기회를 준다.
 * 예를 들어:
 * - 남아 있는 참조를 끊거나
 * - 일회성 상태를 초기값으로 되돌리거나
 * - 다음 init(...) 전에 반드시 비워야 하는 값을 정리할 때
 * 사용할 수 있다.
 *
 * 아무 정리도 필요 없다면 빈 구현이면 된다.
 */
interface IRecyclable {
    fun onRecycle()
}
