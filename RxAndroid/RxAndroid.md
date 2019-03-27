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

## 리액티브 라이브러리와 API

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

## 안드로이드 스튜디오 설정
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

## RxAndroid의 기본 개념

* RxAndroid의 기본 개념은 RxJava와 동일합니다.
* RxJava의 구조에 안드로이드의 각 컴포넌트를 사용할 수 있게 변경해 놓은 것입니다.
* 따라서 RxAndroid의 구성요소는 다음처엄 RxJava의 구성 요소와 같습니다.
    * Observable: 비즈니스 로직을 이용해 데이터를 발행합니다.
    * 구독자: Observable에서 발행한 데이터를 구독합니다.
    * 스케줄러: 스케줄러를 통해서 Observable, 구독자가 어느 스레드에서 실행될지 결정할 수 있습니다.

~~~kotlin
// 1. Observable 생성
Observable.create()
    // 2. 구독자 이용
    .subscribe()

    // 3. 스케줄러 이용
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
~~~

* Observable과 구독자가 연결되면 스케줄러에서 각 요소가 사용할 스레드를 결정하는 기본적인 구조입니다.
* Observable이 실행되는 스레드는 subscribeOn() 함수에서 설정하고 처리된 결과를 observeOn() 함수에 설정된 스레드로 보내 최종 처리합니다.
* RxAndroid에서 제공하는 스케줄러는 무엇이 있을까요
    * AndroidSchedulers.mainThread(): 안드로이드 UI 스레드에서 동작하는 스케줄러입니다.
    * HandlerScheduler.from(handler): 특정 핸들러에 의존하여 동작하는 스케줄러입니다.

## Hello world
* Observable에서 문자를 입력받고 텍스트뷰에 결과를 출력합니다.
~~~java
Observable<String> observable = new DispossableObserver<String>() {
    @Override
    public void onNext(String s) {
        textView.setText(s);
    }

    @Override
    public void onError(Throwable e) { }
    @Override
    public void onComplete() { }
};

Observable.create(new ObservableOnSubsribe<String>() {
    @Override
    public void subscribe(ObservableEmitter<String> e) throws Exception {
        e.onNext("Hello world!");
        e.onComplete();
    } 
}).subscribe(observer);
~~~

* Observable.create()로 Observable을 생성해 "Hello world!"를 입력 받고 subscribe() 함수 안 onNext() 함수에 전달합니다.
* onNext() 함수의 정의를 보면 마지막으로 전달된 문자를 텍스트 뷰에 업데이트하게 되어 있습니다.
* 따라서 실제 구독자를 subscribe(observable) 함수를 통해 등록하고 호출하면 'Hello world'를 텍스트 뷰애 표시합니다.

### 람다로 표현하자!
~~~java
Observable<String>.create(s -> {
    s.onNext("Hello, world!");
    s.onComplete();
}).subscribe(o -> textView.setText(o));
~~~
* 콜백 함수를 람다 표현식으로 바꾸면서 데이터의 흐름이 명확해져 가독성이 좋아졌습니다.
* 전달자는 명확한 단어로 변경할 수도 있지만 기본적으로 이니셜을 많이 사용합니다.

### just() 함수를 이용하자
~~~java
Observable.just("Hello, world!")
    .subscribe(textView::setText);
~~~
* 메서드 레퍼런스를 이용하여 Observable의 생성 코드를 단순하게 했습니다.
* Observable의 생성 방법은 워낙 다양하고 개발자의 성향에 따라 선택의 기준이 달라질 수 있습니다.