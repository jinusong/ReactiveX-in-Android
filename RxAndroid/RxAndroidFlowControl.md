# RxAndroidFlowControl
## 흐름 제어
* 흐름 제어는 Observable이 데이터를 발행하는 속도와 옵서버가 데이터를 받아서 처리하는 속도 사이의 차이가 발생할 때 사용하는 함수입니다.
* RxJava는 push 방식으로 동작하므로 이러한 문제가 발생할 때 대처할 수 있어야 합니다.
* RxJava는 다양한 흐름 제어 함수를 제공합니다. 크게 sample(), buffer(), throttle(), window(), debounce() 함수 등을 제공합니다.

### sample() 함수
* sample() 함수는 특정한 시간 동안 가장 최근에 발행된 데이터만 걸러줍니다.
* 해당 시간에는 아무리 많은 데이터가 들어와도 해당구간의 마지막 데이터만 발행하고 나머지는 무시합니다.

* sample() 함수의 emitLast 인자는 sample() 함수의 데이터 발행이 완료되지 않고 마지막에 데이터가 남아 있을 때 해당 데이터를 발행할 것인지 결정합니다.
~~~java
String[] data = {"1", "7", "2", "3", "6"};

// 시간 측정용.
CommonUtils.exampleStart();

// 앞의 4개는 100ms 간격으로 발행
Observable<String> earlySource = Observable.fromArray(data)
    .take(4)
    .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a, b) -> a);

// 마지막 데이터는 300ms 후에 발행.
Observalble<String> lateSource = Observable.just(data[4])
    .zipWith(Observable.timer(300L, TimeUnitMILLISEECONDS), (a, b) -> a);

// 2개의 Observable을 결합하고 300ms로 샘플링.
Observable<String> source = Observable.concat(earlySource, lateSource)
    .sample(300L, TimeUnit.MILLISECONDS);

source.subscribe(Log::it);
CommonUtils.sleep(1000);
~~~

* 먼저 100ms 간격으로 data 배열에 있는 데이터 4개를 발행합니다.
* 마블 다이어그램처럼 '1'원부터 '3'원에 해당합니다.
* 그리고 마지막 데이터인 '6'원의 경우 300ms 후에 발행합니다.
* 또한 내가 원하는 특정 시간 후에 발행하기 위해 concat() 함수를 호출하여 2개의 데이터 흐름을 결합했습니다.
* 이렇게 전체 데이터 흐름을 세부 데이터 흐름으로 나누면 코드의 가독성이 좋아집니다.

* 샘플링 300ms 간격으로 수행합니다. 매 300ms 간격으로 가장 최근에 들어온 값만 최종적으로 발행합니다.
* 만약 sample() 함수의 실행이 끝나지 않았는데 Observable이 종료되는 경우에 마지막 값을 발행하려면 emiLast 인자를 true로 넣어주면 됩니다.

~~~java
// 2개의 Observable을 결합하고 300ms으로 샘플링(emitLast = true).
Observable<String> source = Observable.concat(earlySource, lateSource)
    .sample(300L, TimeUnit.MILLISECONDS, true);
~~~

### buffer() 함수
* buffer() 함수는 smaple() 함수와는 조금 다릅니다.
* sample() 함수는 특정 시간 가격을 기준으로 가장 최근에 발행된 데이터만 넘겨주고 나머지는 무시하는 반면 buffer() 함수는 일정 시간 동안 데이터를 모아두었다가 한꺼번에 발행해줍니다.
* 따라서 넘치는 데이터 흐름을 제어할 필요가 있을 때 활용할 수 있습니다.

* 기본적으로 스케줄러 없이 현재 스래드에서 동작합니다.
* 입력되는 값을 count에 저장된 수만큼 모아서 List<T>에 한꺼번에 발행합니다.

* RxJava의 buffer() 함수는 정말 다양한 오버로딩을 제공합니다.
* 가장 간단한 것부터 몇 가지를 설펴보갰습니다.
* 첫 번째는 마블 다이어그램을 코드로 count 인자에 데이터를 모을 개수를 입력합니다.

~~~java
String[] data = {"1", "2", "3", "4", "5", "6"};
CommonUtils.exampleStart();

// 앞의 3개는 100ms 간격으로 발행.
Observable<String> earlySource = Observable.fromArray(data)
    .take(3)
    .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a, b) -> a);

// 가운데 1개는 300ms 후에 발행
Observable<String> middleSource = Observable.just(data[3])
    .zipWith(Observable.timer(300L, TimeUnit.MILLISECONDS) , (a, b) -> a);

// 마지막 2개는 100ms 후에 발행.
Observable<String> lateSource = Observable.just(data[4], data[5])
    .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a, b) -> a);

