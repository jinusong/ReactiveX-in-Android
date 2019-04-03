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
* 마블 다이어그램을 코드로 count 인자에 데이터를 모을 개수를 입력합니다.

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