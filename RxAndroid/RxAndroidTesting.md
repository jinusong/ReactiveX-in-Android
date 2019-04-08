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

