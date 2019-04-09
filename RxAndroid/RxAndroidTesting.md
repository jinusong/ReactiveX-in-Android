# RxAndroidTesting
## JUnit 5
* JUnit 5의 가장 큰 특징은 모듈화입니다.
* JUnit4까지는 단인 jar 파일로 구성되었지만 이제는 기능에 따라 세부 모듈로 구별합니다.
* 첫 번째 테스트 코드를 작성합니다.
~~~java
import org.junit.*;

@RunWith(JUnitPlatform.class)
public class JUnit5Basic {
    @DisplayName("JUnit 5 First Example")
    @Test
    void testFirst() {
        int expected = 3;
        int actual = 1 + 2;
        assertEquals(expected, actual);
    }
}
~~~

* 이제는 JUnit 5를 기반으로 RxJava의 테스트 코드를 작성합니다.

~~~java
@DisplayName("test getShape() Observable")
@Test
void testGetShapeObservable() {
    String[] data = {"1", "2-R", "3-T"};
    Observable<String> source = Observable.fromArray(data)
        .map(Shape::getShape);
    
    String[] expected = {Shape.BALL, Shape.RECTANGLE, Shape.TRIANGLE};
    List<String> actual = new ArrayList<>();
    source.doOnNext(Log::d)
        .subscribe(actual::add);
    
    assertEquals(Arrays.asList(expected), actual);
}
~~~

* source는 data의 각 값을 Observable에 넣은 후 CommonUtils.getShape()를 호출하여 매핑합니다.
* 데이터 예상 결과는 expected 인자에 저장했습니다. 
* source에서 발행하는 데이터 원본은 doOnNext() 함수에서 확인할 수 있으며 실제 값은 actual 변수에 저장합니다.

* 마지막으로 assertEquals() 메서드를 통해 내가 예상했던 데이터와 실제 데이터가 일치하는지 확인합니다.

## TestObserver 클래스
* RxJava에서 젱공하는 TestObserver 클래스를 활용한 방법을 알아보기 위해 testGetShapeObserver() 테스트 코드를 좀 더 쉽게 바꿔봅니다.

~~~java
@DisplayName("#1: using TestObserver for Shape.getShape()")
@Test
void testGetShapeUsingTestObserver() {
    String[] data = {"1", "2-R", "3-T"};
    Observable<String> source = Observable.fromArray(data)
        .map(Shape::getShape);
    
    String[] expected = {Shape.BALL, Shape.RECTANGLE, Shape.TRIANGLE};
    source.test().assertResult(expected);
}
~~~

* 앞서 작성한 JUnit 5 기반의 테스트 코드와 다른 점은 test()와 assertResult() 함수입니다.
* test() 함수는 TestObserver 객체를 리턴합니다.
* 다음은 RxJava 2.x에 새로 추가된 test() 함수릐 소스코드입니다.

~~~java
public final TestObserver<T> test() {
    TestObserver<T> ts = new TestObserver<T>();
    subscribe(ts);
    return ts;
}
~~~

* TestObserver클래스의 주요 함수는 다음과 같습니다.
    * assertResult(): 예상된(expected) 결과와 실제(actual) 결과를 비교하는 메서드입니다. Jnit의 assertEquals() 메서드와 같습니다.
    * assertFailure(): Observable에서 기대했던 에러(onError 이벤트)가 발생하는지 확인하는 코드입니다. 만약 기대했던 에러가 발생하지 않으면 테스트 코드 실행은 실패합니다.
    * assertFailureAndMessage(): 기대했던 에러 발생 시 에러 메시지까지 확인할 수 있습니다.
    * awaitDone(): interval() 함수처럼 비동기로 동작하는 Observable 코드를 테스트할 수 있습니다.
    * assertComplete(): Observable을 정상적으로 완료(onComplete 이벤트)했는지 확인합니다.

* 다음은 assertFailure() 함수를 활용하는 예제입니다. 
* 총 3개의 값을 넣어서 앞 두 번째 값까지는 정상적으로 발행하고 마지막 값에서 기대했던 예외가 발생하는지 확인합니다.

~~~java
@DisplayName("assertFailure() example")
@Test
void assertFailureExample() {
    String[] data = {"100", "200", "%300"};
    Observable<Integer> source = Observable.fromArray(data)
        .map(Integer::parseInt);

    source.test().assertFailure(NumberFormatException.class, 100, 200);
}
~~~

