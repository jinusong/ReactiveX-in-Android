import io.reactivex.Observable;

public class Just {
	public void emit() {
		Observable.just(1, 2, 3, 4, 5, 6)
		.subscribe(System.out::println);
	}
	
	public static void main(String[] args){
		Just demo = new Just();
		demo.emit();
	}
}