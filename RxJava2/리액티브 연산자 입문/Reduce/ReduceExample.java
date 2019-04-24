import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;

public class ReduceExample {
	public void marbleDiagram() {
		String[] balls = {"1", "3", "5"};
		Maybe<String> source = Observable.fromArray(balls)
				.reduce((ball1, ball2) -> ball2 + "(" + ball1 + ")");
		source.subscribe(System.out::println);
	}
	public void biFunction() {
		BiFunction<String, String, String> mergeBalls = 
				(ball1, ball2) -> ball2 + "("  + ball1 + ")";
		String[] balls = {"1", "3", "5"};
		Maybe<String> source = Observable.fromArray(balls)
				.reduce(mergeBalls);
		source.subscribe(System.out::println);
	}
	
	// 데이터 쿼리하기 (compile 'org.apache.commons:commons-lang3:3.1') - Lang3 라이브러리의 Pair 사용 
	
	// 1. 데이터 입력 
	// 왼쪽에는 상품 이름, 오른쪽에는 매출액
	List<Pair<String, Integer>> sales = new ArrayList<>();
	
	sales.add(Pair.of("TV", 2500));
	sales.add(Pair.of("Camera", 300));
	sales.add(Pair.of("TV", 1600));
	sales.add(Pair.of("Phone", 800));
	
	Maybe<Integer> tvSales = Observable.fromArray(sales)
			// 2. 매출 데이터 중 TV 매출을 필터링함 
			.filter(sale -> "TV".equals(sale.getLeft()))
			.map(sale -> sale.getRight())
			// 3. TV 매출의 합을 구함 
			.reduce((sale1, sale2) -> sale1 + sale2);
	tvSales.subscribe(tot -> System.out.println("TV Sales: $" + tot));
}