// 3개씩 모아서 한꺼번에 발행함/
Observable<List<String>> source = Observable.concat(earlySource, middleSource, lateSource)
    .buffer(3);

source.subscribe(Log::it);
CommonUtils.sleep(1000);
~~~

* 마블 다이어그램처럼 데이터를 발행하기 위해서 interval(), timer() 함수와 concat() 함수를 호출했습니다.
* buffer(3)는 데이터를 3개씩 모았다가 List<String>에 채운 후 값을 한꺼번에 발행해줍니다.

* 두 번째 buffer() 함수에는 모으거나 무시할 데이터 개수를 입력합니다.
* skip 변수는 count보다 값이 커야 합니다.
* 만약 count가 2이고 skip이 3이면 2개의 데이터를 모으고 1개는 스킵합니다.

~~~java
// 2는 모으고 1개는 건너뜀.
Observable<List<String>> source = 
    Observable.concat(earlySource, middleSource, lateSource)
        .buffer(2, 3);
~~~

* 바뀐 점은 count 값을 2로 줄이고 skip을 3으로 설정한 것입니다.
* 2개의 데이터가 발행되며 바로 List<String>에 채워 발행하고 데이터 1개는 건너 뜁니다.

* Observable에서 onNext 이벤트가 발생하면 내부 데이터를 3개가 아니라 2개 값을 모아 바로 List<String>에 채운 후 구독자에게 발행합니다.
* buffer() 함수에는 시간 간격으로 데이터를 모으는 오버로딩도 제공합니다.

### throttleFirst()와 throttleLast() 함수
* throttle은 영어로 '조절판'이라는 뜻입니다.
* 그것에 맞게 throttleFirst()는 주어진 조건에서 가장 먼저 입력된 값을 발행합니다.
* throttleLast()는 주어진 조건에서 가장 마지막에 입력된 값을 발행합니다.

* throttleFirst() 함수는 sample() 함수와 비슷하지만 다릅니다.
* sample() 함수가 주어진 시간 동안 입력된 마지막 값을 발행한다면, throttleFirst() 함수는 어떤 데이터를 발행하면 지정된 시간 동안 다른 데이터를 발행하지 않도록 막습니다.

* throttleFirst() 함수는 게산 스케줄러에서 실행합니다.
* 즉, 비동기로 동작하도록 설계된 함수입니다.
* windowDuration 인자는 시간 간격을 지정하며, unit은 시간의 단위입니다.

~~~java
String[] data = {"1", "2", "3", "4", "5", "6"};
CommonUtils.exampleStart();

// 앞의 1개는 100ms 간격으로 발행.
Observable<String> earlySource = Observable.just(data[0])
    .zipWith(Observable.timer(100L, TimeUnit.MILLiSECONDS), (a, b) -> a);

// 다음 1개는 300ms 후에 발행.
Observable<String> middleSource = Observable.just(data[1])
    .zipWith(Observable.timer(300L, TimeUnit.MILLISECONDS), (a, b) -> a);

// 마지막 4개는 100ms 후에 발행.
Observable<String> lateSource = Observable.just(data[2], data[3], data[4], data[5])
    .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a, b) -> a)
    .doOnNext(Log::dt); // 디버깅 정보 출력

// 200ms 간격으로 throttleFirst() 실행함.
Observable<String> source = 
    Observable.concat(earlySource, middle, lateSource)
        .throttleFirst(200L, TimeUnit.MILLISECONDS);
    
source.subscribe(Log::it);
CommonUtils.sleep(1000);
~~~

* 처음 100ms가 지난 후에 '1'을 발행한 후 300ms 동안 기다린 다음 '2'를 발행합니다.
* 그리고 100ms 간격으로 나머지 값들을 발행합니다. 
* 마지막으로는 throttleFirst() 함수를 호출하여 200ms 간격으로 타임 윈도에 맨 먼저 입력된 값을 발행합니다.

* 결과는 '1', '2', '4', '6'원이 다운스트림으로 발행되었습니다.
* throttleLast() 함수는 sample() 함수와 기본 개념은 동일합니다.
* throttleLast() 함수가 주어진 시간 동안 입력된 값 중 마지막 값을 발행하는 기본 개념에 충실하다면 sample() 함수는 다양한 오버로딩을 제공합니다.

### window() 함수
* windwo() 함수는 groupBy() 함수와 개념적으로 비슷합니다.
* groupBy() 함수는 특정 조건에 맞는 입력값들을 그룹화해 별도의 Observable을 병렬로 만듭니다.
* 반면 window() 함수는 throttleFirst()나 sample() 함수처럼 내가 처리할 수 있는 일부의 값들만 받아들일 수 있습니다.
* 흐름 제어 기능에 groupBy() 함수와 비슷한 별도의 Observable 분리 기능을 모두 갖추었다고 생각하면 됩니다.

