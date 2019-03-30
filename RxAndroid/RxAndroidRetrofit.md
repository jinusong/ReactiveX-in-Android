# RxAndroidRetrofit
## Retrofut2 + OkHttp 활용하기
### 물론 알고 있겠지만 간단한 Retrofit2와 OkHttp에 대한 설명
#### OkHttp
* OkHttp는 안드로이드에서 사용할 수 있는 대표 클라이언트 중 하나이며 페이스북에서 사용하는 것으로 유명합니다.
* SPDY/GZIP 지원 등 네트워크 스택을 효율적으로 관리할 수 있고 빠른 응답 속도를 보일 수 있다는 장점이 있습니다.

#### Retrofit2
* Retrofit은 서버 연동과 응답 전체를 관리하는 라이브러리입니다.
* OkHttp가 서버와의 연동 관련 기능만 제공한다면 응답까지 관리해준다는 면에서 편리합니다.
* Retrofit 1.x 버전에서는 OkHttp, HttpClient 등 사용자가 원하는 클라이언트를 선택해서 사용할 수 있었지만 2.x 버전에서는 HttpClient는 더 이상 사용할 수 없고 OkHttp에 의존하도록 변경되었습니다.

#### Retrofit2의 장점
* Retrofit2의 장점 중 하나는 어노테이션을 지원하는 것입니다. 스프링처럼 어노테이션으로 API를 설계할 수 있습니다.

~~~java
public interface GitHubServiceApi {
    @GET("repos/{owner}/{repo}/contributors")
    Call<List<Contributor>> getCallContributors (
        @Path("owner") String owner, @Path("repo") String repo);
    @GET("repos/{owner}/{repo}/contributors")
    Observable<List<Contributor>> getCallContributors (
        @Path("owner") String owner, @Path("repo") String repo);
    @Headers({"Accept: application/vnd.github.v3.full+json"})
    @GET("repos/{owner}/{repo}/contributors")
    Call<List<Contributor>> getCallContributorsWithHeader(
        @Path("owner") String owner, @Path("repo") String repo);
}
~~~
* Retrofit은 RxJava를 정식으로 지원하므로 Observable을 API 리턴값으로 사용할 수 있습니다.
* 그 외에 Call과 Future 인터페이스도 지원합니다. 정의한 API를 사용할 수 있는 사용자화 Adapter 클래스를 만들어봅시다.
~~~java
public calss RestfulAdapter {
    private static final String BASE_URI = "https://api.github.com/";

    public GitHubServiceApi getSimpleApi() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URI)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        return retrofit.create(GitHubServiceApi.class);
    }

    public GitHubServiceApi getServiceApit() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .builder();

        Retrofit retrofit = new Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(BASE_URL)
            .build();

        return retrofit.create(GitHubServiceApi.class);
    }

    private RetfulAdapter() {}

    private static class Singleton {
        private static final RestfulAdapter instance = new ResfulAdapter();
    }

    public static RestfulAdapter getInstance() {
        return Singleton.instance;
    }
}
~~~
* getSimpleApi()와 getServiceApi() 메서드의 차이점은 REST API 스택의 디버깅이 가능한지 여부입니다.
* getSimpleApi() 메서드의 경우는 Retrofit에 포함된 OkHttpClient 클래스를 사용하게 되고 getServiceApi() 메서드는 따로 OkHttpClient.Builder() 객체를 구성하여 로그를 위한 인터셉터를 설정합니다.
* 인터셉터를 설정하면 네트워크를 통해 이동하는 데이터나 에러 메시지를 실시간으로 확인할 수 있습니다.

~~~java
public class Contributor {
    String login;
    String url;
    int id;

    @Override
    public String toString() {
        return "login : " + login + "id : " + id + "url : " + url;
    }
}
~~~
* 이렇게 모델을 작성하구요.
~~~java
public class OkHttpFragment extends Fragment {

    // 생략

    /**
    * Retrofit + OkHttp + RxJava
    */

    private void startRx() {
        GitHubServiceApi service = RestfulAdapter.getInstance().getServiceApi();
        Observable<List<Contributor>> observable = 
            service.getObContributors(sName, sRepo);

        mCompositeDisposable.add(
            observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Contributor>>(){
                    @Override
                    public void onNext(List<Contributor> contributors) {
                        for(Contributor c : contributors) {
                            log(c.toString());
                        }
                    }

                    // 생략
                }));
    }

    /**
    * retrofit + okHttp(Retrofit의 내부)
    */

    private void startRetrofit() {
        GitHubServiceApi service = RestfulAdapter.getInstance().getSimpleApi();
        call.equeue(new Callback<List<Contributor>>() {
            @Override
            public void onReponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                if (response.isSuccessful()) {
                    List<Contributor> contributors = response.body();
                    for (Contributor c: contributors) {
                        log(c.toString());
                    }
                }
                else {
                    log("not successful");
                }
            }
            @Override
            public void onFailure(Call<List<Contributor>> call, Throwable t) {
                log(t.getMessage());
            }
        });
    }

    /**
    * Retrofit + OkHttp
    */

    private void startOkHttp() {
        GitHubServiceApi service = RestfulAdapter.getInstance().getServiceApi();
        // 생략
    }
}
~~~
* startRx() 메서드는 RestfulAdapter 클래스의 getServiceApi() 메서드 안 retrofit 변수를 이용해 생성된 API 프록시를 가져옵니다.
* owner와 repo의 값을 전달하면 observable 변수에 저장된 Observable을 리턴합니다.
* 생성된 Observable에 구독자를 설정하면 getServiceApi() 메서드를 호출하여 github.com에서 정보를 얻어옵니다.
* 결과는 구독자가 수신하게 되고 GSON에서 Contributor 클래스의 구조에 맞게 디코딩한 다음 UI 스레드를 이용해 화면에 업데이트합니다.

* startRetrofit() 메서드도 동일합니다. 하지만 getXXX() 메서드의 실행을 위해서는 retrofit에서 제공하는 Call 인터페이스를 사용합니다.
* Call 인터페이스의 enqueue() 메서드에 콜백을 등록하면 GSON에서 디코딩한 결과를 화면에 업데이트할 수 있습니다.
* 참고로 안드로이드에서 Retrofit의 콜백은 UI 스래ㅔ드에서 실행합니다. 만약 처리 시간이 오래 걸리는 작업이 필요하다면 새로운 스레드를 생성해서 실행해야 합니다.