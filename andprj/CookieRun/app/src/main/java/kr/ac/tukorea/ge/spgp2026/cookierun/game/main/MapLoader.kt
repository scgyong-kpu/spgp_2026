package kr.ac.tukorea.ge.spgp2026.cookierun.game.main

import android.graphics.Canvas
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.util.Gauge
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.cookierun.R
import kr.ac.tukorea.ge.spgp2026.cookierun.game.main.MainScene.Layer

// MapLoader 는 text 파일에 적힌 stage 정보를 읽어서,
// 화면 오른쪽 바깥부터 필요한 MapObject 를 조금씩 만들어 내는 역할을 한다.
//
// stage 파일은 문자 하나가 100x100 게임 좌표 한 칸을 뜻하는 간단한 tile map 이다.
// 예를 들어 '1' 은 jelly item, 'O' 는 긴 바닥, '@' 는 Magnification 특수 젤리로 해석한다.
//
// 전체 stage 를 처음부터 모두 만들지 않고, 화면에 필요한 오른쪽 끝까지만 생성한다.
// 이렇게 하면 긴 맵을 다루더라도 처음 시작할 때 모든 객체를 한 번에 만들 필요가 없다.
class MapLoader(gctx: GameContext, val world: World<Layer>, private val stage: Int): IGameObject {
    // x 는 지금까지 맵 오브젝트를 만들어 둔 가장 오른쪽 화면 좌표다.
    // MapObject.SPEED 가 음수이므로, update() 때 x 도 함께 줄어든다.
    private var x = 0f
    // column 은 stage text 파일에서 현재 몇 번째 세로줄을 읽고 있는지를 뜻한다.
    // x 는 게임 좌표, column 은 text 파일의 문자 index 라고 구분해서 보면 된다.
    private var column = 0
    // stageWidth 는 stage 전체가 몇 column 짜리인지 나타낸다.
    // pageWidth 는 stage 파일 안에서 '|' 문자가 나오기 전까지의 한 페이지 폭이다.
    private var stageWidth = 0
    private var pageWidth = 0
    // 파일에서 읽은 모든 줄을 그대로 보관한다.
    // getAt(col, row) 가 이 목록에서 알맞은 줄과 문자 위치를 계산한다.
    private val lines = mutableListOf<String>()
    // MapLoader 는 stage 전체 폭과 현재 생성한 column 을 모두 알고 있다.
    // 그래서 맵 진행률 gauge 도 별도 객체를 만들기보다 여기서 바로 그리는 편이 단순하다.
    private val progressGauge = Gauge(
        MAP_GAUGE_THICKNESS,
        gctx.view.context.getColor(R.color.map_gauge_fg),
        gctx.view.context.getColor(R.color.map_gauge_bg),
    )

    init {
        loadStage(gctx, stage)
    }

    private fun loadStage(gctx: GameContext, stage: Int) {
        val filename = "stage_%02d.txt".format(stage)
        // Android 의 assets 폴더에 있는 파일은 Resources 의 R 값으로 접근하지 않는다.
        // context.assets.open("파일명") 으로 InputStream 을 열어 직접 읽는다.
        gctx.view.context.assets.open(filename).bufferedReader().use { reader ->
            lines.clear()
            pageWidth = 0
            while (true) {
                val line = reader.readLine() ?: break
                if (pageWidth == 0) {
                    // 각 page 오른쪽 끝에는 '|' 가 들어 있다.
                    // 첫 줄에서 그 위치를 찾으면 한 page 의 column 수를 알 수 있다.
                    pageWidth = line.indexOf('|')
                }
                lines.add(line)
            }
        }

        // stage 파일은 9줄짜리 page 를 세로로 이어 붙인 형태이다.
        // 예를 들어 27줄이면 9줄짜리 page 가 3개 들어 있는 것이다.
        val pages = lines.size / STAGE_HEIGHT
        val lastCol = lines.lastOrNull()?.length ?: 0
        // 마지막 page 는 '|' 뒤쪽이 없을 수 있으므로, 마지막 줄 길이까지 더해 stage 전체 폭을 잡는다.
        stageWidth = (pages - 1) * pageWidth + lastCol
    }

