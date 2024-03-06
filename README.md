# 서울맛칩 2.0</br>
서울시에서 선정한 착한가격업소에 대한 정보앱
</br>
🏆 「2023 서울 열린데이터광장 공공데이터 활용 모바일 앱/웹 경진대회」 일반부 **최우수상** 수상</br>
</br>
<img src="./img/그래픽.png">
</br>
<img src="./img/소개.png">
</br>

</br>

### 주요기능
화면|내용|기능
------|---|---
홈|현재 위치의 동네 정보와 서울시 착한가격업소 목록|- 우리 동네 착한가격업소</br>- 서울시 착한가격업소</br>- 추천 착한가격업소 
검색|서울시 착한가격업소 검색|- 검색어 히스토리</br>- 추천 검색어</br>- 검색어 자동완성</br>- 띄어쓰기 상관없이 부분검색 지원
지도|서울시 착한가격업소 지도|- Zoom 배율이 높을 때는 내 위치 중심 지도 제공</br>- Zoom 배율이 낮을 때는 서울시 모양의 숫자 지도 제공</br>- 이 지역 재검색</br>- 필터 : 지역, 카테고리, 거리, 즐겨찾기
업소 상세화면|착한가격업소 정보|- 업소 정보(소개, 정보, 메뉴, 위치)</br>- 연락하기, 길 찾기</br>- 즐겨찾기 저장</br>- SNS 공유하기
더보기|서울맛칩 앱 편의기능|- 즐겨찾기 관리</br> - 푸시알림 설정</br> - 앱 바로가기(Shortcut) 메뉴

</br>

## 레거시 서울맛칩 프로젝트를 리팩토링한 이유
- UI와 비즈니스 로직을 분리하고 MVVM 디자인 패턴으로 리팩토링하여 기능 확장과 유지 보수 성능 확보
- Activity, Fragment가 비즈니스 로직까지 전부 담고 있어서 이를 분리해 UI Class들의 코드량 최적화
- 불안정한 코드들(Coroutine 사용시 GlobalScope 사용, Context 객체 글로벌 참조 등)이 많아서 이를 보수하여 안정적인 앱으로 출시
- 중복 코드를 개선하고 가독성을 높히기 위해

</br>

## Skills
<img src="https://img.shields.io/badge/Android-34A853?style=flat&logo=android&logoColor=white"/> <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white"/> <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=firebase&logoColor=white"/> <img src="https://img.shields.io/badge/MVVM-000000?style=flat&logo=&logoColor=white"/> <img src="https://img.shields.io/badge/RESTful API-000000?style=flat&logo=&logoColor=000000"/> 
- **MVVM 디자인 패턴**</br>
  기존의 서울맛칩 레거시 프로젝트의 유지 보수 성능을 확보하고 기능을 확장하기 위해 ViewModel을 활용해 UI 영역과 비즈니스 로직 영역을 분리하여 MVVM 디자인 패턴으로 리팩토링</br>
- **의존성 주입 : Dagger-Hilt**</br>
  코드의 중복과 객체간 결합도 줄이기 위해 앱의 여러 컴포넌트에서 공통적으로 사용되는 객체를 한 곳에서 관리하는 Module을 만들고, Hilt annotation을 사용해서 각각의 컴포넌트에서 의존성 주입하여 사용</br>
- **Jetpack** : Lifecycles, LiveData, ViewModel, Room, Paging, DataBinding, Navigation, WorkManager
- **비동기 처리 : Coroutine** </br>
  간결한 비동기 처리 코드 작성과 백그라운드 작업시 메모리 성능 최적화를 위해 Coroutine을 사용해 비동기 처리 요청</br>
- **네트워크 통신** : Retrofit2, Okhttp3, Jsoup, Gson
- **위치정보 활용**</br>
  Naver Map SDK : 서울시 착한가격업소 지도 제작</br>
  FusedLocaionProvider : 정확성이 높은 사용자의 현재 위치 정보를 얻기 위해 사용</br>
