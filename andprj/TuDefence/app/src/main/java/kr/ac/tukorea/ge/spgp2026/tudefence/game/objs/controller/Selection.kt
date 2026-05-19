package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.R

class Selection(gctx: GameContext, private val width: Float, private val height: Float) : IGameObject {
    private val installableBitmap: Bitmap = gctx.res.getBitmap(R.mipmap.selection)
    private val nonInstallableBitmap: Bitmap = gctx.res.getBitmap(R.mipmap.sel_non_installable)
    private val dstRect = RectF()
    private var visible = false
    private var canInstall = false

    fun moveTo(cx: Float, cy: Float, canInstall: Boolean) {
        this.canInstall = canInstall
        this.visible = true
        dstRect.set(cx - width / 2f, cy - height / 2f, cx + width / 2f, cy + height / 2f)
    }

    fun hide() {
        visible = false
    }

    override fun update(gctx: GameContext) {}

    override fun draw(canvas: Canvas) {
        if (!visible) return
        val bitmap = if (canInstall) installableBitmap else nonInstallableBitmap
        canvas.drawBitmap(bitmap, null, dstRect, null)
    }
}
