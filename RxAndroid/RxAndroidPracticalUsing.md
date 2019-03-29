# RxAndroidPracticalUsing
* RxAndroid를 활용해보자
## 리액티브 RecyclerView
* RxAndroid를 이용하여 RecyclerView를 구현합니다.
* 설치된 앱의 정보를 읽어와서 보여주는 간단한 예제입니다.
* 이를 통해 리액티브를 이용한 리스너 및 뜨거운 Observable의 사용방법을 배울 수 있습니다.
### 설치된 앱 리스트 나열하기
~~~java
@Dao
@AllArgsConstructor(staticName="of")
public class RecyclerItem {
    Drawable image;
    String title;
}
~~~
~~~java
class MyViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.item_image)
    ImageView mImage;

    @BindView(R.id.item_title)
    TextView m Title;

    private MyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    Observable<RecyclerItem> getClickObservaer(RecyclerItem item) {
        return Observable.create(e -> itemView.setOnClickListener(
            view -> e.onNext(item)));
    }
}
~~~
* 생성자의 인자로 itemView를 전달받으면 super() 메서드를 이용하여 부모 클래스의 RecyclerView.ViewHolder에 정의되어 있는 public final View itemView에 값을 오버라이딩합니다.
* 다음에는 ButterKnife 라이브러리를 이용하여 itemView를 로컬 변수(mImage, mTitle)와 바인딩해줍니다.
* 안드로이드 프로그래밍에서는 생성자에 Click 리스너 이벤트를 넣어주는 게 일반적이지만 리액티브 프로그래밍에서는 Click 이벤트를 분리된 Observable에 생성합니다. 이렇게 콜백지옥을 대체합니다.

~~~java
class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    // 아이템 리스트
    private List<RecyclerItem> mItems = new ArrayList<>();

    // 아이템을 클릭하면 실행되는 이벤트를 Observable Fragment에서 최종 처리합니다.
    private PublishSubject<RecyclerItem> mPublishSubject;

    RecyclerViewAdapter() {
        this.mPublishSubject = PublishSubject.create();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.recycler_view_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final RecyclerItem item = mItems.get(position);
        holder.mImage.setImageDrawable(item.getImage());
        holder.mTitle.setText(item.getTitle());
        holder.getClickObserver(item).subscribe(mPublishSubject);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void updateItems(List<RecyclerItem> items) {
        mItems.addAll(items);
    }

    public void updateItems(RecyclerItem item) {
        mItems.add(item);
    }

    public PublishSubject<RecyclerItem> getItemPublishSubject() {
        return mPublishSubject;
    }
}
~~~

* Adapter 클래스는 onCreateViewHolder(), onBindVoewHolder(), getItemCount() 라는 3개의 메서드를 구현해 주어야합니다.
* 다음은 Fragment입니다.
~~~java
private Observable<RecyclerItem> getItemObservable() {
    final PackageManager pm = getActivity().getPackageManager();
    Intent i = new Intent(Intent.ACTION_MAIN, null);
    i.addCategory(Intent.CATEGORY_LAUNCHER);

    return Observable.fromIterable(pm.queryIntentActivities(i, 0))
        .sorted(new ResolveInfo.DisplayNameComparator(pm))
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .map(item -> {
            Drawable image = item.activityInfo.loadIcon(pm);
            String title = item.activityInfo.loadLabel(pm).toString();
            return RecyclerItem.of(image, title);
        });
}
~~~
* PackageManager 클래스를 이용해 설치된 앱 정보를 가져와 RecyclerItem 객체로 변경하는 간단한 함수입니다.
* queryIntentActivities() 메서드는 설치된 앱 중 CATEGORY_LAUNVHER 타입의 앱만 결과로 가져오게 됩니다.
* 가져온 결과는 앱 이름으로 정렬하고 이미지와 타이틀을 추출하여 RecyclerItem 객체를 생성합니다.