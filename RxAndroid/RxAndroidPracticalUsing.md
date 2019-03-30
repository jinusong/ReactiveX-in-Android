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

### Fragment에서 불필요한 점을 없애보자
~~~java
public class RecyclerViewFragment extends Fragment {
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;

    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Unbinder mUnbinder;

    @Nullalbe
    @Override
    public View onCreateView(LayoutInflater inflater, 
        @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // 내용 입력
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerViewAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter
            .getItemPublishSubject()
            .subscribe(s -> toast(s.getTitle()));
    }

    @Override
    public void onStart() {
        super.omStart();

        if(mRecyclerViewAdapter == null) {
            return;
        }

        getItemObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(item -> {
                mRecyclerViewAdapter.updateItems(item);
                mRecyclerViewAdapter.notifyDataSetChanged();
            });
    }

    private void toast(String title) {
        Toast.makeText(getActivity().getApplicationContext(), title, Toast.LENGTH_SHORT).show();
    }
}

~~~
* 클래스에서 처리하는 부분은 두 가지입니다. 리사이클러뷰를 생성하는 부분과 리스트를 클릭하면 콜백을 받고 toast 팝업을 생성하는 부분입니다.
    * 1. onActivityCreated() 메서드에서는 LayoutManager 객체와 Adapter 객체를 생성하여 리사이클러 뷰에서 사용할 수 있도록 설정합니다. 이걸로 기본적인 RxAndroid의 RecyclerView가 완성되었습니다.
    * 2. onStart() 메서드가 호출되면 설치된 애플리케이션의 정보가 RecyclerViewAdapter 객체에 업데이트되고 출력됩니다.
    * 3. 설치한 앱의 정보를 이용하여 아이콘과 이름으로 리스트 아이템을 구성하고, 리스트를 클릭하면 앱의 이름을 toast 팝업으로 보여줍니다.

## 안드로이드 스레드를 대체하는 RxAndroid
* 안드로이드는 기본적으로 싱글 스레드 모델입니다. 그래서 처리하는 데 오래 걸리는 데이터 전송이나 파일 입,출력 등은 별도의 스레드로 분리하여 작업해야 합니다.
* 이 부분을 고려하지 않고 앱을 개발하면 성능이 나빠지거나 애플리케이션이 응답하지 않는 현상이 발생하기도 합니다.
* 그래서 스레드를 효과적으로 관리하려면 스케줄러를 만들어 관리해야 합니다.

### 뷰와 뷰 그룹의 스레드 관리
* 안드로이드의 뷰나 뷰 그룹은 UI스레드에서만 업데이트할 수 있게 설계되어 있습니다.
* 여러 스레드에서 동시에 UI를 업데이트할 때 발생할 수 있는 동기화 문제를 해결하기 위함입니다.
* 그럼 일반 스레드에서 작업한 결과를 어떻게 뷰에 업데이트할 수 있을까요? 안드로이드는 이를 위해 Looper와 Handler 클래스를 제공합니다.
* 안드로이드는 스레드 관리를 위해 Handler 클래스를 좀 더 사용하기 쉽게 래핑한 HandlerThread 클래스, AsyncTask 클래스도 제공합니다.