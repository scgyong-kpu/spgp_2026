package kr.ac.tukorea.ge.scgyong.samplegame

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withMatrix


// 게임이 내부적으로 사용할 가상 좌표계의 크기이다.
// 실제 폰 화면 크기와는 별개로, 게임 안에서는 900 x 1600 공간이 있다고 생각하고 그린다.
private const val VIRTUAL_WIDTH = 900f
private const val VIRTUAL_HEIGHT = 1600f

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val borderRect = RectF(0f, 0f, VIRTUAL_WIDTH, VIRTUAL_HEIGHT)

    private val ballRect = RectF(350f, 700f, 550f, 900f)
    private val ballBitmap = BitmapFactory.decodeResource(resources, R.mipmap.soccer_ball_240)

    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE // 테두리만 그린다.
        strokeWidth = 10f
    }

    private val transformMatrix = Matrix()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val scaleX = w / VIRTUAL_WIDTH
        val scaleY = h / VIRTUAL_HEIGHT
        val scale = minOf(scaleX, scaleY) // 잘리지 않게 더 작은 배율을 고른다.
        val contentWidth = VIRTUAL_WIDTH * scale
        val contentHeight = VIRTUAL_HEIGHT * scale
        val offsetX = (w - contentWidth) / 2f
        val offsetY = (h - contentHeight) / 2f
        transformMatrix.reset()
        transformMatrix.postTranslate(offsetX, offsetY) // 먼저 가운데로 옮긴다.
        transformMatrix.postScale(scale, scale, offsetX, offsetY) // 그 위치를 기준으로 확대/축소한다.
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Java 였다면 이런 식으로 썼을 것이다. "canvas." 도 생략 가능해서 더 간결한 코드가 된다.
        // canvas.save();
        // canvas.concat(transformMatrix);
        // canvas.drawRect(borderRect, borderPaint);
        // canvas.drawBitmap(ballBitmap, null, ballRect, null);
        // canvas.restore();

        canvas.withMatrix(transformMatrix) {
            drawRect(borderRect, borderPaint) // 900 x 1600 가상 좌표계의 경계
            drawBitmap(ballBitmap, null, ballRect, null) // 공의 위치와 크기는 ballRect 로 정한다.
        }
        // withMatrix 블록이 끝나면 restore 가 자동으로 일어난다.
    }
}
