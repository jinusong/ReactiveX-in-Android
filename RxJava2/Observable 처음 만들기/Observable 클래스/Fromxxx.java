import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class Fromxxx {
	public void FromArray_emit() {
		Integer[] arr = {100, 200, 300};
		Observable<Integer> source = Observable.fromArray(arr);
		source.subscribe(System.out::println);
	}
	// 배열의 데이터를 차례로 발행합니다.
	
	public void FromIterable_emit() {
		List<String> names = new ArrayList<>();
		names.add("Jerry");
		names.add("William");
		names.add("Bob");
		
		Observable<String> source = Observable.fromIterable(names);
		source.subscribe(System.out::println);
	}
	// 리스트나 셋, 객체의 데이터는 Iterable로 처리합니다. (map은 안된다)
	
	public void fromCallable_emit() {
		Callable<String> callable = () -> {
			Thread.sleep(1000);
			return "Hello Callable";
		};
		
		Observable<String> source = Observable.fromCallable(callable);
		source.subscribe(System.out::println);
	}
	// 비동기 실행 결과를 반환하는 call의 경우 처리 
	
	public void fromFuture() {
		Future<String> future = Executors.newSingleThreadExecutor().submit(() -> {
			Thread.sleep(1000);
			return "Hello Future";
		});
	}
	// Future 인터페이스도 Callable과 마찬가지로 비동기로 작업을 처리하며, 주로 비동기 계산의 결과를 구할 때 사용합니다.
	
	public void fromPubilsher_emit() {
		Publisher<String> publisher = (Subscriber<? super String> s) -> {
			s.onNext("Hello Observable.fromPublisher()");
			s.onComplete();
		};
		
		Observable<String> source = Observable.fromPublisher(publisher);
		source.subscribe(System.out::println);
	}
	// Publisher 객체는 Observable.create()와 마찬가지로 onNext, onComplete()함수를 호출할 수 있습니다.
	
	public static void main(String[] args){
		Fromxxx demo = new Fromxxx();
		demo.FromArray_emit();
		demo.FromIterable_emit();
		demo.fromCallable_emit();
		demo.fromFuture();
		demo.fromPubilsher_emit();
	}
}
