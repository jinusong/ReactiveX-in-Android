import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class Create {
	public void emit() {
		Observable<Integer> source = Observable.create(
				(ObservableEmitter<Integer> emitter) -> {
					emitter.onNext(100);
					emitter.onNext(200);
					emitter.onNext(300);
					emitter.onComplete();
				});
		source.subscribe(System.out::println);
	}
	
	public static void main(String[] args){
		Create demo = new Create();
		demo.emit();
	}
}
// create()함수는 just와 달리 onNext와 onComplete를 만들어야 합니다. (사용자의 의존)