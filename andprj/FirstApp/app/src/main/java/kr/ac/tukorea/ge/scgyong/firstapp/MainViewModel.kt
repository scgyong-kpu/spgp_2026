package kr.ac.tukorea.ge.scgyong.firstapp

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    // orientation change가 발생해도 유지할 현재 페이지 번호이다.
    var currentPage = 1
}