import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class TrampolineSchedulerExample {
	public void basic() {
		String[] orgs = {"1", "3", "5"};
		Observable<String> source = Observable.fromArray(orgs);
		
		// 구독 #1
		source.subscribeOn(Schedulers.trampoline())
			.map(data -> "<<" + data + ">>")
			.subscribe(Log::i);
		
		// 구독 #2
		source.subscribeOn(Schedulers.trampoline())
			.map(data -> "##" + data + "##")
			.subscribe(Log::i);
		
		CommonUtils.sleep(500);
	}
}
