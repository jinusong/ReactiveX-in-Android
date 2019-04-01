# RxAndroidException
## 예외처리
* 예외처리를 하려면 기존 자바에서는 try-catch 문을 제공합니다.
~~~java
Observable<String> source = Observable.create(
    (ObservableEmitter<String> emitter) -> {
        emitter.onNext("1");
        emitter.onError(new Exception("Some Error"));
        emitter.onNext("3");
        emitter.onComplete();
    }
);

try {
    source.subscribe(Log::i);
} catch (Exception e) {
    Log.e(e.getMessage());
}
~~~

* 