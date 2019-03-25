import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ComputationSchedulerExample {
	public void basic() {
		String[] orgs = {"1", "3", "5"};
		Observable<String> source = Observable.fromArray(orgs)
				.zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a, b) -> a);
		
		// 구독(Subscription) #1
		source.map(item -> "<<" + item + ">>")
			.subscribeOn(Schedulers.computation())
			.subscribe(Log::i);
		
		// 구독(Subscription) #2
		source.map(item -> "##" + item + "##")
			.subscribeOn(Schedulers.computation())
			.subscribe(Log::i);
		CommonUtils.sleep(1000);
	}
}
