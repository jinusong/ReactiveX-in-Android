# RxAndroidMemoryManage
## 메모리 누수란?
* 메모리 누수란 보통 참조가 완료되었지만 할당한 메모리를 해제하지 않아서 발생합니다.
* 특히 강한 참조의 경우 갈비지 컬렉터가 메모리에서 객체를 제거할 수 없으므로 라이프 사이클에 맞게 객체 참조를 끊어야 사용하지 않는 메모리를 해제할 수 있습니다.
* 메모리 누수는 시스템 전체 성능에 영향을 미치므로 중요하게 관리해야 합니다.
### 메모리 누수를 찾아보자
~~~java
Observer<String> observer = new DisposableObserver<String>() {
    @Override
    public void onNext(String s) {
        textView.setText(s);
    }

    @Override
    public void onError(Throwable e) { }

    @Override
    public void onComplete() { }
};

Observable.create(new ObservableOnSubscribe<String() {
    @Override
    public void subscribe(ObservableEmitter<String> e) throws Exception {
        e.onNext("hello world!");
        e.onComplete();
    }
}).subscribe(observer);
~~~

* 정상 동작하며 큰 문제가 없어 보이지만 메모리 누수 문제가 있는 코드입니다. 
* Observable은 안드로이드의 컨텍스트를 복사하여 유지합니다.
* onComplete(), onError() 함수가 호출되면 내부에서 자동으로 unsubscribe() 함수를 호출합니다.

* 그런데 구독자가 텍스트뷰를 참조하기 때문에 액티비티가 비정상적으로 종료되면 텍스트뷰가 참조하는 액티비티는 종료해도 가비지 컬렉션의 대상이 되지 못합니다.
* 따라서 메모리 누수가 발생하게됩니다.

### 해결해볼래?
* 안드로이드 앱 프로그래밍은 액티비티의 라이프 사이클을 다루는 작업이라 해도 과언이 아닙니다.
* 안드로이드와 subscribe() 함수의 라이프사이클을 고혀하여 잘 설계해야 합니다.
* 이런 문제를 해결하기 위해 방법 3가지가 필요합니다.

#### Disposable 인터페이스를 이용하여 명시적으로 자원 해제
* onCreate() 메서드에서 subscribe() 함수를 호출하면 onDestroy() 메서드에서 메모리 참조를 해제하고 
* onResume() 메서드에서 subscribe() 함수를 호출하면 onPause() 메서드에서 메모리 참조를 해제합니다.

~~~java
@BindView(R.id.textView) TextView textView;

private Disposable mDisposable;
private Unbinder mUnbinder;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(sabedInstanceState);
    setContentView(R.layout.activity_main);
    mUnbinder = ButterKnife.bind(this);

    DisposableObserver<String> observer = new DisposableObserver<String>() {
        @Override
        public void onNext(String s) {
            textView.setText(s);
        }

        @Override
        public void onError(Throwable e) { }

        @Override
        public void onComplete() { }
    };

    mDisposable = Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> e) throws Exception (
            e.onNext("Hello world!");
            e.onComplete();
        )
    }).subscribeWith(observer);
}

@Override
protected void onDestroy() {
    super.onDestroy();
    if (!mDisposable.isDisposed()) {
        mDisposable.dispose();
    }
    if (mUnbinder != null) {
        mUnbinder.unbind();
    }
}
~~~
* RxJava 1.x에서는 Subscription 인터페이스를 이용하여 스트림과 자원의 라이프 사이클을 관리할 수 있습니다.
* 하지만 2.x에서는 Subscription 인터페이스가 Disposable 인터페이스로 변경되었습니다.
* 그리고 기존의 Publisher.subscribe()는 void를 반환하므로 EsubscribeWith(E subscriber) 함수를 이용하여 구독자를 그대로 리턴해주어야 합니다.

#### RxLifecycle 라이브러리 이용
* 액티비티의 부모를 RxAppCompatActivity으로 변경하고 compose() 함수를 사용하여 Rxlifecycle 라이브러리를 적용할 수 있습니다.

~~~java
public class HelloRxAppActivity extends RxAppCompatActivity {
    public static final String TAG = HelloRxAppActivity.class.getSimpleName();

    @BindView(R.id.textView) TextView mTextView;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("Hello world!");
                e.onComplete();
            }
        }) // .compose(bindToLifecycler())
        .compose(bindUntilEvent(ActivityEvent.DESTROY))
        .subscribe(mTextView::setText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
~~~

* RxLifecycle 라이브러리를 적용한 코드입니다. DIsposable 인터페이스를 사용할 때보다 코드가 더 단순하게 변경된 것을 확인할 수 있습니다.
* 플로우는 다음과 같습니다.

    * 1. RxLifecycler을 사용하기 위해 RxAppCompatActivity 클래스를 상속합니다.
    * 2. compose() 함수를 이용하여 RxLifecycle 라이브러리를 설정합니다. 
        * 설정하는 방법은 Observable 자체를 안드로이드 액티비티에 바인딩하는 방법과 액티비티의 특정 콜백 메서드에 바인딩하는 방법이 있습니다.
        * bindToLifecycler() 함수를 사용하게 되면 onCreate() -> onDestroy() 메서드와 onResume() - onPause() 메서드가 싸으로 동작합니다.
        * 즉, onCreate() 메서드에서 subscribe() 함수를 호출하면 onDestroy() 메서드에서 unsubscribe() 함수를 호출합니다.
    * 3. 종료되는 시점은 직접 bindUntilEvent() 함수를 선언하여 바꿀 수 있습니다., 바꾸는 방법은 ActivityEvent를 참고하시기 바랍니다.
    * 4. onDestroy() 메서드에서는 더 이상 dispose() 함수가 필요 없으므로 삭제합니다. 

#### CompositeDisposable 클래스 이용
* CompositeDisposable 클래스를 이용하면 생성된 모든 Observable을 안드로이드 라이프 사이클에 맞춰 한 번에 모두 해제할 수 있습니다.

~~~java
public class  HelloActivityComposite extends AppCompatActivity {
    public static final String TAG = HelloActivityComposite.class.getSimpleName();

    @BindView(R.id.text)
    TextView textView;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);

        Disposable disposable = 
            Observable.create(new  ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> e) throws Exception {
                    e.onNext("hello world!");
                    e.onComplete();
                }
            }).subscribe(textView::setText);
            mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }

        if(mUnbinder != null)  {
            mUnbinder.unbind();
        }
    }
}
~~~

* CompositeDisposable 클래스를 이용하는 방법은 Disposasble 해결책의 연장선에 있습니다.
* DisposableObserver 객체를 직접 해지한다면 이 방법은 Publisher.subscribe() 함수를 이용하여 Disposable를 리턴한 후 CompositeDisposable 클래스에서 일관 관리합니다.
* Publisher.subscribe()는 void를 리턴하므로 새로운 subscribeWith() 함수를 사용하거나 인자에 구독자(Observer)가 아닌 소비자(Consumer)를 전달해서 Disposable 객체를 리턴받아야 합니다.

* clear()와 dispose() 함수 모두 등록된 Disposable 객체를 삭제한다는 점은 같습니다.
* 그러나 clear() 함수의 경우 계속 Disposable 객체를 받을 수 있지만 dispose() 함수의 경우 isDisposed() 함수를 true로 설정하여 새로운 Disposable 객체를 받을 수 없습니다.