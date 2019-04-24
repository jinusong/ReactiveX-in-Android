import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ExecutorSchedulerExample {
	public void basic() {
		final int THREAD_NUM = 10;
		
		String[] data = {"1", "3", "5"};
		Observable<String> source = Observable.fromArray(data);
		Executor executor = Executors.newFixedThreadPool(THREAD_NUM);
		
		source.subscribe(Schedulers.from(executor))
			.subscribe(Log::i);
		source.subscribeOn(Schedulers.from(executor))
			.subscribe(Log::i);
		
		CommonUtils.sleep(500);
	}
}