* window() 함수는 count를 인자로 받습니다.
* count에 '3'이라는 값이 오면 앞으로 데이터 3개가 발행될 때마다 새로운 Observable을 생성하겠다는 뜻입니다.

* window() 함수는 현재 스레드를 그대로 활용합니다.
* window() 함수의 원형 window(timespan, timeskip, unit)은 어떤 필터링 작업을 해줘야 하기 때문에 계산 스케줄러를 활용하게 됩니다. buffer() 함수가 현재 스레드에서 실행되는 이유와 동일합니다.

~~~java
String[] data = {"1", "2", "3", "4", "5", "6"};
CommonUtils.exampleStart();

// 앞의 100ms 간격으로 발행.
Observable<String> earlySource = Observer.fromArray(data)
    .take(3)
    .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a, b) -> a);

// 가운데 1개는 300ms 후에 발행.
Observable<String> middleSource = Observable.just(data[3])
    .zipWith(Observable.timer(300L, TimeUnit.MILLISECONDS), (a, b) -> a);

// 마지막 2개는 100ms 후에 발행.
Observable<String> lateSource = Observable.just(data[4], data[5])
    .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a, b) -> a);

// 데이터 3개씩 모아서 새로운 Observable을 생성함.
Observable<Observable<String>> source = Observable.concat(earlySource, middleSource, lateSource)
    .window(3);

source.subscribe(observable -> {
    Log.dt("New Observable Started!!");
    observable.subscribe(Log::it);
});

CommonUtils.sleep(1000);
CommonUtils.exampleComplete();
~~~

* window() 함수에 count 인자로 3을 넣었습니다.
* 처음에 Observable을 생성하고 3개의 데이터를 전달받으면 새로운 Observable을 다시 생성하여 값을 발행합니다.
* windwo() 함수의 리턴 타입이 Observable<Observable<T>> 이므로 subscribe() 함수의 람다 표현식이 조금 달라졌습니다.
* 처음에는 Observable의 Observable이라는 개념이 이해하기는 어려울 수 있습니다.
* 그때는 람다 표현식의 인자로 Observable이 들어온다고 생각하면 이해하기 쉽습니다.

* 새로운 Observable이 생성될 때마다 "New Observable Started!!"라는 문자열을 출력했습니다.
* 그 다음 각 Observable에서 발행되는 값을 그대로 출력합니다.
* 즉, '1'값을 발행할 때와 '4'값을 발행할 때 각각 새로운 Observable이 생성되었습니다.

### debounce() 함수
* deboundce() 함수는 빠르게 연속 이벤트를 처리하는 흐름 제어 함수입니다. 
* POJO Java와 같이 콘솔에서는 크게 활용할 일이 없으나 안드로이드와 같은 UI 기반의 프로그래밍에서는 유용하게 활용할 수 있습니다.
* 예를 들어 버튼을 빠르게 누른 상황에서 마지막에 누른 이벤트만 처리해야 할 때 간단하게 적용할 수 있습니다.
* RxJava를 이용하지 않는다면 마지막에 버튼을 누른 시간을 멤버 변수에 저장하고 일정 시간 동안 if문으로 예외 처리해야 하기 때문에 매우 번거롭고 실수할 가능성도 큽니다.

* debounce() g함수는 계산스케줄러에서 동작합니다.
* 어떤 이벤트가 입력되고 timeout에서 지정한 시간 동안 추가 이벤트가 발생하지 않으면 마지막 이벤트를 최종적으로 발행합니다.

~~~java
String[] data = {"1", "2", "3", "5"};

Observable<String> source = Observable.concat(
    Observable.timer(100L, TimeUnit.MILLISECONDS).map(i -> data[0]),
    Observable.timer(300L, TimeUnit.MILLISECONDS).map(i -> data[1]),
    Observable.timer(100L, TimeUnit.MILLISECONDS).map(i -> data[2]),
    Observable.timer(300L, TimeUnit.MILLISECONDS).map(i -> data[3])
        .debounce(200L, TimeUnit.MILLISECONDS);

    source.subscribe(Log::i);
    CommonUtils.sleep(1000);
)
~~~
* 각각의 시간 간격이 서로 다르므로 concat() 함수를 활용하여 각각 데이터를 발행했습니다.
* time() 함수는 이벤트를 한 번만 발생시키고 완료하기 때문에 concat()와 timer() 함수의 조합은 유용합니다.
* debounce() 함수를 활용하여 200ms안에 더 이상의 이벤트가 발생하지 않으면 마지막 입력된 값을 발행합니다.