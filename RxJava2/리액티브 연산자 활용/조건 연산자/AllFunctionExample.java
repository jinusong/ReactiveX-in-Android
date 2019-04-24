import io.reactivex.Observable;

public class AllFunctionExample {
	String[] data = {"1", "2", "3", "4"};
	
	Single<Boolean> source = Observable.fromArray(data)
			.map(Shape::getShape)
			.all(Shape.BALL::equals);
			//.all(val -> Shape.BALL.equals(Shape.getShape(val)));
	source.subscribe(Log::i);
}
