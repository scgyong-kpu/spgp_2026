package kr.ac.tukorea.ge.spgp2026.taptu.game.scene.pause

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import androidx.core.graphics.toColorInt
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.DrawableSprite
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.util.LabelUtil
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kr.ac.tukorea.ge.spgp2026.taptu.R
import androidx.core.graphics.drawable.toDrawable

class PauseScene(gctx: GameContext): Scene(gctx) {
    enum class Layer {
        UI,
    }

    override val isTransparent = true
    override val world = World(Layer.entries.toTypedArray())

    private var elapsedTime = 0f

    init {
        val dimOverlay = DrawableSprite("#7f000000".toColorInt().toDrawable()).apply {
            setSize(gctx.metrics.width, gctx.metrics.height)
            setCenter(gctx.metrics.width / 2f, gctx.metrics.height / 2f)
        }
        world.add(dimOverlay, Layer.UI)
        val speechBox = DrawableSprite(gctx.res.getDrawable(R.drawable.box_bg)).apply {
            setSize(SPEECH_BOX_WIDTH, SPEECH_BOX_HEIGHT)
            setCenter(gctx.metrics.width / 2f, gctx.metrics.height / 2f)
        }
        world.add(speechBox, Layer.UI)
        world.add(MessageObject(), Layer.UI)
    }

    override fun onEnter() {
        elapsedTime = 0f
    }

    override fun update(gctx: GameContext) {
        elapsedTime += gctx.frameTime
        super.update(gctx)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_DOWN) return true
        handlePauseInput()
        return true
    }

    override fun onBackPressed(): Boolean {
        handlePauseInput()
        return true
    }

    private fun handlePauseInput() {
        if (isExitTime()) {
            gctx.sceneStack.popAll()
        } else {
            pop()
        }
    }

    private fun isExitTime(): Boolean {
        return elapsedTime < RESUME_DELAY
    }

    private fun message(): String {
        return if (isExitTime()) EXIT_MESSAGE else RESUME_MESSAGE
    }

    private inner class MessageObject: IGameObject {
        private val label = LabelUtil(
            textSize = MESSAGE_TEXT_SIZE,
            color = MESSAGE_TEXT_COLOR,
            align = Paint.Align.CENTER,
        )

        override fun update(gctx: GameContext) {
        }

        override fun draw(canvas: Canvas) {
            label.draw(
                canvas,
                message(),
                gctx.metrics.width / 2f,
                gctx.metrics.height / 2f,
            )
        }
    }

    companion object {
        private const val SPEECH_BOX_WIDTH = 800f
        private const val SPEECH_BOX_HEIGHT = 600f
        private const val RESUME_DELAY = 1.0f
        private const val MESSAGE_TEXT_SIZE = 54f
        private val MESSAGE_TEXT_COLOR = "#00107F".toColorInt()
        private const val EXIT_MESSAGE = "Press Back To Exit"
        private const val RESUME_MESSAGE = "Press Back To Resume"
    }
}