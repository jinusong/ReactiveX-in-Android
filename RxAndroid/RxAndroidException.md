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

* RxJava의 내부코드를 보면 try-catch 문은 RxJava에서 활용할 수 없습니다.
* 추가로 함수 체인이나 Observable 내부에서 예외가 발생해도 onError 이벤트가 발생하고 try-catch 문으로는 해결할 수 없습니다.
* 그래서 RxJava에서는 다른 방식의 예외 처리 방법을 제공합니다.

### onErrorReturn() 함수
* 예외 처리하는 첫번째 방식은 예외가 발생했을 때 에러를 의미하는 다른 데이터로 대체하는 것입니다.
* onError 이벤트는 데이터 흐름이 바로 중단되므로 subscribe() 함수를 호출할 때 onError 이벤트를 처리하는 것은 Out Of Memory 같은 중대한 에러가 발생했을 때만 활용합니다.
* onErrorReturn()는 에러가 발생했을 때 내가 원하는 데이터로 대체하는 함수입니다.

* 앞의 3개의 데이터가 정상적으로 발행되고 마지막 데이터에서 어떤 에러가 발생하는 경우 onErrorReturn() 함수는 인자로 넘겼던 기본값을 대신 발행하고 onComplete 이벤트가 발생합니다.

~~~java
String[] grades = {"70", "88", "$100", "93", "83"}; // $100이 에러 데이터

Observable<Integer> source = ObservablefromArray(grades)
    .map(data -> Integer.parseInt(data))
    .onErrorReturn(e -> {
        if(e instanceof NumberFormatException) {
            e.printStackTrace();
        }
        return -1;
    });

source.subscribe(data -> {
    if(data < 0) {
        Log.e("Wrong Data found!");
        return;
    }

    Log.i("Grade is " + data);
});
~~~

* 먼저 Integer.parseInt() 메서드는 NumberFormatException이라고 하는 검증된 예외가 있습니다.
* RxJava에서는 try-catch 문이 동작하지 않기 때문에 onErrorReturn() 함수에서 처리하면 NumberFormatException 발생시 '-1'을 리턴합니다.
* subscribe() 함수는 성적 데이터를 처리하므로 0보다 커야 합니다.
* onErrorReturn() 함수에서 예외 발생 시 음수 값을 리턴했으므로 data가 0보다 작으면 에러 발생 여부를 판단하고 에러 로그를 출력합니다.

#### 함수를 활용하여 예외처리하는 것의 장점
* subscribe() 함수를 호출할 때 onError 이벤트를 처리하는 것이 아닌 onErrorReturn() 등의 함수를 활용하여 예외처리하는 것은 몇가지 장점이 있습니다.
    * 첫 번째는 예외 발생이 예상되는 부분을 선언적으로 처리할 수 있다는 점입니다.
    * 두 번째는 Observable을 생성하는 측과 구독하는 측이 서로 다를 수 있다는 점입니다.
        * 구독자는 Observable에서 발생할 수 있는 예외를 구독한 이후에 모두 파악하는 것이 어렵습니다.
        * 좀 더 구체적으로 말하자면 Observable에서 에러 가능성을 명시하지 않았는데 구독자가 필요한 예외 처리를 빠짐없이 하는 것은 어렵다는 뜻입니다.
        * 이럴 때 Observable을 생성하는 측에서 발생할 수 있는 예외처리를 미리 해두면 구독자는 선언된 예외 상황을 보고 그에 맞는 처리를 할 수 있습니다.

* 한번 onError로 처리한 것을 볼까요.

~~~java
String[] grades = {"70", "88", "$100", "93", "83"};

Observable<Integer> source = Observable.fromArray(grades)
    .map(data -> Integer.parseInt(data));

source.subscribe(
    data -> Log.i("Grade is " + data),
    e -> {
        if(e instanceof NumberFormatException) {
            e.printStatckTrace();
        }
        Log.e("Wrong Data found!");
    }
);
~~~

* onErrorReturn() 함수와 비슷한 함수로 onErrorReturnItem() 함수가 있습니다.
* onErrorReturn() 함수와 동일하지만 Throwable 객체를 인자로 전달하지 않기 때문에 코드는 좀 더 간결해집니다.

~~~java
String[] grades = {"70", "88", "$100", "93", "83"};

Observable<Integer> source = Observable.fromArray(grades)
    .map(data -> Integer.parseInt(data))
    .onErrorReturnItem(-1);

source.subscribe(data -> {
    if (data < 0) {
        Log.e("Wrong Data found!");
        return;
    }

    Log.i("Grade is " + data);
});
~~~

* Throwable 객체를 사용하지 않았기 때문에 코드의 가독성이 좀 더 높아집니다. 결과는 동일합니다.

### onErrorResumeNext() 함수
* onErrorReturn() 과 onErrorReturnItem() 함수는 에러가 발생한 시점에 특정 값으로 대체하는 것이었습니다.
* onErrorResumeNext()는 에러가 발생했을 때 내가 원하는 Observable로 대체하는 방법입니다.
* Observable로 대체한다는 것은 에러 발생 시 데이터를 교체하는 것뿐만 아니라 관리자에게 이메일을 보낸다던가 자원을 해제하는 등의 추가 작업을 해야 할 때 유용합니다.
* 에러가 발생했을 때 특정 값을 발행한다는 점은 onErrorReturn() 함수와 크게 다르지 않습니다. 특정 값을 원하는 Observable로 설정할 수 있다는 점이 다릅니다.

~~~java
String[] salesData = {"100", "200", "A300"};    // A300은 에러 데이터
Observable<Integer> = onParseError = Observable.defer(() -> {
    Log.d("send email to administrator");
    return Observable.just(-1);
}).subscribeOn(Schedulers.io());    // IO 스케줄러에서 실행됨.

