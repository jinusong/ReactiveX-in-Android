import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class CallbackHeaven {
	private static final String GITHUB_ROOT = 
			"https://raw.githubusercontent.com/yudong80/reactivejava/amster/";
	private static final String FIRST_URL = "https://api.github.com/zen";
	private static final String SECOND_URL = GITHUB_ROOT + "/samples/callback_hell";
	
	public void usingConcat() {
		CommonUtils.exampleStart();
		Observable<String> source = Observable.just(FIRST_URL)
				.subsctiveOn(Schedulers.io())
				.map(OkHttpHelper::get)
				.concatWith(Observable.just(SECOND_URL)
						.map(OkHttpHelper::get));
		source.subscribe(Log::it);
		CommonUtils.sleep(5000);
	}
	
	public void usingZip() {
		CommonUtils.exampleStart();
		
		Observable<String> first = Observable.just(FIRST_URL)
				.subscriveOn(Schedulers.io())
				.map(OkHttpHelper::get);
		Observable<String> second = Observable.just(SECOND_URL)
				.subscribeOn(Schedulers.io())
				.map(OkHttpHelper::get);
		
		Observable.zip(first, second, (a, b) -> ("\n>> " + a + "\n>> " + b))
			.subscribe(Log::it);
		
		CommonUtils.sleep(5000);
	}
}
