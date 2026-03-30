package kr.ac.tukorea.ge.scgyong.samplegame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.samplegame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // MainActiity 와 GameActivity 를 나누어 놓았는데, 실행할 때마다 버튼을 누르기 귀찮다.
        // 그래서 임시로, onCreate() 안에서 바로 게임 화면으로 넘어가도록 해보자.
        // 나중에 Title 화면을 제대로 만들고 나면 아래 함수호출은 삭제할 것이다.
        startGameActivity()
    }

    fun onStartGameClicked(view: View) {
        startGameActivity()
    }

    private fun startGameActivity() {
        Log.d(javaClass.simpleName, "Start Game")
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }
}
