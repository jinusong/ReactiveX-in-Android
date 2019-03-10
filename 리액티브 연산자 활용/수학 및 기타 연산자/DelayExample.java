import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class DelayExample {
	public void marbleDiagram() {
		String[] data = {"1", "7", "2", "3", "4"};
		Observable<String> source = Observable.fromArray(data)
				.delay(100L, TimeUnit.MILLISECONDS);
		source.subscribe(Log::it);
		CommonUtils.sleep(1000);
	}
}
