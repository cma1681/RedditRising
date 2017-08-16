package cma.redditrising.network;

import java.io.IOException;
import java.net.URISyntaxException;

import cma.redditrising.BuildConfig;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Credentials;
import okhttp3.HttpUrl;

public class RedditNetworkUtil extends NetworkUtil {

    // TODO: Replace with client ID, stored privately
    private static final String REDDIT_CLIENT_SECRET = "";

    private static final String REDDIT_RESPONSE_TYPE = "code"; //Either "code" or "token", seems like "code" works
    private static final String REDIRECT_URL = "redditrising://launch";
    private static final String REDDIT_PERMISSIONS_SCOPE = "mysubreddits,read"; // CSV of scope
    private String token;

    // TODO Look into Dagger or some dependency injection
    private static RedditNetworkUtil INSTANCE = new RedditNetworkUtil();
    private RedditApi redditApi;

    public static RedditNetworkUtil getInstance() {
        return INSTANCE;
    }

    private RedditNetworkUtil() {
    }

    private RedditApi getRedditApi() {
        if ( redditApi == null ) {
            redditApi = getRetrofit().create( RedditApi.class );
        }
        return redditApi;
    }

    /**
     * Ask user to authorize reddit permissions
     *
     * @param state response when user gives permission will also have state. Verify they are the same for security
     * @return
     * @throws URISyntaxException
     */
    public String getRedditAuthUrl( String state ) throws URISyntaxException {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme( "https" )
                .host( "www.reddit.com" )
                .addPathSegment( "api/v1/authorize" )
                .addQueryParameter( "client_id", BuildConfig.REDDIT_CLIENT_ID )
                .addQueryParameter( "response_type", REDDIT_RESPONSE_TYPE )
                .addQueryParameter( "state", state )
                .addQueryParameter( "redirect_uri", REDIRECT_URL )
                .addQueryParameter( "scope", REDDIT_PERMISSIONS_SCOPE )
                .build();

        return httpUrl.url().toString();
    }

    public Observable<String> getRedditAuthToken( String code ) throws IOException { // TODO verify that state is the same uuid that was passed in
        String credential = Credentials.basic( BuildConfig.REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET );

        return getRedditApi().getAccessToken( credential, "authorization_code", code, REDIRECT_URL )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() );
    }


    public Observable<String> getSubscribedSubreddits() throws IOException {
        return getRedditApi().loadSubscribedSubreddits( "bearer " + getToken() )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() );
    }

    public Observable<String> getRisingPosts( String subreddit ) throws IOException {
        return getRedditApi().getRisingPosts( subreddit, "bearer " + getToken() )
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() );
    }

    public void setToken( String token ) {
        this.token = token;
    }

    @Override
    protected String getToken() {
        return token;
    }
}