Observable<Integer> source = Observable.fromArray(salesData)
    .map(Integer::parseInt)
    .onErrorResumeNext(onParseError);

source.subscribe(data -> {
    if(data < 0) {
        Log.e("Wrong Data found!!");
        return;
    }

    Log.i("Sales data : " + data);
});
~~~

* 에러가 발생했을 때 관리자에게 이메일을 보내고 '-1' 이라는 데이터를 발행하는 Observable로 대체합니다.
* onParseError 변수는 subscribe() 함수를 호출하여 IO 스케줄러에서 실행합니다.
* RxJava는 이처럼 내가 원하는 코드를 실행하는 스케줄러를 선언적으로 지정할 수 있어 활용 범위가 넓습니다.

* 참고로 onErrorResumeNext() 함수는 onErrorReturn() 함수처럼 Throwable을 받아오는 오버로딩도 제공합니다.

### retry() 함수
* 예외 처리의 다른 방법은 재시도입니다. 
* 예를 들어 서버와 통신할 때 인터넷이 일시적으로 안되거나 서버에 일시적인 장애가 발생하면 클라이언트에서는 일정 시간 후에 다시 통신을 요청하는 것이 필요합니다.
* 이때 1개의 API가 아닌 API 다수를 연속해서 호출해야 하는 경우 재시도하는 시나리오가 매우 복잡해질 수 있기도 합니다.

* RxJava는 이러한 것을 단순하게 처리할 수 있는 retry() 라는 함수를 제공합니다.
* onError 이벤트 발생 시 해당 처리를 재시도합니다.

* retry() 함수는 Observable에서 onError 이벤트가 발생하면 바로 다시 subscribe() 함수를 호출하여 재구독하게 되어 있습니다.
* retry() 함수는 다양한 오버로딩이 제공되며 인자는ㄴ 재시도 횟수를 지정하거나 어떤 조건에서 재시도할 것인지를 판단합니다.

~~~java
// 시간 표시용.
CommonUtils.exampleStart();

String url = "https://api.github.com/zen";
Observable<String> source = Observable.just(url)
    .map(OkHttpHelper::getT)
    .retry(5)
    .onErrorReturn(e -> CommonUtils.ERROR_CODE);

source.subscribe(data -> Log.it("result : " + data));
~~~
* 실행 시간을 표시하기 위해 CommonUtils.exampleStart()를 호출한 후 url에 정해진 URL을 저장합니다.
* 그리고 map(urlOkHttpHelper::getT)을 호출한 후 retry() 함수의 실행횟수는 5회로 지정합니다.
* 마지막으로 에러 발생시 ERROR_CODE를 리턴합니다.
* 참고로 OkHttpHelper.getT()는 기존의 OkHttpHelper.get()와 동일하지만 시간 표시를 위한 로그만 변경한 것입니다.

~~~java
final int RETRY_MAX = 5;
final int RETRY_DELAY = 1000;

CommonUtils.exampleStart();

String url = "https://api.github.com/zen";
Observable<String> source = Observable.just(url)
    .map(OkHttpHelper::getT)
    .retry((retryCnt, e) -> {
        Log.e("retryCnt = " + retryCnt);
        CommonUtils.sleep(RETRY_DELAY);

        return retryCnt < RETRY_MAX ? true: false;
    })
    .onErrorReturn(e -> CommonUtils.ERROR_CODE);

source.subscribe(data -> Log.it("result : " + data));
~~~

* retry() 함수는 람다 표현식의 인자로 retryCnt와 Throwable 객체를 전달받습니다.
* 재시도할 때는 CommonUtils.sleep() 함수를 호출하여 1000ms 동안 대기합니다.
* 마지막으로 재시도 횟수를 제한하기 위해서 재시도 횟수가 5회 이내일 때는 람다 표현식이 true를 리턴하고 5회 이후에는 false를 리턴합니다.

### retryUntil() 함수
* retryUntil() 는 특정 조건이 충족될 때까지만 재시도 하는 함수입니다.
* retry() 함수는 재시도를 지속할 조건이 없을 때 재시도를 중단한다면, retryUntil() 함수는 재시도를 중단할 조건이 발생할 때까지 계속 재시도합니다.

~~~java
// 시간 표시용.
CommonUtils.exampleStart();

String url = "https://api.github.com/zen";
Observable<String> source = Observable.just(url)
    .map(OkHttpHelper::getT)
    .subscribeOn(Schedulers.io)
    .retryUntil(() -> {
        if(CommonUtils.isNetworkAvailable())
            return true;    // 중지
        
        CommonUtils.sleep(1000);
        return false;   // 계속 진행
    });

    source.subscribe(Log::i);

    // IO 스케줄러에서 실행되기 때문에 sleep() 함수가 필요함.
    CommonUtils.sleep(5000);
~~~

* 보통 재시도 로직은 별도의 스레드에서 동작하기 때문에 IO 스케줄러를 활용하면 됩니다. 따라서 subscribeOn() 함수에는 Schedulers.io()를 인자로 넣었습니다.
* retryUntil() 함수의 인자인 람다 표현식에서는 먼저 CommonUtils.isNetworkAvailable()를 호출하여 네트워크가 사용 가능한 상태인지 확인합니다.
* 만약 true를 리턴하면 재시도를 중단하도록 람다 표현식도 true를 리턴합니다.
* 네트워크를 사용할 수 없는 상태라면 1000ms를 쉬고 재시도합니다. 이때 람다 표현식은 false를 리턴합니다.

~~~java
public static boolean isNetworkAvailable() {
    try {
        return InetAddress.getByName("www.google.com").isReachable(1000)
    }
}
~~~
