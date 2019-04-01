# RxAndroidDebugging
## 디버깅
* 보통 코딩하는 도중에 로그를 넣는 이유는 잘못되었을 때 대처하기 위함입니다.
* 하지만 RxJava 코드는 로그를 넣을 수 있는 공간이 없습니다.
* Observable로 시작하는 업스트림과 그것을 받아서 처리하는 다운스트림이 동일한 문장으로 이루어져 있기 때문입니다.
* 전체 동작을 선언적으로 만들 수 있으므로 전체 맥락에 대한 가독성은 높아지지만 예외 코드를 어떻게 넣어야 하는지에 대한 어려움이 있습니다.

### doOnNext(), doOnComplete(), doOnError() 함수
* doOnNext(), doOnComplete(), doOnError() 라는 세 가지 함수는 Observable의 알림 이벤트에 해당합니다.
* Observable이 어떤 데이터를 발행할 때는 onNext, 중간에 에러가 발생하면 onError, 모든 데이터를 발행하면 onComplete 이벤트가 발생합니다.
* 이 세가지 함수는 이벤트에 맞게 원하는 내용을 확인할 수 있습니다.
~~~java
String[] orgs = {"1", "3", "5"};
Observable<String> source = Observable.fromArray(orgs);

source.doOnNext(data -> Log.d("onNext()", data))
    .doOnComplete(() -> Log.d("onComplete()"))
    .doOnError(e -> Log.e("onError()", e.getMessage()))
    .subscribe(Log::i);
~~~

* doOnNext(), doOnComplete(), doOnError() 함수에 로그를 출력하는 람다 표현식을 넣었습니다.
* 모두 main 스레드에서 실행되었고, 각 이벤트 발생시에는 debug 로그를 출력합니다.
* 실제로 Observable이 구독자에게 발행한 데이터는 value로 표시했습니다.
* 한 가지 흠이 있다면 onError 이벤트를 확인할 수 없다는 점인데 다음과 같이 보완할 수 있습니다.

~~~java
// 0으로 나눌 수 없다.
Integer[] divider = {10, 5, 0};

Observable.fromArray(divider)
    .map(div -> 1000/div)
    .doOnNext(data -> Log.d("onNext()", data))
    .doOnComplete(() -> Log.d("onCompolete()"))
    .doOnError(e -> Log.e("onError()", e.getMessage()))
    .subscribe(Log::i);
~~~

* Observable은 1000을 어떤 숫자로 나누며, 나누는 수로 10, 5, 0을 대입합니다.
* 어떤 숫자도 0으로는 나눌 수 없기 때문에 에러가 발생합니다.

* doOnError() 함수로 onError 이벤트가 발생했을 때의 에러 메시지인 '/ by zero'를 출력한 후 아래에는 표준 에러 입출력의 자세한 에러 메시지를 출력합니다.
* 에러 이름은 OnErrorNotImpementedException입니다. 이름에서 짐작할 수 있듯이 onError 이벤트에 대해서는 개발자가 어떤 조치를 해야 합니다.

### doOnEach() 함수
* 그 다음은 doOnEach() 함수입니다. onNext, onComplete, onError 이벤트를 각각 처리하는 것이 아니라 한번에 처리할 수 있기 때문에 편리합니다.
* 첫 번째는 Notification<T> 객체를 전달받아서 이벤트별로 구별하여 처리하는 방법입니다.
~~~java
String[] data = {"ONE", "TWO", "THREE"};
Observable<String> source = Observable.fromArray(data);

source.doOnEach(noti -> {
    if (noti.isOnNExt()) Log.d("onNext()",  noti.getValue());
    if (noti.isOnComplete()) Log.d("onComplete()");
    if (noti.isOnError()) Log.e("onError()", noti.getError().getMessage());
}).subscribe(System.out::println);
~~~
* Notification<T> 객체는 발생한 이벤트의 종류를 알 수 있는 boolean 타입의 isOnNext(), isOnComplete(), isOnError() 함수를 제공합니다.
* onNext() 함수의 경우 getValue() 함수를 호출하면 발행된 값을 알 수 있고 onError() 함수의 경우 getError() 함수를 호출하면 Throwable 객체를 얻어 올 수 있습니다.
* Notification<T> 객체를 사용하지 않고 Observer 인터페이스를 사용하는 방법도 있습니다.
* Observer 인터페이스는 Observable의 subscribe() 함수를 호출할 때 인자로 전달하는 인터페이스입니다.

