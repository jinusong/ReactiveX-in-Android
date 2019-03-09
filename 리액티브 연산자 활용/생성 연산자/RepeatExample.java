import java.io.IOException;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;

public class RepeatExample {
	public void marbleDiagram() {
		String[] balls = {"1", "3", "5"};
		Observable<String> source = Observable.fromArray(balls)
				.repeat(3);	// 3번 반복 실행합니다.
		
		// onComplete() 함수를 호출했을 때 로그 출력.
		source.doOnComplete(() -> Log.d("onComplete"))
			.subscribe(Log::it);
	}
	
	public void heartbeatV1() {
		CommonUtils.exampleStart();
		String serverUrl = "https://api.github.com/zen";
		
		// 2초 간격으로 서버에 ping 보내기
		Observable.timer(2, TimeUnit.SECONDS)
		.map(val -> serverUrl)
		.map(OkHttpHelper::get)
		.repeat()
		.subscribe(res -> Log.it("Ping Result : " + res));
		CommonUtils.sleep(10000);
	}
}

class OkHttpHelper {
	private static OkHttpClient client = new OkHttpClient();
	
	public static String get(String url) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.build();
		
		try {
			Response res = client.newCall(request).execute();
			return res.body().String();
		} catch (IOException e) {
			Log.e(e.getMessage());
			throw e;
		}
	}
}

class Log {
	public static void it(Object obj) {
		long time = System.currentTimeMillis() - CommonUtils.startTime;
		System.out.println(getThreadName() + " | " + time + " | " + "value = " + obj);
	}
}

class CommonUtils{
	// 실행 시간을 표기하기 위한 정적변수.
	public static long startTime;
	
	public static void exampleStart() {
		startTime = System.currentTimeMillis();
	}
	
	public static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}