    override fun update(gctx: GameContext) {
        // 이미 만들어 둔 오른쪽 끝 x 도 맵과 같은 속도로 왼쪽으로 이동한다고 생각한다.
        // 그래서 화면 오른쪽 끝보다 x 가 작아지면, 부족해진 만큼 새 column 을 더 만든다.
        x += MapObject.SPEED * gctx.frameTime
        while (x < gctx.metrics.width) {
            createColumn(gctx)
            column += 1
            // stage text 의 column 하나는 게임 좌표 100 만큼의 폭을 가진다.
            x += TILE_SIZE
        }
    }

    private fun createColumn(gctx: GameContext) {
        // 현재 column 의 위에서 아래까지 9칸을 읽는다.
        // 각 row 에 있는 문자 하나를 보고 Floor, JellyItem 같은 객체를 생성한다.
        for (row in 0 until STAGE_HEIGHT) {
            val tile = getAt(column, row)
            val top = TILE_SIZE * row
            createObject(gctx, tile, x, top)
        }
    }

    private fun createObject(gctx: GameContext, tile: Char, left: Float, top: Float) {
        // 먼저 바닥 문자인지 확인한다.
        // 하나의 문자에서 객체가 만들어졌으면 바로 return 해서 중복 생성되지 않게 한다.
        val floorType = floorTypeFor(tile)
        if (floorType != null) {
            world.add(Floor.get(gctx, floorType, left, top), Layer.FLOOR)
            return
        }

        // 바닥이 아니면 아이템 문자인지 확인한다.
        // 아직 Obstacle 은 구현하지 않았으므로, X/Y/Z/W/T 같은 문자는 여기서 아무것도 만들지 않는다.
        val itemIndex = itemIndexFor(tile) ?: return
        world.add(JellyItem.get(gctx, itemIndex, left, top), Layer.ITEM)
    }

    private fun floorTypeFor(tile: Char): Floor.Type? {
        // stage 파일의 바닥 문자를 Floor.Type 으로 바꾼다.
        // null 을 반환하면 "이 문자는 Floor 가 아니다"라는 뜻이다.
        return when (tile) {
            'O' -> Floor.Type.T_10x2
            'P' -> Floor.Type.T_2x2
            'Q' -> Floor.Type.T_3x1
            else -> null
        }
    }

    private fun itemIndexFor(tile: Char): Int? {
        // 숫자 문자는 jelly.png 의 앞쪽 칸으로 연결한다.
        // '@' 는 확대 효과를 테스트하기 위한 특수 젤리 index 로 연결한다.
        return when (tile) {
            '@' -> JellyItem.MAGNIFICATION_INDEX
            in '1'..'8' -> tile - '1'
            else -> null
        }
    }

    private fun getAt(col: Int, row: Int): Char {
        if (col >= stageWidth || pageWidth <= 0) return EMPTY_TILE
        return try {
            // stage 파일은 9줄짜리 페이지가 가로로 이어진 형태다.
            // col 이 pageWidth 를 넘어가면 다음 9줄 묶음에서 같은 row 를 읽는다.
            // 예: pageWidth 가 100 이고 col 이 123 이면, 두 번째 page 의 23번째 문자를 읽는다.
            val lineIndex = col / pageWidth * STAGE_HEIGHT + row
            val line = lines[lineIndex]
            line[col % pageWidth]
        } catch (_: Exception) {
            EMPTY_TILE
        }
    }

    override fun draw(canvas: Canvas) {
        // column 은 지금까지 생성한 stage column 수이고,
        // stageWidth 는 stage 파일 전체 column 수이다.
        // 즉 column / stageWidth 는 "맵을 얼마나 진행했는가"를 0.0 ~ 1.0 범위로 표현한다.
        val progress = if (stageWidth > 0) {
            (column.toFloat() / stageWidth).coerceIn(0f, 1f)
        } else {
            0f
        }
        progressGauge.draw(canvas, MAP_GAUGE_X, MAP_GAUGE_Y, MAP_GAUGE_WIDTH, progress)
    }

    companion object {
        // stage 파일에서 한 화면 page 는 세로 9줄로 구성된다.
        private const val STAGE_HEIGHT = 9
        // stage 문자 하나가 게임 좌표계에서 차지하는 가로/세로 크기이다.
        private const val TILE_SIZE = 100f
        // stage 끝이거나 읽을 수 없는 위치일 때는 아무것도 만들지 않는 문자로 처리한다.
        private const val EMPTY_TILE = '\u0000'
        private const val MAP_GAUGE_X = 200f
        private const val MAP_GAUGE_Y = 100f
        private const val MAP_GAUGE_WIDTH = 1200f
        private const val MAP_GAUGE_THICKNESS = 0.025f
    }
}
