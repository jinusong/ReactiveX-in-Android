# RxAndroidFlowable
* 이 문서에서는 고급 주제인 배압과 Flowable 클래스에 대해서 알아봅니다.

* Flowable는 RxJava 2.x에 새로 도입된 클래스입니다. 
* RxJava에는 Observable 클래스의 수많은 변형이 존재하는데 Flowable은 배압 이슈를 위해 별도 분리한 클래스입니다.

* Flowable 클래스를 도입한 이유는 Observable 클래스의 성능을 향상시키기 위해서 입니다.
* 기존의 Observable 클래스는 배압에 관한 처리가 불필요한 경우에는 초기 로딩 때문에 약간의 오버헤드가 있었습니다.
* 하지만 RxJava 2의 Observable 클래스에는 배압으로 인한 성능 오버헤드가 사라졌습니다.

* Flowable 클래스의 활용은 기본적으로 Observable과 동일합니다. 
* 또한 Flowable에서 Observable로 변환하는 것이나 Observable에서 Flowable로 변환하는 것도 어렵지 않습니다.
* 변환을 위해 toObservable()과 toFlowable() 함수를 제공합니다. 지금까지 배웠던 Observable과 동일합니다.

~~~java
Flowable.just("Hello world")
    .subscribe(new Consumer<String>() {
        @Override public void accept(String s) {
            System.out.println(s);
        }
    });
~~~

* Flowable 클래스를 Observable 클래스로 바꿔도 똑같이 동작합니다.
* subscribe() 함수 부분은 람다 표현식과 메서드 레퍼런스로 고치면 System.out::println으로 단순화됩니다.
* 비동기 프로그래밍으로 subscribeOn()과 observeOn() 함수도 동일하게 사용할 수 있습니다.

~~~java
Flowable.fromCallable(() _> {
    // 값비싼 연산을 흉내 냄.
    Thread.sleep(1000);
    return "Done";
})
    .subscribeOn(Schedulers.io())
    .observeOn(Schedulers.single())
    .subscribe(System.out::println, Throwable::printStackTrace);
// Flowable 실행을 완료할 때까지 기다림.
Thread.sleep(2000);
~~~

* 지금까지는 Observable과 모든 것이 동일합니다.
* 하지만 배압이 다릅니다.

## Observable과 Flowable의 선택 기준
* RxJava 2.x의 위키에는 Observable과 Flowable의 선택 기준이 공개되어 있습니다.

* Observable을 사용해야 할 때
    * 최대 1,000개 미만의 데이터 흐름. 예를 들어 응용 프로그램에서 OOME(Out of Memory Exception)이 발생할 확률이 거의 없는 경우입니다.
    * 마우스 이벤트나 터치 이벤트를 다루는 GUI 프로그래밍, 이 경우에는 배압의 이슈가 거의 발생하지 않습니다. Observable로는 초당 1,000회 이하의 이벤트를 다루는데 이때 sample()이나 debounce() 같은 흐름 제어 함수를 활용하면 됩니다.
    * 데이터 흐름이 본질적으로 동기 방식이지만 프로젝트에서 사용하는 플랫폼이 자바 Stream API나 그에 준하는 기능을 제공하지 않을 때, Observable은 보통 Flowable과 비교했을 때 성능 오버헤드가 낮습니다.

* Observable보다 Flowable을 선택해야할 때
    * 특정 방식으로 생성된 10,000개 이상의 데이터를 처리하는 경우. 이때 메서드 체인에서 데이터 소스에 데이터 개수 제한을 요청해야 합니다.
    * 디스크에서 파일을 읽어 들일 경우. 본질적으로 블로킹I/O 방식을 활용하고 내가 원하는 만큼 가져오도록 제어할 수 있습니다.
    * JDBC를 활용해 데이터베이스의 쿼리 결과를 가져오는 경우. 블로킹 방식을 이용하므로 ResultSet.next()를 호출하는 방식으로 쿼리의 결과를 읽어오도록 제어할 수 있습니다.
    * 다수의 블로킹 방식을 사용하거나 가져오는 방식의 데이터 소스가 미래에는 논 블로킹방식의 리액티브 API나 드라이버를 제공할 수도 있는 경우입니다.

