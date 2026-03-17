package kr.ac.tukorea.ge.android.cardsbyai

import android.content.Context
import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    val id: Int,
    val cardType: Int,
    var isOpen: Boolean = false,
    var isRemoved: Boolean = false
) : Parcelable

@Parcelize
data class GameState(
    var cardList: MutableList<Card> = mutableListOf(),
    var flipCount: Int = 0,
    var firstCardIndex: Int = -1,
    var secondCardIndex: Int = -1,
    var isCheckingCards: Boolean = false,
    var removedCount: Int = 0
) : Parcelable {
    companion object {
        private const val PREFS_NAME = "game_state"
        private const val KEY_STATE = "gameState"
        private val gson = Gson()

        fun newGame(): GameState {
            val list = mutableListOf<Card>()
            var id = 0
            for (cardType in 0..7) {
                list.add(Card(id++, cardType))
                list.add(Card(id++, cardType))
            }
            list.shuffle()
            return GameState(cardList = list)
        }

        fun load(context: Context): GameState? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(KEY_STATE, null) ?: return null
            return try {
                gson.fromJson(json, GameState::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun save(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(this)
        prefs.edit().putString(KEY_STATE, json).apply()
    }

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_STATE).apply()
    }
}
