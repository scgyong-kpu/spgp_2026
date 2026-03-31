package kr.ac.tukorea.ge.scgyong.samplegame.game.scene.stacktest

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class StackTestScene1(gctx: GameContext) : Scene(gctx) {
    private val backgroundPaint = Paint().apply {
        color = Color.argb(220, 30, 60, 120)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 36f
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, gctx.metrics.width, gctx.metrics.height, backgroundPaint)
        canvas.drawText("Stack Test 1", 80f, 180f, textPaint)
        canvas.drawText("여기를 누르면 push", 80f, 320f, textPaint)
        canvas.drawText("여기를 누르면 pop", 80f, gctx.metrics.height / 2f + 120f, textPaint)
    }

    override fun onEnter() {
        Log.d(javaClass.simpleName, "onEnter")
    }

    override fun onExit() {
        Log.d(javaClass.simpleName, "onExit")
    }

    override fun onPause() {
        Log.d(javaClass.simpleName, "onPause")
    }

    override fun onResume() {
        Log.d(javaClass.simpleName, "onResume")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_DOWN) return false
        val pt = gctx.metrics.fromScreen(event.x, event.y)
        if (pt.y >= gctx.metrics.height / 2f) {
            pop()
        } else {
            StackTestScene2(gctx).push()
        }
        return true
    }
}
