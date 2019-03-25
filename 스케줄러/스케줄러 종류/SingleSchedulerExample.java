import io.reactivex.Observable;

public class SingleSchedulerExample {
	public void basic() {
		Observable<Integer> numbers = Observable.range(100, 5);
		Observable<String> chars = Observable.range(0, 5)
				.map(CommonUtils::numberToAlphabet);
		
		numbers.subscribe(Schedulers.single())
			.subscribe(Log::i);
		chars.subscribeOn(Schedulers.single())
			.subscribe(Log::i);
		CommonUtils.sleep(500);
	}
}