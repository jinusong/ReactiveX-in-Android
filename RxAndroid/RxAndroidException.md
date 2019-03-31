# RxAndroidException
## 디버깅
* 보통 코딩하는 도중에 로그를 넣는 이유는 잘못되었을 때 대처하기 위함입니다.
* 하지만 RxJava 코드는 로그를 넣을 수 있는 공간이 없습니다.
* Observable로 시작하는 업스트림과 그것을 받아서 처리하는 다운스트림이 동일한 문장으로 이루어져 있기 때문입니다.
* 전체 동작을 선언적으로 만들 수 있으므로 전체 맥락에 대한 가독성은 높아지지만 예외 코드를 어떻게 넣어야 하는지에 대한 어려움이 있습니다.

### doOnNext(), doOnComplete(), doOnError() 함수
* doOnNext(), doOnComplete(), doOnError() 라는 세 가지 함수는 Observable의 알림 이벤트에 해당합니다.
* Observable이 어떤 데이터를 발행할 때는 onNext, 중간에 에러가 발생하면 onError, 모든 데이터를 발행하면 onComplete 이벤트가 발생합니다.

## 예외처리
