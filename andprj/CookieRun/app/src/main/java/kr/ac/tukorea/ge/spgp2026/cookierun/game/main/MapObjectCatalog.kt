package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

object MapObjectCatalog {
    // MapObjectCatalog 는 이 게임에서 등장하는 MapObject 생성 규칙을 한 번에 등록하는 곳이다.
    // MapObjectRegistry 는 단순히 Char -> Creator 표를 보관할 뿐,
    // Floor 나 JellyItem 같은 구체 클래스를 직접 알지 않는다.
    //
    // 반대로 이 Catalog 는 CookieRun 앱의 맵 파일 포맷을 알고 있다.
    // 예를 들어 'O' 는 긴 Floor, '1' 은 첫 번째 JellyItem, '@' 는 확대 젤리라는 식의
    // "stage 문자와 실제 객체 생성 규칙의 연결"을 이곳에 모은다.
    //
    // 이렇게 하면 MapLoader 는 stage 파일을 읽고 좌표를 계산하는 책임에 집중하고,
    // 문자별 생성 규칙은 이 Catalog 로 분리할 수 있다.
    private var registered = false

    fun registerAll() {
        // MainScene 은 Activity 재생성 등으로 여러 번 만들어질 수 있다.
        // registerAll() 이 여러 번 불려도 같은 문자를 중복 등록하지 않도록 한 번만 실행한다.
        if (registered) return
        registered = true

        // '1' ~ '9' 까지의 숫자 문자는 모두 일반 jelly item 이므로 한 번에 등록한다.
        // stage 문자 '1' 이 jelly sheet 의 0번 index 를 뜻하므로, tile - '1' 로 변환한다.
        // 즉 '1' -> 0, '2' -> 1, ..., '9' -> 8 이 된다.
        MapObjectRegistry.register('1'..'9', { gctx, tile, left, top ->
            JellyItem.get(gctx, tile - '1', left, top)
        })
        // '@' 는 임시로 Magnification 효과를 테스트하기 위한 특수 jelly 이다.
        // 맵 파일 문자인 '@' 와 jelly sheet 의 index 값이 만나는 지점은
        // MapLoader 도 JellyItem 도 아니라, 문자별 생성 규칙을 모으는 Catalog 쪽에 둔다.
        MapObjectRegistry.register('@',  { gctx, _, left, top ->
            JellyItem.get(gctx, JellyItem.MAGNIFICATION_INDEX, left, top)
        })

        // 바닥 타일은 문자마다 서로 다른 Floor.Type 으로 연결한다.
        // Floor.Type 이 이미지 리소스와 실제 게임 좌표 크기를 함께 들고 있으므로,
        // Catalog 는 어떤 문자가 어떤 Type 인지만 결정하면 된다.
        MapObjectRegistry.register('O', { gctx, _, left, top ->
            Floor.get(gctx, Floor.Type.T_10x2, left, top)
        })
        MapObjectRegistry.register('P', { gctx, _, left, top ->
            Floor.get(gctx, Floor.Type.T_2x2, left, top)
        })
        MapObjectRegistry.register('Q', { gctx, _, left, top ->
            Floor.get(gctx, Floor.Type.T_3x1, left, top)
        })

        // 'X' 는 가장 기본 장애물이다.
        // MapLoader 는 'X' 의 의미를 직접 알지 않고,
        // Catalog 에 등록된 생성 규칙을 통해 SimpleObstacle 을 만들게 된다.
        MapObjectRegistry.register('X', { gctx, _, left, top ->
            SimpleObstacle.get(gctx, left, top)
        })

        // 'Y' 와 'Z' 는 애니메이션 장애물이다.
        // 두 문자가 같은 AnimObstacle 클래스를 쓰지만,
        // 서로 다른 Type 을 넘겨 이미지 프레임 목록과 기준 폭을 다르게 잡는다.
        MapObjectRegistry.register('Y'..'Z', { gctx, ch, left, top ->
            val type = if (ch == 'Y') AnimObstacle.Type.SPIKY_3 else AnimObstacle.Type.SPIKY_2
            AnimObstacle.get(gctx, type, left, top)
        })
    }
}
