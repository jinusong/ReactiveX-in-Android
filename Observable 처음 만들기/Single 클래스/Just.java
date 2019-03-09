import io.reactivex.Observable;
import io.reactivex.Single;

public class Just {
	public void emit() {
		Single<String> source = Single.just("Hello Single");
		source.subscribe(System.out::println);
	}
	
	public static void main(String[] args){
		Just demo = new Just();
		demo.emit();
	}
}