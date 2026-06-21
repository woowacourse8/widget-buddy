# 🐾 위젯 버디 (Widget Buddy)

> **"앱을 켜지 않아도, 내 홈 화면에서 살아 숨 쉬는 나만의 픽셀 펫"**
>
> 우아한테크코스 프리코스 오픈 미션 프로젝트

<br>

## 📖 프로젝트 소개

**위젯 버디(Widget Buddy)** 는 안드로이드 홈 스크린 위젯 기반의 **방치형 펫 키우기 게임**입니다.

기존의 앱들은 펫을 보기 위해 앱을 실행해야 했지만, 위젯 버디는 스마트폰의 배경화면(홈 스크린)에 항상 상주합니다. 사용자가 앱을 보지 않을 때도 펫은 배고파하고, 심심해하며, 때로는 가출을 결심하기도 합니다.

**"어떻게 하면 안드로이드 환경에서 '살아있는' 느낌을 주는 펫을 구현할 수 있을까?"** 라는 고민에서 시작된 프로젝트로, 최신 안드로이드 UI 툴킷인 **Jetpack Glance**와 백그라운드 작업 처리를 위한 **WorkManager**를 깊이 있게 탐구하여 구현해냈습니다.

* **개발 기간:** 2025.11.05 ~ 2025.11.24 (3주)
* **배포 상태:** [Google Play Store 배포 완료](https://play.google.com/store/apps/details?id=com.starterkim.widgetbuddy&hl=ko)
<img width="910" height="587" alt="image" src="https://github.com/user-attachments/assets/45734bbc-80a6-48da-b5d0-89c1aa39a3b7" />

<br>

## 📺 프로젝트 전체 시연 영상

> 아래 이미지를 클릭하면 유튜브에서 전체 시연 영상을 확인하실 수 있습니다!

[위젯 버디 시연 영상 바로 가기](https://youtube.com/shorts/TjV3xrSg81Q?feature=share)


<br>

## 📸 주요 기능 및 시연

| 1. 알 부화 및 탄생 | 2. 실시간 상호작용 (밥주기) |
| :---: | :---: |
| <img width="220" alt="demo_hatch" src="https://github.com/user-attachments/assets/257024d2-2c5c-4e57-956f-0b76983b3cc4" /> | <img width="220" alt="demo_feedback" src="https://github.com/user-attachments/assets/dec01fb0-ef3b-47ae-a3df-6195237d113e" /> |
| 홈 화면에 위젯을 배치하고 클릭하면 알이 깨어나며 랜덤한 펫(뱁새/용)이 탄생합니다. | 펫의 상태에 따라 버튼이 활성화되며, 클릭 시 즉각적인 피드백(표정 변화, 대사)을 제공합니다. |

| 3. 시간의 흐름에 따른 상태 변화 | 4. 메인 앱 (펫의 방 & 꾸미기) |
| :---: | :---: |
| <img width="220" alt="demo_passive_update" src="https://github.com/user-attachments/assets/d9b0780b-e30c-433a-93c7-455051333f8d" /> | <img width="220" alt="demo_main_app" src="https://github.com/user-attachments/assets/3f9f25bc-aa9a-4e49-afda-e3dbe8801889" /> |
| 앱을 켜지 않아도 시간이 지나면 펫이 배고파하거나 심심해하며 사용자에게 신호를 보냅니다. | 메인 앱에서 '사랑 주기'를 통해 포인트를 모으면, 썰렁했던 펫의 방에 가구가 하나씩 추가됩니다. |

<br>

## 🎯 기술적 도전 과제와 해결

프리코스에서 학습한 Kotlin 언어와 객체지향 설계를 바탕으로, 생소한 안드로이드 위젯 환경에 도전했습니다. 특히 다음 세 가지 핵심 기술을 중점적으로 연구하고 적용했습니다.

### 1️⃣ Jetpack Glance를 활용한 선언형 위젯 UI 구현

* **도전:** 기존 안드로이드 위젯 방식인 `RemoteViews`는 XML 기반으로 복잡하고 유연성이 떨어졌습니다. Jetpack Compose와 유사한 최신 방식의 위젯 개발이 필요했습니다.
* **해결:** **Jetpack Glance**를 도입하여, 위젯 UI를 Kotlin 코드로 직관적으로 구성했습니다. `GlanceAppWidgetReceiver`와 `GlanceAppWidget`으로 생명주기를 관리하고, `actionRunCallback`을 활용하여 클릭 이벤트를 간결하게 처리했습니다.

### 2️⃣ '살아있는 펫'을 위한 타임스탬프 기반 백그라운드 로직

* **도전:** 안드로이드의 배터리 최적화 정책(Doze 모드) 때문에 정확한 시간에 백그라운드 작업을 실행하는 것이 불가능했습니다. 단순히 `WorkManager`를 주기적으로 실행하는 것만으로는 자연스러운 상태 변화를 만들기 어려웠습니다.
* **해결:** **WorkManager**와 함께 **'타임스탬프 기반 로직'** 을 고안했습니다. 백그라운드 작업이 *실제로 실행된 시점*에 `(현재 시간 - 마지막 업데이트 시간)`의 차이를 계산하고, 그동안 감소했어야 할 스탯을 한꺼번에 반영하여 끊김 없는 시간의 흐름을 구현했습니다.

### 3️⃣ DataStore를 활용한 위젯-앱 간 상태 동기화

* **도전:** 홈 스크린의 '위젯'과 별도 프로세스로 실행되는 '메인 앱'이 동일한 펫의 데이터(이름, 상태, 포인트)를 실시간으로 공유해야 했습니다.
* **해결:** `SharedPreferences` 대신 타입 안정성과 비동기 처리를 지원하는 **Jetpack DataStore**를 채택했습니다. 특히 `GlanceStateDefinition`을 구현하여 위젯과 앱이 완전히 동일한 DataStore 파일을 바라보게 함으로써, 단일 진실 공급원(SSOT) 구조를 완성했습니다.

<br>

## 🛠️ 기술 스택

* **Language:** Kotlin
* **UI (Widget):** **Jetpack Glance** (핵심 기술)
* **UI (App):** Jetpack Compose
* **Background:** **WorkManager**
* **Local DB:** **Jetpack DataStore** (Preferences)
* **Ad Network:** Google AdMob (보상형 광고)
* **IDE:** Android Studio
* **Version Control:** Git, GitHub

<br>

## 📂 프로젝트 구조
```
com.starterkim.widgetbuddy
├── data/             # DataStore 및 데이터 모델 (PetState, PetType Enum)
├── logic/            # 순수 Kotlin 비즈니스 로직 (PetStateCalculator - 테스트 용이성 확보)
├── ui/theme/         # Compose 테마 설정
├── util/             # 유틸리티 및 매퍼 (PetVisualMapper, PetDialogueMapper)
├── widget/           # Jetpack Glance 위젯 관련 코드 (UI, Receiver)
│   └── callbacks/    # 위젯 클릭 이벤트 처리 (Hatch, Feed, Play Callback)
├── MainActivity.kt   # 메인 앱 진입점 (Compose UI)
└── WidgetBuddyApp.kt # Application 클래스 (Hilt 등 설정)
```

<br>

## 🚀 회고 및 학습 경험

이번 오픈 미션은 콘솔 환경에서 학습한 Kotlin 지식을 안드로이드 플랫폼의 가장 특수한 영역인 '위젯'으로 확장하는 도전적인 과정이었습니다.

* **스스로 학습하는 힘:** 생소했던 Jetpack Glance와 WorkManager 공식 문서를 파고들며 기술을 익혔습니다. 수많은 시행착오와 디버깅 과정 자체가 큰 배움이었습니다.
* **설계 원칙의 적용:** 프리코스에서 강조한 '역할과 책임의 분리'를 적용하여, 비즈니스 로직(`PetStateCalculator`)과 UI 이벤트 처리(`Callback`)를 분리했습니다. 덕분에 안드로이드 의존성 없이 핵심 로직을 검증할 수 있었습니다.
* **완성의 가치:** 3주 전, 빈 화면에 알 아이콘 하나를 띄우는 것조차 버거웠던 프로젝트가 이제는 구글 플레이 스토어를 통해 실제 사용자의 폰에서 살아 움직이는 서비스가 되었습니다. 이 작은 펫이 가져다준 성취감은 앞으로의 개발 여정에 큰 자산이 될 것입니다.

<br>

> 📝 개발 과정에서 겪은 상세한 기술적 고민과 문제 해결 기록은 [개발 문서 (docs/)](./docs/) 폴더에서 확인하실 수 있습니다.

<br>

## 💬 커밋 컨벤션 (프리코스 규칙 준수)

* `feat`: 새로운 기능 추가
* `fix`: 버그 수정
* `docs`: 문서 수정 (README 등)
* `refactor`: 코드 리팩토링
* `test`: 테스트 코드 추가/수정
* `chore`: 빌드 설정, 기타 잡무

<br>

---
Move Fast and Break Nothing.
ⓒ 2025 Widget Buddy Project.
