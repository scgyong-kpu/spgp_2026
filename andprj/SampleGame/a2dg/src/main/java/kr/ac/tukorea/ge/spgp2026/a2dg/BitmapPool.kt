package kr.ac.tukorea.ge.spgp2026.a2dg

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

// 같은 비트맵을 여러 번 decode 하지 않도록 id 별로 한 번만 로드해 재사용하는 단순 캐시이다.
class BitmapPool(
    private val resources: Resources,
) {
    private val bitmaps = mutableMapOf<Int, Bitmap>()

    fun get(id: Int): Bitmap {
        return bitmaps.getOrPut(id) {
            BitmapFactory.decodeResource(resources, id)
        }
    }
}
