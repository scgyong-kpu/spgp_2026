package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.graphics.withScale
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
    fun interface OnMenuListener {
        fun onMenu(resId: Int): Boolean
    }

    private val gctx = gctx
    // Drawable 은 리소스 로더가 내부 캐시를 공유할 수 있으므로,
    // 여기서는 메뉴 배경을 이 객체 전용으로만 쓰도록 mutate() 한다.
    // 그렇지 않으면 alpha 같은 상태를 바꾸는 순간 다른 화면 요소에도
    // 같은 변경이 번질 수 있다.
    //
    // alpha 는 draw() 때마다 바꾸지 않고 생성 시점에 한 번만 고정한다.
    // 메뉴는 항상 같은 반투명도면 충분하므로, 매 프레임 상태를 바꾸는 것보다
    // 로드 후 바로 세팅해 두는 편이 더 단순하고 안전하다.
    private val menuBgDrawable = gctx.res.getDrawable(R.mipmap.menu_bg).mutate().apply {
        alpha = MENU_ALPHA
    }
    private val background = DrawableSprite(menuBgDrawable)
    private val bgPadding = Rect().also { menuBgDrawable.getPadding(it) }
    // 메뉴 아이콘도 매 draw 마다 Paint 를 새로 만들지 않고 하나만 재사용한다.
    // alpha 를 고정하면 배경과 아이템이 같은 투명도로 보여서
    // 메뉴 전체가 "위에 얹힌 UI"처럼 자연스럽게 보인다.
    private val itemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        alpha = MENU_ALPHA
    }
    private val screenWidth = gctx.metrics.width
    private val screenHeight = gctx.metrics.height

    private var visible = false
    private var menuItems: IntArray = BLANK_MENU_ITEMS
    private var itemSize = 0f
    private var bgWidth = 0f
    private var bgHeight = 0f
    private var bgLeft = 0f
    private var bgTop = 0f
    private var flipBackgroundX = false
    var onMenuListener: OnMenuListener? = null

    fun showInstallMenuAt(selectionRect: RectF) {
        menuItems = INSTALL_MENU_ITEMS
        itemSize = INSTALL_ITEM_SIZE
        applyLayout(selectionRect, INSTALL_CONTENT_WIDTH, INSTALL_CONTENT_HEIGHT)
        visible = true
    }

    fun showManageMenuAt(selectionRect: RectF) {
        menuItems = MANAGE_MENU_ITEMS
        itemSize = MANAGE_ITEM_SIZE
        applyLayout(selectionRect, MANAGE_CONTENT_WIDTH, MANAGE_CONTENT_HEIGHT)
        visible = true
    }

    fun hide() {
        visible = false
        menuItems = BLANK_MENU_ITEMS
    }

    fun onTouch(x: Float, y: Float): Boolean {
        if (!visible || menuItems.isEmpty()) return false

        val itemTop = bgTop + bgPadding.top
        val itemBottom = itemTop + itemSize
        var itemLeft = bgLeft + if (flipBackgroundX) bgPadding.right else bgPadding.left
        for (menuItem in menuItems) {
            val itemRight = itemLeft + itemSize
            if (x >= itemLeft && x <= itemRight && y >= itemTop && y <= itemBottom) {
                val handled = onMenuListener?.onMenu(menuItem) ?: false
                if (handled) {
                    hide()
                    return true
                }
                return false
            }
            itemLeft += itemSize
        }
        return false
    }

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        if (!visible || menuItems.isEmpty()) return

        drawBackground(canvas)

        val itemLeft = bgLeft + if (flipBackgroundX) bgPadding.right else bgPadding.left
        val itemTop = bgTop + bgPadding.top
        var x = itemLeft
        for (menuItem in menuItems) {
            canvas.drawBitmap(
                gctx.res.getBitmap(menuItem),
                null,
                RectF(x, itemTop, x + itemSize, itemTop + itemSize),
                itemPaint,
            )
            x += itemSize
        }
    }

    private fun drawBackground(canvas: Canvas) {
        val drawable = background.drawable
        if (flipBackgroundX) {
            drawDrawableFlippedX(canvas, drawable, bgLeft, bgTop, bgWidth, bgHeight)
            return
        }
        drawDrawable(canvas, drawable, bgLeft, bgTop, bgWidth, bgHeight)
    }

    private fun drawDrawable(
        canvas: Canvas,
        drawable: Drawable,
        left: Float,
        top: Float,
        width: Float,
        height: Float,
    ) {
        // bounds 는 현재 draw 한 번에만 필요한 위치 정보이므로,
        // 여기서만 설정하고 바로 그린다.
        drawable.setBounds(
            left.toInt(),
            top.toInt(),
            (left + width).toInt(),
            (top + height).toInt(),
        )
        drawable.draw(canvas)
    }

    private fun drawDrawableFlippedX(
        canvas: Canvas,
        drawable: Drawable,
        left: Float,
        top: Float,
        width: Float,
        height: Float,
    ) {
        canvas.withScale(-1f, 1f, left + width / 2f, top + height / 2f) {
            drawDrawable(this, drawable, left, top, width, height)
        }
    }

    private fun canFitRight(anchorX: Float, contentWidth: Float): Boolean {
        val fullWidth = contentWidth + bgPadding.left + bgPadding.right
        return anchorX + fullWidth <= screenWidth
    }

    private fun applyLayout(selectionRect: RectF, contentWidth: Float, contentHeight: Float) {
        bgWidth = contentWidth + bgPadding.left + bgPadding.right
        bgHeight = contentHeight + bgPadding.top + bgPadding.bottom

        // 메뉴가 화면 아래/위로 잘리지 않도록 세로 위치를 clamp 한다.
        val anchorY = selectionRect.centerY()
        val wantedTop = anchorY - bgHeight / 2f
        bgTop = wantedTop.coerceIn(0f, (screenHeight - bgHeight).coerceAtLeast(0f))

        // 오른쪽이 남으면 selection 의 오른쪽 중심을 앵커로 쓰고,
        // 아니면 selection 의 왼쪽 중심을 앵커로 써서 왼쪽으로 붙인다.
        val rightAnchorX = selectionRect.right
        val leftAnchorX = selectionRect.left
        val fitsRight = canFitRight(rightAnchorX, contentWidth)
        bgLeft = if (fitsRight) {
            flipBackgroundX = false
            rightAnchorX
        } else {
            flipBackgroundX = true
            val wantedLeft = leftAnchorX - bgWidth
            wantedLeft.coerceAtLeast(0f)
        }

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
        private const val MENU_ALPHA = 0xA0
    }
}
