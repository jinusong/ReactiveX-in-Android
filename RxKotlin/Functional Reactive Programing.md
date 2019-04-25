# Functional Reactive Programing
* OOP와 FP는 굉장히 오래된 패러다임이고 각각 장단점을 보유하고 있습니다.
* 이 번거로움을 해결할 수 있는 방법은 OOP와 FP를 결합하는 것입니다.
* 작은 곳에서는 함수적이고, 큰 곳에서는 객체지향적인 것이 가장 일반적입니다.

## 함수형 리액티브 프로그래밍
* 함수형 리액티브 프로그래밍의 개념은 FP와 리액티브 프로그래밍을 결합해 나타났씁니다.
* 함수형 리액티브 프로그래밍의 정의는 FP의 빌딩블록(map, reduce, filter)를 사용하는 리액티브 프로그래밍을 위한 프로그래밍 패러다임을 이야기 합니다.

### 리액티브 매니페스토
* 리액티브 매니페스토는 다음과 같은 네가지 리액티브 원리를 정의하는 문서입니다.

#### 반응
* 시스템은 적시에 응답해야 합니다.
* 반응 시스템은 신속하고 일관된 응답 시간을 제공하는 데 주력하므로 일관된 서비스 품질을 제공합니다.

#### 복원
* 시스템이 장애와 마주쳐도 응답을 유지합니다.
* 복원은 복구, 격리, 위임을 통해 얻을 수 있습니다.
* 실패는 각 컴포넌트 내에 포함돼 각자로부터 컴포넌트를 분리하므로 컴포넌트에서 실패가 발생할 때 다른 컴포넌트나 전체 시스템에 영향을 미치지 않습니다.

#### 탄력
* 리액티브 시스템은 변화에 반응하고 다양한 작업 부하에서 반응성을 유지합니다.
* 리액티브 시스템은 상용 하드웨어와 소프트웨어 플랫폼에서 비용 효율적인 방식으로 탄력성을 발휘합니다.

#### 메시지 중심
* 탄력성 원칙을 수립하기 위해서는 리액티브 시스템이 비동기 메시지 전달에 의존해 컴포넌트 간의 경계를 설정해야 합니다.

## RxKotlin 맛보기
* Iterator를 사용한 코드를 RxKotlin의 Observable로 바꿉니다.

### 원래코드
~~~kotlin
fun main(args: Array<String>) {
    var list: List<Any> = listOf(1, "둘", 3, "넷", "다섯", 5.5f)
    var iterator = list.iterator()
    while(iterator.hasNext()) {
        println(iterator.next())
    }
}
~~~

### RxKotlin
* Observable클래스는 Iterator와 반대입니다.
* Observable은 Iterator 패턴과 같이 소비자가 생산자로부터 값을 가져오지 않고 생산자가 소비자에게 값을 알림으로 푸시합니다.

~~~kotlin
fun main(args: Array<String>) {
    var list = listOf(1, "둘", 3, "넷", "다섯", 5.5f)
    var observable = list.toObservable()

    observable.subscribeBy(
        onNext = { println(it) },
        onError = { it.printStackTrace() },
        onComplete = { println("완료!")}
    )
}
~~~