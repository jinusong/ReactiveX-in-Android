import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class ZipExample {
	public void marbleDiagram() {
		String[] shapes = {"BALL", "PENTAGON", "STAR"};
		String[] coloredTriangles = {"2-T", "6-T", "4-T"};
		
		Observable<String> source = Observable.zip(
				Observable.fromArray(shapes).map(Shape::getSuffix),	// 모양을 가져옵니다.
				Observable.fromArray(coloredTriangles).map(Shape::getColor),	// 색상을 가져옵니다.
				(suffix, color) -> color + suffix);
		
		source.subscribe(Log::i);
	}
	
	public void zipNumbers() {
		Observable<Integer> source = Observable.zip(
				Observable.just(100, 200, 300),
				Observable.just(10, 20, 30),
				Observable.just(1, 2, 3),
				(a, b, c) -> a + b + c);
		source.subscribe(Log::i);
	}
	
	public void zipInterval() {
		Observable<String> source = Observable.zip(
				Observable.just("RED", "GREEN", "BLUE"), 
				Observable.interval(200L, TimeUnit.MILLISECONDS),
				(value,i) -> value);
		
		CommonUtils.exampleStart();
		source.subscribe(Log::it);
		CommonUtils.sleep(1000);
	}
}
