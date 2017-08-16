package cma.redditrising.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public abstract class NetworkUtil {

    private String BASE_URL = "https://www.reddit.com/api/v1/";

    private Retrofit retrofit;

    public Retrofit getRetrofit() {
        if ( retrofit == null ) {
            retrofit = new Retrofit.Builder()
                    .baseUrl( BASE_URL )
                    .addConverterFactory( ScalarsConverterFactory.create() )
//                    .addConverterFactory( GsonConverterFactory.create() )
                    .addCallAdapterFactory( RxJava2CallAdapterFactory.create() )
                    .build();
        }
        return retrofit;
    }

    protected abstract String getToken();
}
