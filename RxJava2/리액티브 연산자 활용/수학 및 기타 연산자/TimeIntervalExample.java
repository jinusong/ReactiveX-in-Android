import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.schedulers.Timed;

public class TimeIntervalExample {
	public void marbleDiagram() {
		String[] data = {"1", "3", "7"};
		
		CommonUtils.exampleStart();
		Observable<Timed<String>> source = Observable.fromArray(data)
				.delay(item -> {
					CommonUtils.doSomething();
					return Observable.just(item);
				})
				.timeInterval();
		
		source.subscribe(Log::it);
		CommonUtils.sleep(1000);
	}
	
	public void doSomething() {
		try {
			Thread.sleep(new Random().nextInt(100));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
