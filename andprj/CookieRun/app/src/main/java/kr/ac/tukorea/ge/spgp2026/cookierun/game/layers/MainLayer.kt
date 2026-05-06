package kr.ac.tukorea.ge.spgp2026.cookierun.game.layers

// MainLayer 는 실제 플레이 화면(MainScene)의 World 가 사용하는 레이어 목록이다.
// MapObject, Obstacle, Player 처럼 여러 package 의 클래스가 같은 layer 값을 알아야 하므로
// MainScene 내부 enum 으로 두면 다른 package 가 MainScene 전체에 의존하게 된다.
//
// 별도 package 의 enum 으로 분리하면 각 객체는 "내가 어느 layer 에 놓이는지"만 알면 되고,
// MainScene 의 생성/입력/음악 처리 같은 구체 구현에는 의존하지 않아도 된다.
enum class MainLayer {
    // OBSTACLE 은 FLOOR/ITEM 보다 앞에, PLAYER 보다 뒤에 둔다.
    // 이렇게 하면 장애물이 바닥과 아이템 위에 보이면서도,
    // 플레이어가 장애물에 가려지지 않아 충돌 상황을 확인하기 쉽다.
    //
    // TOUCH 는 화면에 그려지는 버튼을 담는 레이어이다.
    // World 는 이 레이어를 draw 하고, Scene 은 같은 레이어를 touch dispatch 대상으로도 사용한다.
    BG, FLOOR, ITEM, OBSTACLE, PLAYER, TOUCH, CONTROLLER
}
