import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;

public class GroupByExample {
	public void marbleDiagram() {
		String[] objs = {"6", "4", "2-T", "2", "6-T", "4-T"};
		Observable<GroupedObservable<String, String>> source = Observable.fromArray(objs).groupBy(CommonUtils::getShape);
		
		source.subscribe(obj -> {
			obj.subscribe(
					val -> System.out.println("GROUP:" + obj.getKey() + "\t Value:" + val));
		});
	}
	
	public void filterBallGroup() {
		String[] objs = {"6", "4", "2-T", "2", "6-T", "4-T"};
		Observable<GroupedObservable<String, String>> source = 
				Observable.fromArray(objs).groupBy(CommonUtils::getShape);
		
		source.subscribe(obj -> {
			obj.filter(val -> obj.getKey().equals(Shape.BALL))
			.subscribe(val -> 
			System.out.println("GROUP:" + obj.getKey() + "\t Value:" + val));
		});
	}
	
	class Shape{
		String getShape(String obj) {
			if (obj == null || obj.equals("")) return "NO-SHAPE";
			if (obj.endsWith("-H")) return "HEXAGON";
			if (obj.endsWith("-O")) return "OCTAGON";
			if (obj.endsWith("-R")) return "RECTANGLE";
			if (obj.endsWith("-T")) return "TRIANGLE";
			if (obj.endsWith("nemo")) return "DIAMOND";
			return "BALL";
		}
	}
	
}
