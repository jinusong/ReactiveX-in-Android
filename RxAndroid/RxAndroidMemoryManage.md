# RxAndroidMemoryManage
## 메모리 누수란?
* 메모리 누수란 보통 참조가 완료되었지만 할당한 메모리를 해제하지 않아서 발생합니다.
* 특히 강한 참조의 경우 갈비지 컬렉터가 메모리에서 객체를 제거할 수 없으므로 라이프 사이클에 맞게 객체 참조를 끊어야 사용하지 않는 메모리를 해제할 수 있습니다.
* 메모리 누수는 시스템 전체 성능에 영향을 미치므로 중요하게 관리해야 합니다.
### 메모리 누수를 찾아보자
~~~java
Observer<String> observer = new DisposableObserver<String>() {
    @Override
    public void onNext(String s) {
        textView.setText(s);
    }

    @Override
    public void onError(Throwable e) { }

    @Override
    public void onComplete() { }
};

Observable.create(new ObservableOnSubscribe<String() {
    @Override
    public void subscribe(ObservableEmitter<String> e) throws Exception {
        e.onNext("hello world!");
        e.onComplete();
    }
}).subscribe(observer);
~~~

* 정상 동작하며 큰 문제가 없어 보이지만 메모리 누수 문제가 있는 코드입니다. 
* Observable은 안드로이드의 컨텍스트를 복사하여 유지합니다.
* onComplete(), onError() 함수가 호출되면 내부에서 자동으로 unsubscribe() 함수를 호출합니다.

* 그런데 구독자가 텍스트뷰를 참조하기 때문에 액티비티가 비정상적으로 종료되면 텍스트뷰가 참조하는 액티비티는 종료해도 가비지 컬렉션의 대상이 되지 못합니다.
* 따라서 메모리 누수가 발생하게됩니다.

### 해결해볼래?
* 안드로이드 앱 프로그래밍은 액티비티의 라이프 사이클을 다루는 작업이라 해도 과언이 아닙니다.
* 안드로이드와 subscribe() 함수의 라이프사이클을 고혀하여 잘 설계해야 합니다.
* 이런 문제를 해결하기 위해 방법 3가지가 필요합니다.

#### Disposable 인터페이스를 이용하여 명시적으로 자원 해제
#### RxLifecycle 라이브러리 이용
#### CompositeDisposable 클래스 이용