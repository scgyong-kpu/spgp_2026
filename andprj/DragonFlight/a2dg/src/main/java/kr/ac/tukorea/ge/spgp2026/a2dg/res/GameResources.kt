package kr.ac.tukorea.ge.spgp2026.a2dg.res

import android.content.res.Resources
import android.graphics.Bitmap

// GameResources 는 게임 실행 중 공통으로 쓰는 리소스 접근 창구이다.
// 지금은 Bitmap 만 다루지만, 나중에는 소리나 폰트 같은 다른 리소스도 이쪽으로 모을 수 있다.
class GameResources(
    resources: Resources,
) {
    private val bitmapPool = BitmapPool(resources)

    fun getBitmap(resId: Int): Bitmap {
        return bitmapPool.get(resId)
    }
}
