import io.reactivex.Observable;

public class RangleExample {
	public void forLoop() {
		Observable<Integer> source = Observable.range(1, 10)
				.filter(number -> number % 2 == 0);
		source.subscribe(Log::it);
	}
}

class Log {
	public static void it(Object obj) {
		long time = System.currentTimeMillis() - CommonUtils.startTime;
		System.out.println(getThreadName() + " | " + time + " | " + "value = " + obj);
	}
}
