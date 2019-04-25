import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable

class Observable {
    fun observable() {
        val observer = object: Observer<Any> {
            override fun onComplete() {
                println("모두 완료됨")
            }

            override fun onSubscribe(d: Disposable) {
                println("$d 구독됨")
            }

            override fun onNext(t: Any) {
                println("다음 $t")
            }

            override fun onError(e: Throwable) {
                println("에러 발생 $e")
            }
        }

        val observable = listOf(1, "둘", 3, "넷", "다섯", 5.5f).toObservable()

        observable.subscribe(observer)

        val observableOnList = Observable.just(listOf(1, "둘", 3, "넷", "다섯", 6.0f),
            listOf("아이템 1개인 리스트"),
            listOf(1,2,3))

        observableOnList.subscribe(observer)
    }
}