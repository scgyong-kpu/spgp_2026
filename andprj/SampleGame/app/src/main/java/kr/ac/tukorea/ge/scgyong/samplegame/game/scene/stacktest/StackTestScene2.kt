package kr.ac.tukorea.ge.scgyong.samplegame.game.scene.stacktest

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class StackTestScene2(gctx: GameContext) : Scene(gctx) {
    private val backgroundPaint = Paint().apply {
        color = Color.argb(220, 120, 40, 40)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 36f
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, gctx.metrics.width, gctx.metrics.height, backgroundPaint)
        canvas.drawText("Stack Test 2", 80f, 180f, textPaint)
        canvas.drawText("화면을 터치하면 이 Scene 이 pop() 된다", 80f, 260f, textPaint)
        canvas.drawText("그 뒤 Scene 1 로 돌아간다", 80f, 340f, textPaint)
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
        pop()
        return true
    }
}
