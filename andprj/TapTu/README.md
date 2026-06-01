# TapTu

Android 2D game programming 수업에서 진행할 리듬 게임 예제 프로젝트이다.

플레이어는 곡을 선택하고, 음악 시간에 맞추어 내려오는 note 를 처리한다. 프로젝트는 Android 기본 Activity 에서 시작해 곡 목록 UI, asset 기반 데이터 로딩, 음악 미리듣기, `a2dg` 기반 게임 화면, note 생성과 음악 시간 동기화까지 단계적으로 확장한다.

## 현재 상태

- [x] Android project skeleton 생성
- [x] 기본 `MainActivity` 생성
- [x] `README.md` 로 진행 계획 정리
- [x] `viewBinding` / `buildConfig` 활성화
- [ ] 곡 목록 화면 구성
- [ ] 곡 선택 후 게임 화면 진입
- [ ] 음악 시간에 맞춘 note 표시

## Project Setup

- [x] 앱 패키지와 이름 정리
- [x] 기본 `MainActivity` 확인
- [x] `buildFeatures` 에 `viewBinding` / `buildConfig` 활성화
- [ ] `MainActivity` 에 view binding 적용
- [ ] package 를 역할별로 분리
  - [x] `app`
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
- [x] thumbnail 이미지를 asset 으로 가져오기
- [ ] mp3 파일을 asset 으로 가져오기
- [ ] note data 자동 생성 script 작성
- [ ] BPM 기반 note 생성 실험
- [ ] 곡별 BPM 값 확인 및 기록

`assets/mp3/` 에는 곡별 mp3 파일을 둘 수 있지만, mp3 는 크기가 크므로 git 에 포함하지 않는다. `app/src/main/assets/.gitignore` 에서 `mp3/` 를 제외하고, 필요한 파일은 수업 자료 링크에서 받아 로컬 `assets/mp3/r_001.mp3` 형식으로 배치한다. `Song.mp3AssetPath` 는 `rank` 를 이용해 이 파일명을 만든다.

### `chart_grab.js` 실행 방법

곡 목록은 NAVER VIBE 의 차트 페이지에서 수집한다. 브라우저에서 `https://vibe.naver.com/chart/total` 에 접속한 뒤, chart list 가 화면에 실제로 보이는 상태에서 개발자 도구 Console 에 `chart_grab.js` 내용을 붙여 넣어 실행한다.

이 스크립트는 서버 API 를 직접 호출하는 방식이 아니라, 브라우저에 렌더링된 HTML 요소를 읽는 방식이다. 그래서 VIBE 사이트의 class 이름이나 DOM 구조가 바뀌면 그대로 동작하지 않을 수 있다. 실행 전에 Console 에서 다음을 먼저 확인한다.

```js
document.querySelector('.track_section')
```

결과가 `null` 이 아니면, 작년 스크립트가 기대하는 chart section 을 찾은 것이다. 이어서 다음 코드로 row 가 잡히는지도 확인한다.

```js
document.querySelectorAll('.track_section tr').length
```

0보다 큰 값이 나오면 `chart_grab.js` 를 실행할 수 있다. 만약 `null` 또는 `0` 이 나오면 VIBE 의 DOM 구조가 바뀐 것이므로, 스크립트 안의 selector 를 현재 사이트 구조에 맞게 수정해야 한다.

스크립트가 정상 실행되면 Console 에 `rank`, `title`, `artist`, `album`, `thumbnail` 을 가진 object 배열과 JSON 문자열이 함께 출력된다. 이 중 JSON 문자열을 복사해서 `songs.json` 으로 저장한다. 즉, `songs.json` 은 직접 손으로 작성하는 파일이 아니라 `chart_grab.js` 가 Console 에 출력한 JSON 을 파일로 저장한 것이다. 웹 페이지에서 복사된 text 에는 non-breaking space 인 `U+00A0` 가 섞일 수 있으므로, `chart_grab.js` 는 이를 일반 공백으로 바꾸고 연속 공백을 하나로 줄인 뒤 JSON 을 만든다.

`thumbnail` 은 이미지 파일 자체가 아니라 URL 이다. 이후 `thumbnail_downloader.py` 가 `songs.json` 을 읽고 각 URL 의 이미지를 내려받아 `cover_001.jpg`, `cover_002.jpg` 같은 파일명으로 저장한다.

## Song Data

