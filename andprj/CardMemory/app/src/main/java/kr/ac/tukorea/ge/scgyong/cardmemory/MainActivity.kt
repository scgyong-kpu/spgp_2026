package kr.ac.tukorea.ge.scgyong.cardmemory

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
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
    fun onCardClicked(v: View) {
        // v 는 클릭된 View 객체이다.
        // Layout XML 에서 여러 ImageButton 이 동일한 onClick 함수를 사용하기 때문에
        // 어떤 버튼이 눌렸든지 이 함수 하나로 전달된다.

        // onClick 의 매개변수 타입은 View 이므로 ImageButton 전용 함수인
        // setImageResource() 를 사용하려면 ImageButton 으로 형변환이 필요하다.
        // 이를 Downcasting 이라고 한다.
        val button = v as ImageButton

        // Downcasting 은 실제 객체 타입이 맞지 않으면 ClassCastException 이 발생한다.
        // 그러나 이 함수는 XML 에서 ImageButton 의 onClick 으로만 연결되어 있으므로
        // 여기서는 안전하다고 가정하고 사용한다.

        // 아니면, 다음과 같이 안전을 추구해도 좋다
        // val button = v as? ImageButton ?: return
        //    as → 강제 캐스팅 (실패하면 crash)
        //    as? → 안전 캐스팅 (실패하면 null)

        // 클릭된 버튼의 이미지를 Ace of Spades 로 변경한다.
        button.setImageResource(R.mipmap.card_as)

        // 굳이 한 줄로 쓰자면:
        // (v as? ImageButton)?.setImageResource(R.mipmap.card_as)
    }
}
