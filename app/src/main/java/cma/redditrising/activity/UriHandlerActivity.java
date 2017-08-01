package cma.redditrising.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

        new AsyncTask() {
            @Override
            protected String doInBackground( Object[] params ) {
                try {
                    RedditNetworkUtil redditNetworkUtil = RedditNetworkUtil.getInstance();
                    String tokenResponse = redditNetworkUtil.getRedditAuthToken( code );
                    JSONObject json = new JSONObject( tokenResponse );
                    String token = json.getString( "access_token" );
                    redditNetworkUtil.setToken( token );
                    return redditNetworkUtil.getSubscribedSubreddits();
                } catch ( Exception e ) {
                    RLog.e( e );
                }
                return null;
            }

            @Override
            protected void onPostExecute( Object o ) {
                super.onPostExecute( o );
                Gson gson = new Gson();
                RedditObject redditObject = gson.fromJson( (String) o, RedditObject.class );
                final List<RedditObject> children = redditObject.getData().getChildren();

                StringBuilder sb = new StringBuilder();
                for ( RedditObject child : children ) {
                    sb.append( child.getData().getDisplayName() );
                    sb.append( "\n" );
                }
                textView.setText( sb.toString() );

                textView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        goToSubreddit( children.get( 0 ).getData().getDisplayName() );
                    }
                } );
            }
        }.execute();
    }

    private void goToSubreddit( final String subreddit ) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground( String... strings ) {
                try {
                    RedditNetworkUtil redditNetworkUtil = RedditNetworkUtil.getInstance();
                    return redditNetworkUtil.getRisingPosts( subreddit );
                } catch ( IOException e ) {
                    RLog.e( e );
                }
                return null;
            }

            @Override
            protected void onPostExecute( String s ) {
                super.onPostExecute( s );
                RLog.d( s );
            }
        }.execute();
    }

}