- [x] `songs.json` asset 추가
- [x] `Song` data class 작성
- [x] `SongLoader` 작성
- [x] `SongCatalog` 로 읽어 온 곡 목록 보관
- [x] JSON 으로부터 rank 만 읽어 보기
- [x] JSON 으로부터 title 읽기
- [x] artist / album field 추가
- [x] Kotlin `kotlinx.serialization` parser 도입
- [x] 필요한 field 만 읽고 나머지는 무시
- [x] thumbnail 파일명 규칙 정리
- [x] `Song` 이 자신의 thumbnail bitmap 을 로드하도록 책임 이동
- [x] default thumbnail 처리

### `@Transient` 와 `runCatching`

`Song` 은 `@Serializable` data class 이므로, 기본적으로 constructor 에 있는 property 들이 JSON 과 연결된다. 그런데 thumbnail bitmap 은 JSON 에서 읽는 값이 아니라 앱 실행 중 asset 파일을 열어서 만든 runtime 객체이다. 이런 값은 JSON parsing 대상이 아니므로 `@Transient` 로 표시해 둔다.

`@Transient` 는 kotlinx.serialization 에게 "이 property 는 직렬화/역직렬화 대상에서 제외한다"고 알려 준다. 그래서 `private var thumbnailBitmap: Bitmap?` 처럼 메모리에만 들고 있을 cache 를 `Song` 안에 둘 수 있다. 단, `@Transient` property 는 JSON 으로부터 값이 들어오지 않으므로 기본값이 있어야 한다.

`runCatching { ... }` 은 block 안에서 예외가 발생할 수 있는 코드를 감싸고, 성공/실패를 `Result` 로 다루게 해 주는 Kotlin 표준 함수이다. 여기서는 asset 파일이 없거나 이미지 decode 에 실패해도 앱이 바로 crash 하지 않도록 하기 위해 사용한다. `getOrNull()` 을 붙이면 성공하면 bitmap 을, 실패하면 `null` 을 얻는다.

즉 현재 thumbnail 로드는 다음 의도를 가진다.

- 정상 파일이 있으면 `BitmapFactory.decodeStream()` 으로 bitmap 을 만들고 cache 한다.
- 파일이 없거나 열 수 없으면 `null` 을 반환해 다음 단계의 default thumbnail 처리로 넘길 수 있게 한다.
- 한 번 로드한 bitmap 은 `thumbnailBitmap` 에 저장해 RecyclerView 가 같은 row 를 다시 bind 할 때 반복 decode 하지 않는다.

## Main Screen UI

- [x] main layout 에 title / song list / start button 배치
- [ ] 처음에는 `ListView` 또는 단순 list 로 곡 표시 실험
- [x] adapter 가 item count 와 item view 를 제공하는 구조 설명
- [x] item layout 추가
- [x] item layout 에 view binding 적용
- [x] rank / title / artist / album 표시
- [x] thumbnail 표시
- [x] 구분선 추가
- [x] `RecyclerView` 로 전환
- [x] `RecyclerView.Adapter` / `ViewHolder` 구조 정리
- [x] view 재활용 동작 확인
- [x] item click 처리
- [x] 선택된 곡을 로그로 확인
- [x] 선택 상태를 item background 로 표시
- [x] 같은 item 을 다시 누르면 선택 해제
- [x] 선택된 곡이 있을 때만 Start Game button 활성화
- [x] 상단 정사각형 preview 영역에 선택된 앨범 cover 표시
- [x] 선택된 곡 title / artist / album 을 preview 위에 overlay
- [x] 선택 곡 변경 시 preview fade / scale animation 적용
- [x] 가로 화면에서는 preview 와 곡 목록을 좌우로 배치
- [x] Fold 류 화면에서도 preview 가 세로 높이 또는 가로 폭의 절반을 넘지 않도록 제한

### `ListView` 와 `RecyclerView`

곡 목록은 `ListView` 로도 만들 수 있지만, 올해 프로젝트에서는 `RecyclerView` 를 기본 선택으로 둔다.

`ListView` 는 오래된 목록 UI 이다. adapter 가 item view 를 만들어 주고, `convertView` 를 재사용해서 성능을 챙기는 구조이다. 간단한 세로 목록에는 여전히 사용할 수 있지만, ViewHolder 패턴을 직접 관리해야 하고 item animation, 여러 view type, layout 확장에는 불리하다.

`RecyclerView` 는 재사용 구조를 더 명시적으로 만든 목록 UI 이다. `ViewHolder` 가 필수이고, `LayoutManager` 로 세로 목록, 가로 목록, grid 등을 바꿀 수 있다. thumbnail, title, artist, album 을 함께 보여 주는 TapTu 의 곡 목록처럼 item UI 가 조금 복잡해질 때 더 적합하다.

