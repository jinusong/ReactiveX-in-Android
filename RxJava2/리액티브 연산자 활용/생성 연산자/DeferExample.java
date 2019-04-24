import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Callable;
import io.reactivex.Observable;

public class DeferExample{
	Iterator<String> colors = Arrays.asList("1", "3", "5", "6").iterator();
	
	public void marbleDiagram() {
		Callable<Observable<String>> supplier = () -> getObservable();
		Observable<String> source = Observable.defer(supplier);
		
		source.subscribe(val -> Log.it("Subscriber #1:" + val));
		source.subscribe(val -> Log.it("Subscriber #2:" + val));
		CommonUtils.exampleComplete();
	}
	
	// 번호가 적힌 도형을 발행하는 Observable을 생성합니다.
	private Observable<String> getObservable() {
		if(colors.hasNext()) {
			String color = colors.next();
			return Observable.just(
					Shape.getString(color, Shape.BALL),
					Shape.getString(color, Shape.RECTANGLE),
					Shape.getString(color, Shape.PENTAGON));
		}
		
		return Observable.empty();
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
	
	public static void exampleComplete() {
		startTime = System.currentTimeMillis();
	}
}
