package kr.ac.tukorea.ge.spgp2026.a2dg.res

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

// 같은 비트맵을 여러 번 decode 하지 않도록 id 별로 한 번만 로드해 재사용하는 단순 캐시이다.
// 여기서 비트맵은 화면 해상도나 밀도에 따라 자동으로 확대/축소된 결과가 아니라,
// 리소스 파일이 가진 "원본 픽셀 크기" 그대로 로드하는 것을 기본 정책으로 삼는다.
//
// 이렇게 해 두면:
// 1. 기기 밀도가 달라져도 Bitmap 자체 크기가 예측 가능하고
// 2. 실제 화면에 얼마만큼 크게 그릴지는 Rect 나 Matrix 같은 게임 쪽 좌표계에서 결정할 수 있으며
// 3. 안드로이드 리소스 시스템이 자동으로 스케일한 결과와 게임 좌표계 스케일이 서로 섞여 헷갈리는 일을 줄일 수 있다.
class BitmapPool(
    private val resources: Resources,
) {
    private val bitmaps = mutableMapOf<Int, Bitmap>()
    private val decodeOptions = BitmapFactory.Options().apply {
        // inScaled=false 로 두면 decodeResource() 가 기기 density 에 맞춰
        // Bitmap 크기를 자동으로 바꾸지 않고, 리소스의 원본 픽셀 크기를 그대로 유지한다.
        inScaled = false
    }

    // getOrPut() 을 쓰지 않고 같은 함수를 풀어 쓰면 대략 아래와 같은 형태가 된다.
    //
    // fun get(id: Int): Bitmap {
    //     val cached = bitmaps[id]
    //     if (cached != null) {
    //         return cached
    //     }
    //
    //     val bitmap = BitmapFactory.decodeResource(resources, id, decodeOptions)
    //     bitmaps[id] = bitmap
    //     return bitmap
    // }
    fun get(id: Int): Bitmap {
        return bitmaps.getOrPut(id) {
            BitmapFactory.decodeResource(resources, id, decodeOptions)
        }
    }
}