- **앱 업데이트 관리** : Firebase RemoteConfig</br>
- **etc.**</br>
  Firebase Dynamic Link : 미리보기가 포함된 공유하기 기능 구현을 위해 사용</br>
  LeakCanary : 앱 메모리 누수 점검시 안드로이드 스튜디오 App Profiler와 함께 병행</br>
  Glide : 사진을 뷰에 로드하는 라이브러리</br>
  UI: ConstaintLayout, Motion Layout, ViewPager2, TabLayout, RecyclerView, Lottie

</br>

## App Data
- 서울시 착한가격업소 데이터 : 백엔드 서버를 구축할 수 없어 대안으로 로컬 DB에 서울시 착한가격업소 데이터베이스를 구축하고 Room을 활용해 사용
- 서울시 착한가격업소 메뉴 데이터 : Retrofit2, Okhttp3를 이용해 REST API(서울 열린데이터광장) 연동
- 우리동네 기상정보 : Retrofit2, Okhttp3를 이용해 REST API(공공데이터 포털) 연동
- 사용자의 위치 정보 : FusedLocationProviderClient LocationRequest를 사용하여 사용자의 현재 위치 정확성을 높힘
- 데이터 처리시 비동기 처리 코드 효율화: Coroutine을 사용하여 비동기 처리

</br>

## Update Note
<details><summary>2.1.2 (2024.02.15)</summary>
1. 앱 업데이트 링크 버그 수정</br>
2. 안드로이드14에서 푸시알림시 앱이 꺼지는 현상 수정</br>
</details>    
<details><summary>2.1.1 (2024.02.15)</summary>
1. 북마크 기능 안정화 : 반복된 북마크 추가시 오류가 발생하는 현상 수정</br>
2. 지도 기능 안정화 : 더이상 중복된 마커가 생성되지 않습니다.</br>
3. 이외의 버그 수정 : 검색 후 재검색시 스크롤이 리셋되지 않는 현상, 부자연스러운 UI 애니메이션 수정</br>
4. 코드 리팩토링으로 코드 최적화(중복코드 개선 및 유지보수 성능 향상)</br>
</details>    
<details><summary>2.1.0 (2023.09.7)</summary>
1. 앱의 로딩속도 개선 : 기존 5초 -> 2초</br>
2. 지도에서 "재검색"기능 추가</br>
3. 착한가격업소 사진이 나오지 않는 현상 수정</br>
</details>    
<details><summary>2.0.1 (2023.06.2)</summary>
출시 후 버그 수정 : 동네 정보 불러오지 못하는 문제, 메뉴 정보를 네트워크에서 불러오지 못하는 문제
</details>   
<details><summary>2.0.0 (2023.06.1)</summary>
서울맛칩2.0을 출시합니다. 서울맛칩은 서울시에서 선정한 착한가격업소 정보앱입니다!
</details>   

</br>

## Issue Note

+ [Location 사용 로직 변경](https://github.com/SANDY-9/Project_SeoulMatcheap2.0/pull/3)</br>
+ [지도 성능 개선](https://github.com/SANDY-9/Project_SeoulMatcheap2.0/pull/4)</br>
+ [프로젝트 패키지 구조 개선](https://github.com/SANDY-9/Project_SeoulMatcheap2.0/pull/8)</br>
+ [북마크 등록/삭제 로직 개선](https://github.com/SANDY-9/Project_SeoulMatcheap2.0/pull/10)</br>
+ [푸시알림 백그라운드 예약 방식 변경](https://github.com/SANDY-9/Project_SeoulMatcheap2.0/pull/14)

</br>

## Links
+ 서울맛칩 이전(1.0) 개발 버전 : [서울맛칩1.0 코드 보러가기](https://github.com/SANDY-9/Project_matcheap-1.0)</br>
+ 서울맛칩 개발기록 : [링크](https://reflective-goose-443.notion.site/0b89bddfb6334d178d6f6c45f1d71182?pvs=4)</br>
+ 서울맛칩 다운로드 : [링크](https://play.google.com/store/apps/details?id=com.sandy.seoul_matcheap)</br></br>
<img src="./img/링크.png">