* Integer 클래스의 parseInt() 메서드는 정수 이외의 값을 잘못 입력해서 정수로 변환할 수 없을 때 NumberFormatException이 발생합니다.
* 따라서 Observable에 비정상 데이터가 들어왔을 때 앞 예외가 발생하는지 알아봅니다.
* 첫 번째 데이터와 두 번째 데이터는 정상적으로 처리하고 세 번째 데이터는 NumberFormatException이 발생하고 onError 이벤트로 종료되었습니다.

* assertFailureAndMessage() 함수는 assertFailure() 함수와 동일하지만 에러 메시지를 확인하기 위해 message인자가 추가된 형태입니다.
* 사용자 정의 예외를 활용하는 경우 사전에 유용하게 테스트해볼 수 잇습니다.

~~~java
@DisplayName("assertFailureAndMessage() example")
@Test
void assertFailureAndMessage() {
    String[] data = {"100", "200", "%300"};
    Observable<Integer> source = Observable.fromArray(data)
        .map(Integer::parseInt);
    
    source.test().assertFailureAndMessage(NumberFormatException.class, "For input string: \"%300\"",  100, 200);
}
~~~

* 세 번째 값인 "%300"에 대한 에러 메시지까지 맞는지 확인합니다.
* 만약 에러 메시지가 다른 경우 테스트 코드를 실행하면 에러를 확인할 수 있습니다.

* 마지막은 assertComplete() 함수 입니다.
* 테스트하는 Observable이 정상적으로 완료되었는지 확인합니다.

~~~java
@DisplayName("assertComplete() example")
@Test
void assertComplete() {
    Observable<String> source = Observable.create(
        (ObservableEmitter<String> emitter) -> {
            emitter.onNext("Hello RxJava");
            emitter.onComplete();
        }
    );
    source.test().assertComplete();
}
~~~

* source의 Observable은 onComplete() 함수를 명시적으로 호출했습니다.
* TestObserver에서 source Observable이 onComplete 알림을 보냈는지 확인한다는 뜻입니다.
* 참고로 assertComplete()의 반대 함수로 assertNotComplete() 함수도 제공합니다.

## 비동기 코드 테스트
* RxJava는 스케줄러를 활용해 다양한 상황에서 비동기 코드를 직관적으로 작성할 수 있습니다.
* 하지만 비동기 코드를 테스트하는 것은 어려우므로 RxJava는 비동기로 동작하는 코드를 테스트할 방법을 제공합니다.
* 먼저 Observable의 interval() 함수를 활용하는 코드를 살펴봅니다.

~~~java
@DisplayName("test Observable.interval() wrong")
@Test

// 테스트 코드를 비활성화시키는 경우네는 @Disable을 추가합니다.
@Disabled
void testIntervalWrongWay() {
    Observable<Integer> source = Observable.interval(100L, TimeUnit.MILLISECONDS)
        .take(5)
        .map(Long::intValue);

    source.doOnNext(Log::d)
        .test().assertResult(0, 1, 2, 3, 4);
}
~~~

* 이렇게 하면 테스트가 실패합니다.
* 실제로 아무것도 실행되지 않습니다.
* 또한 doOnNext(Log::d)를 통해서 값을 출력하도록 했지만 아무것도 출력되지 않습니다.

* 그 이유는 Observable.interval() 메서드가 main 스레드가 아닌 계산 스케줄러에서 실행되기 때문입니다.

* 과거 RxJava 1.x에서는 blocking() 함수를 활용하여 비동기로 동작하는 코드를 동기 방식으로 변경하여 테스트했습니다.
* 하지만 RxJava 2.x는 TestObserver클래스의 awaitDone() 함수를 활용하여 비동기 코드를 테스트할 방법을 제공합니다.

~~~java
Observable<Integer> source = Observable.interval(100L, TimeUnit.MILLISECONDS)
    .take(5)
    .map(Long::intValue);

source.doOnNext(Log::d)
    .test()
    .awaitDone(1L, TimeUnit.SECONDS)
    .assertResult(0, 1, 2, 3, 4);
~~~

* awaitDone() 함수가 편리한 이유는 test() 함수가 실행되는 스레드에서 onComplete() 함수를 호출할 때까지 기다려주기 때문입니다.
* Observable이 실행되는 스케줄러가 무엇인지 고민하지 않아도 됩니다.