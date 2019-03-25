# RxAndroid
## RxAndroid가 뭔가요?
* 기존 안드로이드 개발에서 가장 어려움을 겪는 문제 중 하나는 복잡한 스레드 사용입니다.
* 복잡한 스레드 사용으로 인해 발생하는 문제는 다음과 같습니다.
    * 안드로이드의 비동기 처리 및 에러 핸들링.
    * 수많은 핸들러와 콜백 때문에 발생하는 디버깅 문제
    * 2개의 비동기 처리 후 결과를 하나로 합성하는 작업
    * 이벤트 중복 실행
* 숙련도 높은 개발자도 멀티 스레드 환경에서 발생하는 이러한 문제를 디버깅하는 데 많은 시간을 투자합니다.

* RxAndroid는 습득하기 어려운 부분도 있지만 기존 안드로이드 개발과 비교했을 때 장점이 많습니다.
    * 간단한 코드로 복잡한 병행 프로그래밍을 할 수 있습니다.
    * 비동기 구조에서 에러를 다루기 쉽습니다.
    * 함수형 프로그래밍 기법도 부분적으로 적용할 수 있습니다.

### 리액티브 라이브러리와 API

* 기본적으로 RxJava의 리액티브 라이브러리를 이용합니다.

* 안드로이드에서 이용하는 리액티브 API와 라이브러리는 상당히 많습니다.
* RxJava 1.x 이하 버전에서는 각종 컴포넌트의 이벤트 처리 부분도 하나의 라이브러리에 포함되어 있었습니다.
* 하지만 경량화 작업과 유지보수의 문제로 각 기능을 다른 라이브러리로 분리했습니다.
* 다음은 API 및 라이브러리 목록입니다.
    * RxLifecycle
    * RxBinding
    * SqlBrite
    * Android-ReactiveLocation
    * RxLocation
    * rx-preferences
    * RxFit
    * RxWear
    * RxPermissions
    * RxNotification
    * RxClipboard
    * RxBroadcast
    * RxAndroidBle
    * RxImagePicker
    * ReactiveNetwork
    * ReactiveBeacons
    * RxDataBinding

### 안드로이드 스튜디오 설정
~~~gradle
dependencies {
    // Rx Utils dependencies
    // RxJava 2.x
    implementation 'io.reactivex.rxjava2:rxjava:2.1.3'
    implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'

    // RxLifecycle 2.x
    implementation 'com.trello.rxlifecycle2:rxlifecycle-android:2.1.0'
    implementation 'com.trello.rxlifecycle2:rxlifecycle:2.1.0'
    implementation 'com.trello.rxlifecycle2:rxlifecycle-components:2.1.0'

    // Utils
    implementation 'com.jakewharton.rxbinding:rxbinding:2.0.0'

    // Utils - butterknife
    implementation 'com.jakewharton:butterknife:8.8.1'
    annotationProcessor 'com.jakewharton:butterknife-compiler:8.8.1'
}
~~~
* dependencies 부분에 RxAndroid 라이브러리를 추가해야 합니다.
* 참고로 RxAndroid는 RxJava에 대한 의존성이 있어 RxJava를 추가하지 않아도 되지만, 최신 버전의 RxJava를 사용하려면 명시해주는 것이 좋습니다.

