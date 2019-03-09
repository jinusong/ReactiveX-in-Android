import java.util.function.Function;

import io.reactivex.Observable;

public class FlatMapExample {
	public void marbleDiagram() {
		Function<String, Observable<String>> getDoubleDiamonds = 
				ball -> Observable.just(ball + "♢", ball + "♢");
				
		String[] balls = {"1", "3", "5"};
		
		Observable<String> source = Observable.fromArray(balls)
				.flatMap(getDoubleDiamonds);
		source.subscribe(Log::i);
	}
	
	public void flatMapLambda() {
		String[] balls = {"1", "3", "5"};
		Observable<String> source = Observable.fromArray(balls)
				.flatMap(ball -> Observable.just(ball + "♢", ball + "♢"));
	}
}
