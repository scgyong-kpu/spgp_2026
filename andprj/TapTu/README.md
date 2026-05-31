# TapTu

Android 2D game programming 수업에서 진행할 리듬 게임 예제 프로젝트이다.

플레이어는 곡을 선택하고, 음악 시간에 맞추어 내려오는 note 를 처리한다. 프로젝트는 Android 기본 Activity 에서 시작해 곡 목록 UI, asset 기반 데이터 로딩, 음악 미리듣기, `a2dg` 기반 게임 화면, note 생성과 음악 시간 동기화까지 단계적으로 확장한다.

## 현재 상태

- [x] Android project skeleton 생성
- [x] 기본 `MainActivity` 생성
- [x] `README.md` 로 진행 계획 정리
- [ ] `viewBinding` / `buildConfig` 활성화
- [ ] 곡 목록 화면 구성
- [ ] 곡 선택 후 게임 화면 진입
- [ ] 음악 시간에 맞춘 note 표시

## Project Setup

- [x] 앱 패키지와 이름 정리
- [x] 기본 `MainActivity` 확인
- [ ] `buildFeatures` 에 `viewBinding` / `buildConfig` 활성화
- [ ] `MainActivity` 에 view binding 적용
- [ ] package 를 역할별로 분리
  - [ ] `app`
  - [ ] `data`
  - [ ] `game`
  - [ ] `res`
- [ ] 수업 단계에 맞게 불필요한 template code 정리
- [ ] 필요한 resource / asset 폴더 구조 준비

## Chart Data Tools

- [ ] 음원 chart 정보를 수집하는 tool 준비
- [ ] browser developer console 에서 실행할 chart 수집 script 작성
- [ ] chart JSON 에서 필요한 field 확인
- [ ] thumbnail download script 작성
- [ ] thumbnail 이미지를 asset 으로 가져오기
- [ ] mp3 파일을 asset 으로 가져오기
- [ ] note data 자동 생성 script 작성
- [ ] BPM 기반 note 생성 실험
- [ ] 곡별 BPM 값 확인 및 기록

## Song Data

- [ ] `songs.json` asset 추가
- [ ] `Song` data class 작성
- [ ] `SongLoader` 작성
- [ ] JSON 으로부터 rank 만 읽어 보기
- [ ] JSON 으로부터 title 읽기
- [ ] artist / album / demo 구간 / bpm field 추가
- [ ] JSON field 가 늘어날 때 parsing code 가 커지는 문제 확인
- [ ] reflection 기반 `JsonHelper` 또는 Kotlin 방식의 parser 도입 검토
- [ ] 필요한 field 만 읽고 나머지는 무시
- [ ] thumbnail 파일명 규칙 정리
- [ ] `Song` 이 자신의 thumbnail bitmap 을 로드하도록 책임 이동
- [ ] default thumbnail 처리

## Main Screen UI

- [ ] main layout 에 title / song list / start button 배치
- [ ] 처음에는 `ListView` 또는 단순 list 로 곡 표시 실험
- [ ] adapter 가 item count 와 item view 를 제공하는 구조 설명
- [ ] item layout 추가
- [ ] item layout 에 view binding 적용
- [ ] rank / title / artist / album 표시
- [ ] thumbnail 표시
- [ ] 구분선 추가
- [ ] `RecyclerView` 로 전환
- [ ] `RecyclerView.Adapter` / `ViewHolder` 구조 정리
- [ ] view 재활용 동작 확인
- [ ] item click 처리
- [ ] 선택된 곡을 로그로 확인
- [ ] 선택 상태를 item background 로 표시
- [ ] 같은 item 을 다시 누르면 선택 해제
- [ ] 선택된 곡이 있을 때만 Start Game button 활성화

## Demo Playback

- [ ] 곡 선택 시 미리듣기 재생
- [ ] 다른 곡을 선택하면 이전 미리듣기 중지
- [ ] 같은 곡을 다시 누르면 선택 해제 및 재생 중지
- [ ] `Song` 에 `demoStart` / `demoEnd` 추가
- [ ] 미리듣기는 `demoStart` ~ `demoEnd` 구간만 재생
- [ ] Activity pause 시 미리듣기 중지
- [ ] Activity resume 시 선택 상태 정리
- [ ] `play()` 와 `playDemo()` 의 공통 `MediaPlayer` 준비 code 정리

## Game Activity

- [ ] `MainGameActivity` 추가
- [ ] Start Game button 에서 `MainGameActivity` 실행
- [ ] Activity 간 데이터 전달 방법 비교
  - [ ] 선택 index 를 Intent extra 로 전달
  - [ ] 같은 process 안에서 static/shared selected value 사용
  - [ ] 객체를 직접 전달할 때의 process / serialization 문제 설명
- [ ] 올해 프로젝트 구조에 맞는 곡 선택 전달 방식 결정
- [ ] `a2dg` module 추가
- [ ] `settings.gradle.kts` 에 `:a2dg` 등록
- [ ] app module 이 `a2dg` module 을 dependency 로 사용
- [ ] `MainGameActivity` 가 `BaseGameActivity` 기반 게임 화면을 표시
- [ ] Debug build 에서 grid / FPS / debug info 표시 확인

