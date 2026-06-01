@file:Suppress("DEPRECATION")

package kr.ac.tukorea.ge.spgp2026.taptu.res

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log

// album cover 를 게임 배경으로 크게 깔면 원본 이미지의 선명한 디테일이 note 와 충돌할 수 있다.
// 그래서 cover 를 살짝 blur 한 bitmap 으로 바꿔 배경감만 남기는 helper 를 둔다.
object BitmapBlur {
    private const val BLUR_RADIUS = 5f
    private val TAG = BitmapBlur::class.java.simpleName

    fun blurBitmap(context: Context, bitmap: Bitmap): Bitmap? {
        try {
            blurBitmapWithRenderEffect(bitmap)?.let { return it }
        } catch (e: Exception) {
            Log.w(TAG, "RenderEffect Exception: $e")
        }

        try {
            return blurBitmapWithRenderScript(context, bitmap)
        } catch (e: Exception) {
            Log.w(TAG, "RenderScript Exception: $e")
        }

        return null
    }

    private fun blurBitmapWithRenderEffect(bitmap: Bitmap): Bitmap? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null

        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val blurredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, config)

        // RenderEffect 는 API 31 부터 권장되는 blur 방식이다.
        // RenderNode 에 원본 bitmap 을 한 번 그리고, 그 node 에 blur effect 를 걸어 결과 bitmap 에 다시 그린다.
        val renderNode = RenderNode("BlurNode")
        renderNode.setRenderEffect(
            RenderEffect.createBlurEffect(BLUR_RADIUS, BLUR_RADIUS, Shader.TileMode.CLAMP)
        )

        val canvas = renderNode.beginRecording(bitmap.width, bitmap.height)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        renderNode.endRecording()

        val outputCanvas = Canvas(blurredBitmap)
        outputCanvas.drawRenderNode(renderNode)

        return blurredBitmap
    }

    @Suppress("DEPRECATION")
    private fun blurBitmapWithRenderScript(context: Context, bitmap: Bitmap): Bitmap {
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val blurredBitmap = bitmap.copy(config, true)

        // RenderScript blur 는 오래된 Android 버전에서도 쓸 수 있지만 deprecated 되었다.
        // 여기서는 minSdk 28 기기에서도 같은 실험을 보여주기 위한 fallback 으로만 사용한다.
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        script.setRadius(BLUR_RADIUS)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(blurredBitmap)

        input.destroy()
        output.destroy()
        script.destroy()
        renderScript.destroy()

        return blurredBitmap
    }
}
