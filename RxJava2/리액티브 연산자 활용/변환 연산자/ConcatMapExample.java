import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class ConcatMapExample {
	public void marbleDiagram() {
		CommonUtils.exampleStart();
		
		String[] balls = {"1", "3", "5"};
		Observable<String> source = Observable.interval(100L, TimeUnit.MILLISECONDS)
				.map(Long::intValue)
				.map(idx -> balls[idx])
				.take(balls.length)
				.concatMap(ball -> Observable.interval(200L, TimeUnit.MILLISECONDS)
				.map(notUsed -> ball + "nemo")
				.take(2));
		
		source.subscribe(Log::it);
		CommonUtils.sleep(2000);
	}
	
	public static void main(String[] args) {
		ConcatMapExample example  =  new ConcatMapExample();
		example.marbleDiagram();
	}
}