* 디스크에서 파일 읽기, JDBC를 활용한 데이터베이스 쿼리하기, 네트워크 I/O 등은 차가운 Observable에 해당합니다.
* 보통 차가운 Observable은 결과 데이터를 처리할 수 있는 만큼 조금씩 가져오는 것이 아니라 한 번에 모두 가져옵니다.
* 따라서 이 경우에 반드시 Flowable을 활용해야 하는 것은 아닙니다.
* 업스트림에서 발행하는 데이터의 속도와 다운스트림에서 처리하는 솓오의 차이가 작다면 Observable을 활용해도 좋습니다.

* 즉 데이터 발행과 처리 속도가 차이 나더라도 먼저 sample(), throttle(), debounce() 같은 흐름 제어 함수를 활용하여 해결하는 것이 좋습니다.
* 이러한 함수로 해결하기 어려울 때 Flowable 클래스로 전환하면 됩니다.

## Flowable을 활용한 배압 이슈 대응
* Flowable에서 추가로 제공하는 배압 이슈에 대응하는 함수
    * onBackpressureBuffer(): 배압 이슈가 발생했을 때 별도의 버퍼에 저장합니다. Flowable 클래스는 기본적으로 128개의 버퍼가 있습니다.
    * onBackpressureDrop(): 배압 이슈가 발생했을 때 해당 데이터를 무시합니다.
    * onBackpressureLatest(): 처리할 수 없어서 쌓이는 데이터를 무시하면서 최신 데이터만 유지합니다.

~~~java
// 시간을 측정하기 위해 호출함.
CommonUtils.exampleStart();

PublishSubject<Integer> subject = PublishSubject.create();
subject.observeOn(Schedulers.computation())
    .subscribe(data -> {
        CommonUtils.sleep(100); // 100ms 후 데이터를 처리함.
        Log.it(data);
    }, err -> Log.e(err.toString()));

// 뜨거운 Observable로 50,000,000개의 데이터를 연속으로 발행함.
for(int i = 0; i < 50000000; ++i) {
    subject.onNext(i);
}
subject.onComplete();

~~~

* PublishSubject 객체를 생성한 후 처리 결과는 계산 스케줄러로 전달합니다.
* subscribe() 함수 호출 후 Subject 객체가 발행한 데이터는 100ms 후에 로그를 출력합니다.

* 한편 PublishSubject 객체는 뜨거운 Observable입니다.
* 데이터를 발행하는 속도와 데이터를 처리하는 속도의 차이가 발생했을 때 어떠한 보호 장치도 제공하지 않습니다.

* observeOn() 함수에서 지정한 것처럼 계산 스케줄러에서 결과 데이터를 출력합니다.
* 그런데 코드를 작성하면서 의도했던 100ms 간격보다 상당히 느리게 데이터를 처리합니다.
* 전체 실행 시간도 13초가량입니다.

* 데이터는 PublishSubject 객체에서 매우 빠르게 발행되는데 데이터는 경우 13개만 처리되었습니다.
* 만약 발행하는 데이터의 개수가 훨씬 많아지면 JVM은 곧 Out Of Memory 예외를 발생하고 실행을 중단할 것입니다.
* 이런 배압 이슈에 대응할 때 Flowable 클래스를 활용합니다.

### 첫 번째 대응 방법

* 배압 이슈의 첫 번째 대응 방법은 버퍼 만들기입니다. Flowable의 onBackPressureBuffer() 함수에는 오버로딩들이 있습니다.
    * 첫 번째는 기본 값 128개의 버퍼가 있습니다.
    * 두 번째는delayError 여부를 지정하여 true면 예외가 발생했을 때 버퍼에 쌓인 데이터를 모두 처리할 때까지 예외를 던지지 않고 false면 예외가 발생했을 때 바로 다운스트림에 예외를 던집니다.
    * 세 번째는 capacity 인자로 버퍼 개수를 지정할 수도 있고 onOverflow 인자에 버퍼가 넘쳤을 때 실행할 동작을 지정할 수도 있습니다.
    * 마지막은 버퍼가 가득 찼을 때 추가로 실행하는 전략을 지정할 수 있습니다. RxJava에서 지정할 수 있는 전략은 다음과 같습니다.
        * ERROR: MissingBackpressureException 예외를 던지고 데이터 흐름을 중단합니다.
        * DROP_LATTEST: 버퍼에 쌓여 있는 최근 값을 제거합니다.
        * DROP_OLDEST: 버퍼에 쌓여 있는 가장 오래된 값을 제거합니다.

