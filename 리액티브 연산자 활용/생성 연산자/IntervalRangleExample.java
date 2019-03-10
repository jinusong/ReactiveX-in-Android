import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class IntervalRangleExample {
	public void printNumbers() {
		Observable<Long> source = Observable.intervalRange(1, // start
				5,		// count
				100L, 	// initialDelay
				100L, 	// period
				TimeUnit.MILLISECONDS);	//unit
		source.subscribe(Log::it);
		CommonUtils.sleep(1000);
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