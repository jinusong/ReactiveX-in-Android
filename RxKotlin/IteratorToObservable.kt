package com.jinwoo.myapplication

import io.reactivex.Observable
import io.reactivex.Observer

import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable


class IteratorToObservable {
    fun observable() {
        val observer = object: Observer<Any> {
            override fun onComplete() {
                println("완료됨")
            }

            override fun onSubscribe(d: Disposable) {
                println("구독")
            }

            override fun onNext(t: Any) {
                println("받음 -> $t")
            }

            override fun onError(e: Throwable) {
                println("에러 발생 => ${e.message}")
            }
        }

        val list:List<String> = listOf("Str 1", "Str 2", "Str 3", "Str 4")

        val observable: Observable<String> = list.toObservable()

        observable.subscribe(observer)
    }
}