# RxLifecycle

## RxLifecycle 왜 쓰지요?
* 먼저 RxAndroid에서는 RxLifcycle이라는 라이브러리를 제공합니다.
* 안드로이드의 액티비티와 프래그먼트의 라이프 사이클을 RxJava에서 사용할 수 있게합니다.
* 안드로이드와 UI 라이프 사이클을 대체한다기보다 구독할 때 발생할 수 있는 메모리 누수를 방지하기 위해 사용합니다. 
* 완료하지 못한 구독을 자동으로 해제합니다.

## 제공되는 컴포넌트들
* RxLifcycle 라이브러리는 안드로이드의 라이프 사이클에 맞게 Observable을 관리할 수 있는 컴포넌트를 제공합니다.
    * RxActivity: 액티비티에 대응합니다.
    * RxDialogFragment: Native/Support 라이브러리인 DialogFragment에 대응합니다.
    * RxFragment: Native/Support 라이브러리인 Fragment에 대응합니다.
    * RxPreferenceFragment: PreferenceFragment에 대응합니다.
    * RxAppCompatActivity: Support 라이브러리 AppCompatActivity에 대응합니다.
    * RxAppCompatDialogFragment: Support 라이브러리 AppCompatDialogFragment에 대응합니다.
    * RxFragmentActivity: Support 라이브러리 FragmentActivity에 대응합니다.

* 사용해봅니다.
~~~gradle
'com.trello.rxlifecycle2:rxlifecycle:2.0.1'
'com.trello.rxlifecycle2:rxlifecycle-android:2.0.1'
'com.trello.rxlifecycle2:rxlifecycle-components:2.0.1'
~~~
* 예제
~~~gradle
public class HelloActivityV3 extends RxAppCompatActivity {
    public static final Strint TAG = HelloActivityV3.class.getSimpleName();

    @BindView(R.id.textView) TextView textView;

    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        mUnbinder = ButterKnife.bind(this);

        Observable.just("Hello, rx world!")
            .compose(bindToLifecycle)
            .subscribe(textView::setText);
    }

    @Override
    protected  void  onDestroy() {
        super.onDestroy();
        if (mUnbinder !=  null)  {
            mUnbinder.unbind();
        }
    }
}
~~~
* AppCompatActivity 클래스 대신 RxAppCompatActivity 클래스를 상속하도록 바꿨고, Observable 생성 부분에서는 compose() 함수로 라이프 사이클을 관리하도록 추가했습니다.
* Observable은 HelloActivityV3 클래스가 종료되면 자동으로 해제됩니다.

* 한편 RxJava 2.x에서는 RxLifecycle의 컴포넌트 이외에도 메모리 관리를 위한 방법을 제공합니다.
* 예를 들어  RxJava에 익숙한 개발자들은 안드로이드의 전통적인 라이프 사이클 관리 기법보다는 직접 관리하기 편한 dispose() 함수를 사용하는 것 등이 있습니다.
* 어떤 것이 좋다고 이야기하기는 어렵고 각각 장, 단점이 있습니다. 상황에 맞게 개발자가 잘 선택하여 사용하면 됩니다.