## Main Scene

- [ ] `MainScene` 생성
- [ ] 빈 Scene 을 push 하여 game loop 동작 확인
- [ ] Layer 정의
  - [ ] `BG`
  - [ ] `NOTE`
  - [ ] `UI`
- [ ] 선택된 `Song` 을 `MainScene` 으로 전달
- [ ] `MainScene.onEnter()` 에서 음악 재생
- [ ] `MainScene.onExit()` 에서 음악 정지
- [ ] Scene pause / resume 에서 음악 pause / resume 처리
- [ ] Scene 과 Activity pause 의 차이 설명

## Background

- [ ] 선택된 곡의 album cover 를 game scene 배경으로 사용
- [ ] `Sprite` 에 bitmap 을 직접 설정하는 기능 확인 또는 추가
- [ ] album cover bitmap 을 `MainScene` 에 전달
- [ ] cover 이미지를 화면 높이에 맞추어 배치
- [ ] 별도 배경 이미지를 overlay 로 추가
- [ ] blurred cover 배경 실험
- [ ] blur 구현 방식과 deprecated API 주의점 설명
- [ ] thumbnail 해상도 개선 필요성 확인

## Note Data

- [ ] note asset 파일 추가
- [ ] `Note` data class 작성
- [ ] note file 로부터 note 목록 읽기
- [ ] note line format 정의
- [ ] 단순 split parsing 과 regex parsing 비교
- [ ] `pret` / `time` 값 읽기
- [ ] load 시점부터 millisecond 를 second `Float` 으로 변환
- [ ] 잘못된 line 은 무시
- [ ] 곡별 note file 이름 규칙 정리
- [ ] `Song.loadNotes()` 구현
- [ ] `Song` 이 note 생성 진행 index 를 기억
- [ ] 특정 시간 이전 note 를 하나씩 꺼내는 함수 구현

## Note Object

- [ ] `NoteSprite` 구현
- [ ] note image resource 추가
- [ ] 임시 note 2개를 화면에 배치
- [ ] note 의 `pret` 값으로 x 좌표 결정
- [ ] note 의 시간 값으로 y 좌표 결정 실험
- [ ] `MainScene.musicTime` 으로 현재 음악 시간 관리
- [ ] note 의 y 좌표를 `note.time - musicTime` 으로 계산
- [ ] goal line y 좌표 정의
- [ ] note speed 정의
- [ ] 화면 높이와 speed 로 note 가 미리 생성되어야 하는 시간 계산
- [ ] 필요한 시점이 되면 note 를 생성
- [ ] 화면 아래로 벗어난 note 제거
- [ ] note 객체 recycle bin 적용
- [ ] recycle 된 note 상태 초기화

## Note Animation

- [ ] `AnimSprite` 기반 `NoteSprite` 실험
- [ ] 모든 note 가 같은 frame 을 보여주도록 시작 시간 보정
- [ ] 곡에 BPM 이 있으면 animation fps 를 BPM 에 맞춤
- [ ] 1박당 8 frame 기준으로 fps 계산
- [ ] `a2dg` 의 animation fps 변경 API 필요 여부 확인

## Game Controls

- [ ] Back button 추가
- [ ] Back button 으로 song list 화면 복귀
- [ ] Speed button 추가
- [ ] 1x / 2x speed toggle
- [ ] speed resource icon 추가
- [ ] speed 값이 상수가 아니므로 runtime variable 로 변경
- [ ] speed 변경을 즉시 바꾸는 방식 실험
- [ ] `ValueAnimator` 로 speed 를 부드럽게 변경
- [ ] speed 변경 시 note 생성 lead time 과 y 좌표 계산이 함께 맞는지 확인

## Rhythm Gameplay

- [ ] 판정 line 표시
- [ ] touch 입력 처리
- [ ] 입력 위치를 pret lane 으로 변환
- [ ] 가장 가까운 note 찾기
- [ ] note 와 입력 시간 차이 계산
- [ ] perfect / good / miss 판정 기준 정의
- [ ] 판정 결과 표시
- [ ] 맞춘 note 제거
- [ ] 놓친 note miss 처리
- [ ] score / combo 표시
- [ ] 곡 종료 조건 처리
- [ ] 결과 화면 또는 결과 로그 표시

## Polish

- [ ] resource 이름 정리
- [ ] asset 파일 크기와 git 포함 여부 점검
- [ ] mp3 등 큰 파일 관리 방식 정리
- [ ] 로그 정리
- [ ] 문자열 생성 비용이 있는 log 는 debug build 에서만 수행
- [ ] Activity / Scene lifecycle 정리
- [ ] `MediaPlayer` release 필요성 검토
- [ ] image bitmap cache / recycle 정책 점검
- [ ] draw/update 중 객체 생성이 일어나지 않도록 member 재사용
- [ ] README 진행 상황 갱신
