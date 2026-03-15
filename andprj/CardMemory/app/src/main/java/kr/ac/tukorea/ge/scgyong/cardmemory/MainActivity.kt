package kr.ac.tukorea.ge.scgyong.cardmemory

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kr.ac.tukorea.ge.scgyong.cardmemory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // activity_main.xml의 View들을 안전하게 접근하기 위한 ViewBinding 객체
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LayoutInflater를 사용해 activity_main.xml을 ViewBinding 객체로 생성
        binding = ActivityMainBinding.inflate(layoutInflater)

        // binding.root는 activity_main.xml의 최상위 View이다.
        // setContentView에 전달하여 화면에 표시한다.
        setContentView(binding.root)

        // 이제 XML에 있는 View들을 findViewById 없이 직접 접근할 수 있다.
        // 예: binding.flipCountText.text = "0"
    }

    // XML의 android:onClick 속성으로 연결된 카드 버튼 클릭 처리 함수이다.
    // 여러 ImageButton이 동일한 함수로 들어온다는 것을 확인하기 위해 만든다.
    fun onCardClicked(v: View) {

        // Kotlin의 삼항 연산자 대신 if-else 표현식을 사용하여 카드의 ID를 가져온다.
        //val name = v.id > 0 ? resources.getResourceEntryName(v.id) : "No ID"
        val name = if (v.id > 0) resources.getResourceEntryName(v.id) else "No ID"
        val msg = "Clicked: $name(${v.id})"
        Log.d("CardGame", msg)

        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}