수업에서는 "목록은 `addView()` 로 직접 쌓는 것이 아니라 adapter 가 data 를 view 로 바꿔 준다"는 개념이 중요하다. 이 개념은 `ListView` 와 `RecyclerView` 모두에 있지만, 현대 Android 에서는 `RecyclerView` 를 더 많이 사용하므로 올해는 `ListView` 를 길게 구현했다가 갈아타기보다 `RecyclerView` 중심으로 진행한다.

### `RecyclerView` 개념과 활용 방법

`RecyclerView` 는 많은 item 을 효율적으로 보여 주기 위한 Android 목록 UI 이다. 이름 그대로 화면 밖으로 사라진 item view 를 버리지 않고 재사용한다. 예를 들어 곡이 100개 있어도 화면에 실제로 보이는 row 는 몇 개뿐이므로, 모든 row view 를 한꺼번에 만들 필요가 없다.

`RecyclerView` 를 사용할 때 핵심 구성요소는 세 가지이다.

- `RecyclerView`: 화면에 배치되는 목록 view 자체이다. layout xml 에 들어가며, 실제로 item 들을 스크롤하며 보여 준다.
- `Adapter`: data 목록을 `RecyclerView` 가 이해할 수 있는 item view 로 바꿔 주는 객체이다. "몇 개의 item 이 있는가", "새 row view 는 어떻게 만드는가", "특정 위치의 data 를 row 에 어떻게 표시하는가"에 답한다.
- `ViewHolder`: item view 하나와 그 안의 child view 들을 붙잡고 있는 객체이다. `findViewById()` 를 매번 반복하지 않고, 재사용되는 row view 를 빠르게 갱신하기 위해 사용한다.

Adapter 의 주요 함수는 다음 순서로 이해하면 된다.

- `getItemCount()`: data 가 몇 개인지 알려 준다. TapTu 에서는 `SongCatalog.songs.size` 가 된다.
- `onCreateViewHolder()`: 새 row view 가 필요할 때 호출된다. 여기서 `song_item.xml` 을 inflate 하고 `SongViewHolder` 를 만든다.
- `onBindViewHolder()`: 특정 위치의 data 를 기존 row view 에 표시할 때 호출된다. TapTu 에서는 `Song` 의 title, artist, album, thumbnail 을 row 에 넣는다.

즉 `RecyclerView` 는 직접 data 를 알지 않는다. `RecyclerView` 는 Adapter 에게 "몇 개니?", "view 하나 만들어 줘", "이 위치의 data 를 이 view 에 표시해 줘"라고 물어본다. 실제 data 를 어떻게 보여 줄지는 Adapter 와 ViewHolder 가 담당한다.

TapTu 에서는 다음 순서로 적용한다.

1. `activity_main.xml` 에 임시 곡 선택 button 대신 `RecyclerView` 를 배치한다.
2. 곡 하나를 표시할 `song_item.xml` 을 만든다.
3. `SongAdapter` 를 만들고 `SongCatalog.songs` 를 data source 로 사용한다.
4. `SongViewHolder` 에서 view binding 으로 item view 를 잡는다.
5. `onBindViewHolder()` 에서 `Song` 의 title, artist, album 을 표시한다.
6. 이후 단계에서 thumbnail bitmap 을 asset 에서 읽어 표시한다.
7. item click 으로 선택 상태를 바꾸고, 선택된 곡이 있을 때만 Start Game button 을 활성화한다.

이 구조를 쓰면 곡 목록 표시, 선택 상태 표시, thumbnail 표시, item click 처리를 모두 같은 Adapter 안에서 단계적으로 확장할 수 있다.

## Demo Playback

- [x] `Song` 이 자신의 mp3 asset path 를 알도록 구현
- [x] 곡 선택 시 미리듣기 재생
- [x] 다른 곡을 선택하면 이전 미리듣기 중지
- [x] 같은 곡을 다시 누르면 선택 해제 및 재생 중지
- [ ] `Song` 에 `demoStart` / `demoEnd` 추가
- [ ] 미리듣기는 `demoStart` ~ `demoEnd` 구간만 재생
- [x] Activity pause 시 미리듣기 중지
- [x] Activity pause 시 선택 상태 정리
- [ ] `play()` 와 `playDemo()` 의 공통 `MediaPlayer` 준비 code 정리

## Game Activity

- [x] `MainGameActivity` 추가
- [x] Start Game button 에서 `MainGameActivity` 실행
- [x] Activity 간 데이터 전달 방법 비교
  - [x] 선택 index 를 Intent extra 로 전달
  - [x] 같은 process 안에서 static/shared selected value 사용
  - [x] 객체를 직접 전달할 때의 process / serialization 문제 설명
