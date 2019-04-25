import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.Observable

class ObservableCreate {
    fun observable() {
        val observer = object: Observer<Any> {
            override fun onComplete() {
                println("모두 완료됨")
            }

            override fun onSubscribe(d: Disposable) {
                println("새로운 구독")
            }

            override fun onNext(t: Any) {
                println("다음 $t")
            }

            override fun onError(e: Throwable) {
                println("에러 발생 => ${e.message}")
            }
        }

        val observable: Observable<String> = io.reactivex.Observable.create {
            it.onNext("방출됨 1")
            it.onNext("방출됨 2")
            it.onNext("방출됨 3")
            it.onNext("방출됨 4")
            it.onComplete()
       }

        observable.subscribe(observer)

        val observable2: Observable<String> = Observable.create<String> {
            it.onNext("방출됨 1")
            it.onNext("방출됨 2")
            it.onError(Exception("My Exception"))
        }

        observable2.subscribe(observer)
    }
}