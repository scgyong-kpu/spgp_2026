package kr.ac.tukorea.ge.spgp2026.a2dg.res

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat

// GameResources 는 게임 실행 중 공통으로 쓰는 리소스 접근 창구이다.
// Bitmap, Sound 처럼 리소스 성격의 기능을 gctx.res 아래에 모아 두면
// 게임 오브젝트가 전역 singleton 에 직접 기대지 않고 같은 통로로 접근할 수 있다.
class GameResources(
    private val resources: Resources,
    context: Context,
) {
    private val bitmapPool = BitmapPool(resources)
    val sound = Sound(context)

    fun getBitmap(resId: Int): Bitmap {
        return bitmapPool.get(resId)
    }

    fun getDrawable(resId: Int): Drawable {
        return ResourcesCompat.getDrawable(resources, resId, null)!!
    }
}
