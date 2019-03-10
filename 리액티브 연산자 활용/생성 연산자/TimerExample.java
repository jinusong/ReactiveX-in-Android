import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;

public class TimerExample {
	public void showTime() {
		CommonUtils.exampleStart();
		Observable<String> source = Observable.timer(500L, TimeUnit.MILLISECONDS)
				.map(notUsed -> {
					return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
							.format(new Date());
				});
		source.subscribe(Log::it);
		CommonUtils.sleep(1000);
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

class Log {
	public static void it(Object obj) {
		long time = System.currentTimeMillis() - CommonUtils.startTime;
		System.out.println(getThreadName() + " | " + time + " | " + "value = " + obj);
	}
}