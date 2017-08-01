package cma.redditrising.network;

import java.io.IOException;
import java.util.Map;

import cma.redditrising.util.RLog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class NetworkUtil {

    private OkHttpClient okHttpClient = getOkHttpClient();

    protected String post( String url, Map<String, String> bodyParams ) throws IOException {
        FormBody.Builder formBody = new FormBody.Builder();
        for ( String key : bodyParams.keySet() ) {
            formBody.add( key, bodyParams.get( key ) );
        }
        RequestBody body = formBody.build();

        Request request = getRequestBuilder()
                .url( url )
                .post( body )
                .build();

        Response response = okHttpClient.newCall( request ).execute();
        return response.body().string();
    }

    protected String get( String url ) throws IOException {
        Request request = getRequestBuilder()
                .url( url )
                .get()
                .build();

        RLog.d( request.toString() );
        Response response = okHttpClient.newCall( request ).execute();
        return response.body().string();
    }

    public abstract OkHttpClient getOkHttpClient();

    private Request.Builder getRequestBuilder() {
        return new Request.Builder()
                .addHeader( "User-Agent", "RedditRising/0.1 by cma1681" )
                .addHeader( "Authorization", "bearer " + getToken() );
    }

    protected abstract String getToken();
}
