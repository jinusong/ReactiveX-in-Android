import java.io.File;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class IOSchedulerExample {
	public void basic() {
		// C 드라이브 루트 디렉터리에 파일 목록 생성.
		String root = "C:\\";
		File[] files = new File(root).listFiles();
		Observable<String> source = Observable.fromArray(files)
				.filter(f -> !f.isDirectory())
				.map(f -> f.getAbsoltePath())
				.subscribeOn(Schedulers.io());
		
		source.subscribe(Log::i);
		CommonUtils.sleep(500);
	}
}
