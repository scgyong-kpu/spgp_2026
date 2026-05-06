package kr.ac.tukorea.ge.spgp2026.cookierun.game.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import org.json.JSONArray

data class CookieInfo(
    val id: Int,
    val name: String,
    val stateRects: List<List<Rect>>,
    val jumpPower: Float = Player.JUMP_POWER,
    val scoreRate: Float = 1.0f,
)

// CookieCatalog 는 assets/cookies.json 을 한 번 읽어 쿠키 선택 정보와 애니메이션 Rect 를 제공한다.
// Activity 사이에는 cookieId 만 전달하고, 실제 게임 데이터는 게임 쪽에서 asset 을 읽어 해석한다.
object CookieCatalog {
    private const val COOKIES_JSON = "cookies.json"
    private const val DEFAULT_COOKIE_ID = 107566

    private var cookies: List<CookieInfo>? = null

    fun all(context: Context): List<CookieInfo> {
        cookies?.let { return it }

        val loaded = context.assets.open(COOKIES_JSON).bufferedReader().use { reader ->
            parseCookies(JSONArray(reader.readText()))
        }
        cookies = loaded
        return loaded
    }

    fun get(context: Context, cookieId: Int): CookieInfo {
        val loaded = all(context)
        return loaded.firstOrNull { it.id == cookieId }
            ?: loaded.first { it.id == DEFAULT_COOKIE_ID }
    }

    fun getBitmap(context: Context, cookieId: Int, suffix: String): Bitmap {
        val filename = "cookies/${cookieId}_${suffix}.png"
        return context.assets.open(filename).use { input ->
            BitmapFactory.decodeStream(input)
        }
    }

    private fun parseCookies(array: JSONArray): List<CookieInfo> {
        return List(array.length()) { index ->
            val obj = array.getJSONObject(index)
            CookieInfo(
                id = obj.getInt("id"),
                name = obj.getString("name"),
                stateRects = parseStateRects(obj.getJSONArray("stateRects")),
                jumpPower = obj.optDouble("jumpPower", Player.JUMP_POWER.toDouble()).toFloat(),
                scoreRate = obj.optDouble("scoreRate", 1.0).toFloat(),
            )
        }
    }

    private fun parseStateRects(array: JSONArray): List<List<Rect>> {
        return List(array.length()) { stateIndex ->
            val rects = array.getJSONArray(stateIndex)
            List(rects.length()) { rectIndex ->
                val rect = rects.getJSONArray(rectIndex)
                Rect(rect.getInt(0), rect.getInt(1), rect.getInt(2), rect.getInt(3))
            }
        }
    }
}