~~~java
// 시간을 측정하기 위해 호출함.
CommonUtils.exampleStart();
Flowable.range(1, 50000000)
    .onBackpressureBuffer(128, ()-> {}, BackpressureOverflowStrategy.DROP_OLDEST)
    .observeOn(Schedulers.computation())
    .subscrbie(data -> {
        CommonUtils.sleep(100); // 100ms 후 데이터를 처리함.
        Log.id(data);
    }, err -> Log.e(err.toString()));
~~~

* Flowable.range() 함수를 활용하여 동일한 개수의 데이터를 발행합니다.
* 128개의 버퍼를 생성한 후 버퍼의 넘침이 발생하면 버퍼의 가장 오래된 데이터를 버리도록 전략을 설정합니다.

* 버퍼를 활용하여 데이터를 훨씬 빠르게 다운스트림으로 발행하는 것을 알 수 있습니다.
* 거의 10배의 속도입니다. 데이터의 발행 속도가 워낙 빠르기 때문에 128개의 버퍼로는 모두 대응하는 것은 무리입니다.

### 두 번째 대응 방법

* 배압 이슈에 대응하는 두 번째 방법은 onBackpressureDrop()라는 함수를 활용하는 것입니다.
* onBackpressureBuffer() 함수가 버퍼를 만들어 쌓아 두었다가 처리하는 방식이라면, onBackpressureDrop() 함수는 버퍼가 가득찼을 때 이후 데이터를 그냥 무시합니다.

* onBackpressureBuffer() 함수를 onBackpressureDrop() 함수로 교체한 것입니다.
* 함수만 교체해 원하는 동작을 선언할 수 있다는 것이 RxJava의 매력입니다.

~~~java
// 시간을 측정하기 위해 호출함.
CommonUtils.exampleStart();

Flowable.range(1, 50000000)
    .onBackpressureDrop()
    .observeOn(Schedulers.computation())
    .subscribe(data -> {
        CommonUtils.sleep(100); // 100ms 후 데이터를 처리함.
        Log.it(data);
    }, err -> Log.e(err.toString()));

CommonUtils.sleep(20000);
~~~

* 특이 사항은 마지막 20초간 sleep() 함수를 실행했다는 점입니다.
* onBackpressureDrop() 함수를 사용하면 버퍼에 128개의 데이터가 가득찼을 때 데이터를 계산 스케줄러에서 출력하기도 전에 끝납니다.
* 따라서 계산 스케줄러에서 데이터를 다운스트림으로 발행할 수 있도록 충분한 시간을 기다려주어야 합니다.
* UI 프로그래밍에서는 이와 같은 기다림이 필요하지 않습니다.

### 세 번째 대응 방법

* 배압 이슈에 대응하는 마지막 방법은 onBackpressureLatest() 함수를 활용하는 것입니다.
* onBackpressureBuffer()와 onBackpressureDrop() 함수의 기능을 섞은 것으로 마지막 값을 발행할 수 있도록 해줍니다.

~~~java
// 시간을 측정하기 위해 호출함.
CommonUtils.exampleStart();

Flowable.range(1, 50000000)
    .onBackpressureLatest()
    .observeOn(Schedulers.computation())
    .subscribe(data -> {
        CommonUtils.sleep(100); // 100ms 후 데이터를 처리함.
        Log.it(data);
    }, err -> Log.e(err.toString()));

CommonUtils.sleep(20000);
~~~

* onBackpressureDrop() 함수를 onBackpressureLatest() 함수로 바꾼 것만 제외하면 모두 동일합니다.