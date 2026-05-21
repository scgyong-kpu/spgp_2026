package kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.controller

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.tudefence.game.map.MapCamera
import kr.ac.tukorea.ge.spgp2026.tudefence.game.objs.weapon.Cannon
import kr.ac.tukorea.ge.spgp2026.tudefence.R

class Selection(gctx: GameContext, private val width: Float, private val height: Float) : IGameObject {
    private val installableBitmap: Bitmap = gctx.res.getBitmap(R.mipmap.selection)
    private val nonInstallableBitmap: Bitmap = gctx.res.getBitmap(R.mipmap.sel_non_installable)
    private val dstRect = RectF()
    private var visible = false
    private var canInstall = false
    var selectedCannon: Cannon? = null
        private set

    fun moveTo(cx: Float, cy: Float, canInstall: Boolean) {
        this.canInstall = canInstall
        this.selectedCannon = null
        this.visible = true
        dstRect.set(cx - width / 2f, cy - height / 2f, cx + width / 2f, cy + height / 2f)
    }

    fun selectCannon(cannon: Cannon) {
        selectedCannon = cannon
        canInstall = false
        visible = true
        dstRect.set(
            cannon.x - cannon.width / 2f,
            cannon.y - cannon.height / 2f,
            cannon.x + cannon.width / 2f,
            cannon.y + cannon.height / 2f,
        )
    }

    fun hide() {
        visible = false
        selectedCannon = null
    }

    fun sceneRect(mapCamera: MapCamera, out: RectF = RectF()): RectF {
        out.set(dstRect)
        mapCamera.matrix.mapRect(out)
        return out
    }

    fun mapRect(out: RectF = RectF()): RectF {
        out.set(dstRect)
        return out
    }

    override fun update(gctx: GameContext) {
    }

    override fun draw(canvas: Canvas) {
        if (!visible) return
        selectedCannon?.drawRange(canvas)
        val bitmap = if (selectedCannon != null || canInstall) installableBitmap else nonInstallableBitmap
        canvas.drawBitmap(bitmap, null, dstRect, null)
    }
}
