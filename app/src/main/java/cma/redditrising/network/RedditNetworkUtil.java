package cma.redditrising.network;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import cma.redditrising.BuildConfig;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class RedditNetworkUtil extends NetworkUtil {

    // TODO: Replace with client ID, stored privately
    private static final String REDDIT_CLIENT_SECRET = "";

    private static final String REDDIT_RESPONSE_TYPE = "code"; //Either "code" or "token", seems like "code" works
    private static final String REDDIT_BASE_URL = "https://www.reddit.com/api/v1/";
    private static final String REDDIT_OAUTH_BASE_URL = "https://oauth.reddit.com/";
    private static final String REDIRECT_URL = "redditrising://launch";
    private static final String REDDIT_PERMISSIONS_SCOPE = "mysubreddits,read"; // CSV of scope
    private String token;

    // TODO Look into Dagger or some dependency injection
    private static RedditNetworkUtil INSTANCE = new RedditNetworkUtil();

    public static RedditNetworkUtil getInstance() {
        return INSTANCE;
    }

    private RedditNetworkUtil() {
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

    public String getRedditAuthToken( String code ) throws IOException { // TODO verify that state is the same uuid that was passed in
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put( "grant_type", "authorization_code" );
        requestParams.put( "code", code );
        requestParams.put( "redirect_uri", REDIRECT_URL );
        return post( REDDIT_BASE_URL + "access_token", requestParams );
    }

    @Override
    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder().authenticator( new Authenticator() {
            @Override
            public Request authenticate( Route route, Response response ) throws IOException {
                String credential = Credentials.basic( BuildConfig.REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET );
                return response.request().newBuilder().header( "Authorization", credential ).build();
            }
        } ).build();
    }

    public String getSubscribedSubreddits() throws IOException {
        return get( REDDIT_OAUTH_BASE_URL + "subreddits/mine/subscriber" );
    }

    public String getRisingPosts( String subreddit ) throws IOException {
        String url = String.format( REDDIT_OAUTH_BASE_URL + "r/%s/rising", subreddit );
        return get( url );
    }

    public void setToken( String token ) {
        this.token = token;
    }

    @Override
    protected String getToken() {
        return token;
    }
}
