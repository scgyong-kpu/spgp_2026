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

        // v는 클릭된 View 객체이다.
        // OOP 에서
        //  onCardClicked = message
        //  v = sender
        //  this = receiver
        // 여러 버튼이 같은 함수로 들어오기 때문에 어떤 버튼이 눌렸는지는
        // View의 id 값을 통해 구분할 수 있다.

        // Kotlin에서는 문자열 안에 변수 값을 직접 넣을 수 있는데
        // 이를 String Interpolation(문자열 보간)이라고 한다.
        // $변수 또는 ${수식} 형태로 사용하며, 아래 코드에서는 v.id 값이 문자열 안에 들어간다.
        Log.d("CardGame", "clicked id=${v.id}")

        // 로그는 Android Studio 의 Menu->View->Tool Windows->Logcat 에서 볼 수 있다.

        // 위 로그는 정수 형태의 id 값이 출력된다.
        // 버튼의 실제 이름(card00, card01 등)을 보고 싶다면
        // resources.getResourceEntryName()을 사용하여 id에 대응되는 이름을 얻을 수 있다.
        //Log.d("CardGame", "clicked ${resources.getResourceEntryName(v.id)}")
    }
}