~~~java
String[] orgs = {"1", "3", "5"};
Observable<String> source = Observable.fromArray(orgs);

source.doOnEach(new Observer<String>() {
    @Override
    public void onSubscribe(Disposable  d) {
        //  doOnEach()에서는 onSubscribe() 함수가 호출되지 않습니다.
    }

    @Override
    public void onNext(String value) {
        Log.d("onNext()", value);
    }

    @Override
    public void onError(Throwable e) {
        Log.e("onError()", e.getMessage());
    }

    @Override
    public void onComplete() {
        Log.d("onComplete()");
    }
}).subscribe(Log::i);
~~~

* 한 가지 특이한 점은 Observer 객체를 인자로 전달받았으나 onSubscribe() 함수는 호출되지 않습니다.
* doOnEach() 함수는 오직 onNext, onError, onComplete 이벤트만 처리하기 때문입니다.
* 하지만 Notification<T> 를 활용하는 것이 더 간결한 코드를 유지할 수 있습니다.

### doOnSubscribe(), doOnDispose(), 기타 함수
* Observable의 알림 이벤트 중에는 onSubscribe와 onDispose 이벤트도 있습니다.
* 각각 Observable을 구독했을 때와 구독 해지했을 때의 이벤트를 처리할 수 있습니다.
* doOnSubscribe() 함수는 Observable을 구독했을 때 어떤 작업을 할 수 있습니다.

* 람다 표현식의 인자로는 구독의 결과로 나오는 Disposable 객체가 제공됩니다.
* doOnDispose() 함수는 Observable의 구독을 해지 했을 때 호출되며 인자는 Action 객체입니다.
* 스레드 다수에서 Observable을 참조할 수 있기 때문에 Action 객체는 '스레드 안전'하게 동작해야 합니다.

~~~java
String[] orgs = {"1", "3", "5", "2",  "6"};
Observable<String> source = Observable.fromArray(orgs)
    .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS),  (a, b) -> a)
    .doOnSubscribe(d -> Log.d("onSubscribe()"))
    .doOnDispose(() -> Log.d("onDispose()"));

Disposable d = source.subscribe(Log::i);

CommonUtils.sleep(200);
d.dispose();
CommonUtils.sleep(300);
~~~

* 100ms 간격으로 orgs 배열의 데이터를 발행한 후 doOnSubscribe() 및 doOnDispose() 함수를 호출하여 로그를 출력합니다.
* Observable은 zipWith() 함수를 활용하여 interval() 함수와 합성했기 때문에 main 스레드가 아니라 계산 스케줄러에서 동작합니다.
* main 스레드는 200ms 후에 Observable을 구독 해지합니다.

* doOnSubscribe()와 doOnDispose() 함수를 각각 호출하지 않고 한꺼번에 호출하는 함수인 doOnLifecycle()도 있습니다.
* 다음 코드는 doOnLifeCycle() 함수로 변환한 것입니다.

~~~java
String[] orgs = {"1", "3", "5", "2", "6"};
Observable<String> source= Observable.fromArray(orgs)
    .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a, b) -> a)
    .doOnLifecycle(
        d -> Log.d("onSubscribe()"), () -> Log.d("onDispose()"));
Disposable d = source.subscribe(Log::i);
CommonUtils.sleep(200);
d.dispose();
CommonUtils.sleep(300);
~~~

* doOnSubscribe()와 doOnDispose() 함수와 각각 넣은 인자를 한 번에 넣을 수 있는 것만 다릅니다.
* 또한 doOnTerminate() 함수는 Observable이 끝나는 조건인 onCompolete 혹은 onError 이벤트가 발생했을 때 실행하는 함수입니다.
* 정확하게는 onComplete() 혹은 onError 이벤트 발생 직전에 호출합니다.

~~~java
String[] orgs = {"1", "3", "5"};
Observable<String> source = Observable.fromArray(orgs);

source.doOnTerminate(() -> Log.d("onTerminate()"))
    ,doOnComplete(() -> Log.d("onComplete()"))
    .doOnError(e -> Log.e("onError()", e.getMessage()))
    .subscribe(LogL::i);
~~~

* 마지막으로 doFinally() 함수는 onError, onComplete 혹은 onDispose 이벤트 발생 시에 호출됩니다.
