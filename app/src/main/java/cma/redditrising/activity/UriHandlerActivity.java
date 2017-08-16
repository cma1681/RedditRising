package cma.redditrising.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cma.redditrising.R;
import cma.redditrising.network.RedditNetworkUtil;
import cma.redditrising.object.RedditObject;
import cma.redditrising.util.RLog;

public class UriHandlerActivity extends Activity {

    private TextView textView;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.test_layout );
        textView = findViewById( R.id.test_layout_text );

        Intent intent = getIntent();
        if ( Intent.ACTION_VIEW.equals( intent.getAction() ) ) {
            handleDeeplink( intent );
        }
    }

    private void handleDeeplink( Intent intent ) {
        Uri uri = intent.getData();
        final String code = uri.getQueryParameter( "code" );

        try {
            RedditNetworkUtil redditNetworkUtil = RedditNetworkUtil.getInstance();
            redditNetworkUtil.getRedditAuthToken( code ).subscribe( s -> {
                JSONObject json = new JSONObject( s );
                String token = json.getString( "access_token" );
                redditNetworkUtil.setToken( token );

                redditNetworkUtil.getSubscribedSubreddits().subscribe( subs -> {
                    Gson gson = new Gson();
                    RedditObject redditObject = gson.fromJson( subs, RedditObject.class );
                    final List<RedditObject> children = redditObject.getData().getChildren();

                    StringBuilder sb = new StringBuilder();
                    for ( RedditObject child : children ) {
                        sb.append( child.getData().getDisplayName() );
                        sb.append( "\n" );
                    }
                    textView.setText( sb.toString() );

                    textView.setOnClickListener( v -> goToSubreddit( children.get( 0 ).getData().getDisplayName() ) );
                } );
            } );
        } catch ( Exception e ) {
            RLog.e( e );
        }
    }

    private void goToSubreddit( final String subreddit ) {
        try {
            RedditNetworkUtil redditNetworkUtil = RedditNetworkUtil.getInstance();
            redditNetworkUtil.getRisingPosts( subreddit ).subscribe( s -> {
                RLog.d( s );
            } );
        } catch ( IOException e ) {
            RLog.e( e );
        }
    }
}
