import io.reactivex.Observable;

public class HelloWorld {
	public void emit() {
		Observable.just("Hello", "Rxjava 2!!")
		.subscribe(System.out::println);
	}
	
	public static void main(String args[]) {
		HelloWorld demo = new HelloWorld();
		demo.emit();
	}
}
