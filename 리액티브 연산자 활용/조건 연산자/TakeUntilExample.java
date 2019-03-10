import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class TakeUntilExample {
	
	public void marbleDiagram() {
		String[] data = {"1", "2", "3", "4", "5", "6"};
		
		Observable<String> source  = Observable.fromArray(data)
				.zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (val,  notUsed) ->  val)
				.takeUntil(Observable.timer(500L, TimeUnit.MILLISECONDS));
		
		source.subscribe(Log::i);
		CommonUtils.sleep(1000);
	}
}
