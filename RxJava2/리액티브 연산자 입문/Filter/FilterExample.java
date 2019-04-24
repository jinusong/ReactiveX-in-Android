import io.reactivex.Observable;
import io.reactivex.Single;

public class FilterExample {
	public void marbleDiagram() {
		String[] objs = {"1 CIRCLE", "2 DIAMOND", "3 TRIANGLE", 
				"4 DIAMOND", "5 CIRCLE", "6 HEXAGON" };
		Observable<String> source = Observable.fromArray(objs)
				.filter(obj -> obj.endsWith("CIRCLE"));
		source.subscribe(System.out::println);
	}
	public void evenNumbers() {
		Integer[] data = {100, 34, 27, 99, 50};
		Observable<Integer> source = Observable.fromArray(data)
				.filter(number -> number % 2 == 0);
		source.subscribe(System.out::println);
	}
	public void otherFilters() {
		Integer[] numbers = {100, 200, 300, 400, 500};
		Single<Integer> single;
		Observable<Integer> single2;
		Observable<Integer> source;
		
		// 1. first
		single = Observable.fromArray(numbers).first(-1);
		single.subscribe(data -> System.out.println("first() value = " + data));
		
		// 2. last
		single = Observable.fromArray(numbers).last(999);
		single.subscribe(data -> System.out.println("last() value = " + data));
				
		// 3. take(N) 
		single2 = Observable.fromArray(numbers).take(3);
		single2.subscribe(data -> System.out.println("take(3) value = " + data));
		
		// 4. takeLast(N)
		single2 = Observable.fromArray(numbers).takeLast(3);
		single2.subscribe(data -> System.out.println("takeLast(3) value = " + data));
				
		// 5. skip(N)
		single2 = Observable.fromArray(numbers).skip(2);
		single2.subscribe(data -> System.out.println("skip(2) value = " + data));		
		
		// 6. skipLast(N)
		single2 = Observable.fromArray(numbers).skipLast(2);
		single2.subscribe(data -> System.out.println("skipLast(2) value = " + data));
	}
}
