package kr.ac.tukorea.ge.spgp2026.cookierun.game.map

import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

fun interface MapObjectCreator {
    fun create(gctx: GameContext, tile: Char, left: Float, top: Float): MapObject?
}

// MapObjectRegistry 는 stage 파일의 문자 하나를 실제 MapObject 로 바꾸는 표이다.
// MapLoader 가 'O', '1', '@' 같은 문자의 의미를 모두 직접 알게 하면
// 새 맵 오브젝트가 추가될 때마다 MapLoader 가 계속 커진다.
// 그래서 문자별 생성 규칙은 Registry 에 등록해 두고,
// MapLoader 는 Registry 에 "이 문자로 만들 수 있는 객체가 있니?"라고만 묻는다.
object MapObjectRegistry {
    // fun interface 는 추상 함수가 하나뿐인 인터페이스이다.
    // Java 의 SAM interface 처럼 람다로 구현할 수 있으면서도,
    // typealias 보다 "MapObjectCreator" 라는 역할 이름이 코드에 분명히 남는다.
    //
    // typealias 로도 아래처럼 쓸 수 있다.
    // typealias MapObjectCreator = (GameContext, Char, Float, Float) -> MapObject?
    // 하지만 수업 코드에서는 이름 있는 fun interface 가 의도를 설명하기 더 좋다.
    private val creators = mutableMapOf<Char, MapObjectCreator>()

    // 문자 하나가 곧바로 한 생성 규칙에 대응되는 경우에 사용한다.
    // 예: 'O' 는 긴 Floor, '@' 는 Magnification JellyItem
    fun register(ch: Char, creator: MapObjectCreator) {
        creators[ch] = creator
    }

    // 같은 생성 규칙이 여러 문자에 대응될 때 사용한다.
    // 예: '1'..'9' 은 모두 JellyItem 이지만 tile 값에 따라 image index 만 달라진다.
    // 범위 등록은 여기서 여러 key 로 펼쳐 넣으므로,
    // 실제 create() 시점에는 Char 하나로 바로 creator 를 찾을 수 있다.
    fun register(chars: CharRange, creator: MapObjectCreator) {
        for (ch in chars) {
            creators[ch] = creator
        }
    }

    // 등록되지 않은 문자는 null 을 반환한다.
    // MapLoader 는 null 이면 그 칸에는 아무것도 만들지 않고 넘어간다.
    fun create(gctx: GameContext, tile: Char, left: Float, top: Float): MapObject? {
        return creators[tile]?.create(gctx, tile, left, top)
    }
}

