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

