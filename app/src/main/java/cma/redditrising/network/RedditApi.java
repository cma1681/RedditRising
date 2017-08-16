package cma.redditrising.network;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RedditApi {

    @Headers( {
            "User-Agent: RedditRising/0.1 by cma1681"
    } )


    @POST( "access_token" )
    Observable<String> getAccessToken( @Header( "authorization" ) String auth,
                                       @Query( "grant_type" ) String grantType,
                                       @Query( "code" ) String code,
                                       @Query( "redirect_uri" ) String redirectUri );

    @GET( "https://oauth.reddit.com/subreddits/mine/subscriber/" )
    Observable<String> loadSubscribedSubreddits( @Header( "authorization" ) String auth );

    @GET( "https://oauth.reddit.com/r/{subreddit}/rising" )
    Observable<String> getRisingPosts( @Path( "subreddit" ) String subreddit, @Header( "authorization" ) String auth );
}
