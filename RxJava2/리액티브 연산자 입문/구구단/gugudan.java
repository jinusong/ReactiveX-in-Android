import java.util.Scanner;
import java.util.function.Function;

import io.reactivex.Observable;

public class gugudan {
	
	public void reactiveV1() {
		Scanner in = new Scanner(System.in);
		System.out.println("Gugudan Input: ");
		int dan = Integer.parseInt(in.nextLine());
		
		Observable<Integer> source = Observable.range(1, 9);
		source.subscribe(row -> System.out.println(dan + " * " + row + " = " + dan * row ));
	}
	
	public void reactiveV2() {
		Scanner in = new Scanner(System.in);
		System.out.println("Gugudan Input: ");
		int dan = Integer.parseInt(in.nextLine());
		
		Function<Integer, Observable<String>> gugudan = num -> 
			Observable.range(1, 9).map(row -> num + " * " + row + " = " + dan*row);
		Observable<String> source = Observable.just(dan).flatMap(gugudan);
		source.subscribe(System.out::println);
	}
	
	public void reactiveV3() {
		Scanner in = new Scanner(System.in);
		System.out.println("Gugudan Input: ");
		int dan = Integer.parseInt(in.nextLine());
		
		Observable<String> source = Observable.just(dan)
				.flatMap(num -> Observable.range(1, 9)
						.map(row -> num + " * " + row + " = " + dan*row));
	}
	
	public void usingResultSelector() {
		Scanner in = new Scanner(System.in);
		System.out.println("Gugudan Input: ");
		int dan = Integer.parseInt(in.nextLine());
		Observable<String> source = Observable.just(dan)
				.flatMap(gugu -> Observable.range(1, 9),
						(gugu, i) -> gugu + " * " + i + " = " + gugu*i);
		source.subscribe(System.out::println);
		in.close();
	}
}
