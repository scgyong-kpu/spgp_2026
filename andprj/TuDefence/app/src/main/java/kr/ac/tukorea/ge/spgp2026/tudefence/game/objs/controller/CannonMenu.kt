package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.DrawableSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R

// CannonMenu 는 터치한 위치에 캐넌을 바로 설치하지 않고,
// 설치 / 업그레이드 / 삭제 같은 선택지를 보여 주는 임시 메뉴이다.
//
// 작년 방식처럼 menuItems 를 int 배열로 들고 있다가,
// 설치할 때는 3개짜리 배열, 선택 상태에서는 2개짜리 배열을 넣어
// draw() 가 같은 루프로 메뉴를 그리게 한다.
class CannonMenu(gctx: GameContext) : IGameObject {
    private val gctx = gctx
    private val menuBgDrawable = gctx.res.getDrawable(R.mipmap.menu_bg)
    private val background = DrawableSprite(menuBgDrawable)
    private val bgPadding = Rect().also { menuBgDrawable.getPadding(it) }

    private var visible = false
    private var menuItems: IntArray = BLANK_MENU_ITEMS
    private var itemSize = 0f
    private var bgWidth = 0f
    private var bgHeight = 0f
    private var bgLeft = 0f
    private var bgTop = 0f

    fun showInstallMenuAt(anchorX: Float, anchorY: Float) {
        menuItems = INSTALL_MENU_ITEMS
        itemSize = INSTALL_ITEM_SIZE
        applyLayout(anchorX, anchorY, INSTALL_CONTENT_WIDTH, INSTALL_CONTENT_HEIGHT)
        visible = true
    }

    fun showManageMenuAt(anchorX: Float, anchorY: Float) {
        menuItems = MANAGE_MENU_ITEMS
        itemSize = MANAGE_ITEM_SIZE
        applyLayout(anchorX, anchorY, MANAGE_CONTENT_WIDTH, MANAGE_CONTENT_HEIGHT)
        visible = true
    }

    fun hide() {
        visible = false
        menuItems = BLANK_MENU_ITEMS
    }

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        if (!visible || menuItems.isEmpty()) return

        background.draw(canvas)

        val itemLeft = bgLeft + bgPadding.left
        val itemTop = bgTop + bgPadding.top
        var x = itemLeft
        for (menuItem in menuItems) {
            canvas.drawBitmap(
                gctx.res.getBitmap(menuItem),
                null,
                RectF(x, itemTop, x + itemSize, itemTop + itemSize),
                null,
            )
            x += itemSize
        }
    }

    private fun applyLayout(anchorX: Float, anchorY: Float, contentWidth: Float, contentHeight: Float) {
        bgWidth = contentWidth + bgPadding.left + bgPadding.right
        bgHeight = contentHeight + bgPadding.top + bgPadding.bottom

        // 선택 사각형의 오른쪽 중심이 메뉴 9-patch 의 시작점(꼭지점)으로 오도록 붙인다.
        // 그래서 메뉴는 선택 표시와 간격이 거의 없이 바로 오른쪽에서 열리는 것처럼 보인다.
        bgLeft = anchorX
        bgTop = anchorY - bgHeight / 2f

        background.setSize(bgWidth, bgHeight)
        background.setCenter(bgLeft + bgWidth / 2f, bgTop + bgHeight / 2f)
    }

    companion object {
        private val BLANK_MENU_ITEMS = intArrayOf()
        private val INSTALL_MENU_ITEMS = intArrayOf(
            R.mipmap.f_01_01,
            R.mipmap.f_01_02,
            R.mipmap.f_01_03,
        )
        private val MANAGE_MENU_ITEMS = intArrayOf(
            R.mipmap.upgrade,
            R.mipmap.uninstall,
        )

        // 설치 메뉴는 3칸이 서로 붙어 있는 compact layout 이다.
        private const val INSTALL_ITEM_SIZE = 100f
        private const val MANAGE_ITEM_SIZE = 100f
        private const val INSTALL_CONTENT_WIDTH = 300f
        private const val INSTALL_CONTENT_HEIGHT = 100f
        private const val MANAGE_CONTENT_WIDTH = 200f
        private const val MANAGE_CONTENT_HEIGHT = 100f
    }
}
