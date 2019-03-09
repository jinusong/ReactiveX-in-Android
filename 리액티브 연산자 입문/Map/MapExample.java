import java.util.function.Function;

import io.reactivex.Observable;

public class MapExample {

	public void marbleDiagram() {
		String[] balls = { "1", "2", "3", "5" };
		Observable<String> source = Observable.fromArray(balls)
				.map(ball -> ball + "♢");
		source.subscribe(Log::i);
	}
	
	public void mapFunction() {
		Function<String, String> getDiamond = ball -> ball + "♢";
		String[] balls = {"1", "2", "3", "5"};
		Observable<String> source = Observable.fromArray(balls)
				.map(getDiamond);
		source.subscribe(Log::i);
	}
	
	public void mappingType() {
		Function<String, Integer> ballToIndex = ball -> {
			switch(ball) {
			case "RED": return 1;
			case "YELLOW": return 2;
			case "GREEN": return 3;
			case "BLUE": return 5;
			default: return -1;
			}
		};
		
		String[] balls = {"RED", "YELLOW", "GREEN", "BLUE"};
		Observable<Integer> source = Observable.fromArray(balls)
				.map(ballToIndex);	// 명시적인 타입변환 없이 바로 사용할 수 있음
		source.subscribe(System.out::println);
		
	}
}