- [x] 올해 프로젝트 구조에 맞는 곡 선택 전달 방식 결정
- [ ] `a2dg` module 추가
- [ ] `settings.gradle.kts` 에 `:a2dg` 등록
- [ ] app module 이 `a2dg` module 을 dependency 로 사용
- [ ] `MainGameActivity` 가 `BaseGameActivity` 기반 게임 화면을 표시
- [ ] Debug build 에서 grid / FPS / debug info 표시 확인

### Activity 간 곡 선택 전달 방식

`MainActivity` 는 곡 목록을 보여 주고, `MainGameActivity` 는 선택된 곡으로 게임을 시작한다. 두 Activity 사이에는 "어떤 곡을 골랐는가"를 전달해야 한다. 현재 TapTu 는 `Intent` extra 로 선택된 곡의 index 만 전달한다.

```mermaid
flowchart LR
    A["MainActivity<br/>SongCatalog.songs 표시"] --> B["사용자가 곡 선택<br/>selectedPosition 저장"]
    B --> C["Start Game click"]
    C --> D["Intent extra<br/>songIndex 전달"]
    D --> E["MainGameActivity"]
    E --> F["SongCatalog.load(assets)"]
    F --> G["songs[songIndex] 로 선택 곡 확인"]
```

이 방식은 `Song` 객체 전체를 넘기는 것이 아니라, 같은 `songs.json` 목록에서 몇 번째 곡인지 알려 주는 방식이다. 그래서 Activity 간 전달 값은 작고 단순하며, 게임 화면은 필요할 때 `SongCatalog` 를 다시 로드해서 같은 index 의 곡을 찾는다.

대안은 여러 가지가 있다.

| 방식 | 예시 | 장점 | 단점 | 현재 판단 |
| --- | --- | --- | --- | --- |
| `Intent` extra 로 index 전달 | `putExtra("songIndex", position)` | Android Activity 이동 방식과 잘 맞고 값이 작다. process 재생성 후에도 extra 가 남아 있다. | index 가 목록 순서에 의존하므로 양쪽이 같은 songs.json 을 읽어야 한다. | 현재 채택 |
| `Intent` extra 로 id 전달 | `putExtra("songId", id)` | 목록 순서가 바뀌어도 안정적이다. 서버/저장 데이터와 연결하기 좋다. | 현재 `Song` 에 고유 id 가 없으므로 아직 쓸 수 없다. | 추후 id 가 생기면 더 좋음 |
| static/shared selected value 사용 | `SongCatalog.selectedSong = song` | 구현이 짧고 객체를 다시 찾지 않아도 된다. 같은 process 안에서는 편하다. | Android 가 Activity/process 를 재생성하면 값이 사라질 수 있다. Activity 간 계약이 코드 밖으로 숨어서 테스트와 추적이 어렵다. | 수업 설명용 대안 |
| 객체 전체 전달 | `Parcelable` / `Serializable` 로 `Song` 전달 | 받는 쪽에서 바로 객체를 사용할 수 있다. | 객체가 커지면 전달 비용이 커지고, Bitmap 같은 runtime 객체는 전달 대상이 아니다. Serializable 은 느리고 Android 에서는 Parcelable 준비가 필요하다. | 지금은 과함 |
| repository/cache 에 저장 후 key 전달 | key 만 extra 로 전달하고 repository 에서 조회 | 데이터가 커져도 Activity extra 는 작게 유지된다. 앱 구조가 커졌을 때 자연스럽다. | repository lifetime 과 cache invalidation 을 설계해야 한다. 지금 단계에는 구조가 무겁다. | 이후 확장 후보 |

따라서 지금 단계에서는 `Intent` extra 로 `songIndex` 를 전달한다. 곡 목록은 이미 `assets/songs.json` 으로 앱 안에 들어 있고, `MainActivity` 와 `MainGameActivity` 가 같은 파일을 읽으므로 index 로 같은 곡을 가리킬 수 있다. 나중에 곡 데이터에 안정적인 `id` 를 추가하면 index 대신 id 를 넘기는 방식으로 바꾸는 것이 더 안전하다.

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
- [x] mp3 등 큰 파일 관리 방식 정리
- [ ] 로그 정리
- [ ] 문자열 생성 비용이 있는 log 는 debug build 에서만 수행
- [ ] Activity / Scene lifecycle 정리
- [ ] `MediaPlayer` release 필요성 검토
- [ ] image bitmap cache / recycle 정책 점검
- [ ] draw/update 중 객체 생성이 일어나지 않도록 member 재사용
- [ ] README 진행 상황 갱신
