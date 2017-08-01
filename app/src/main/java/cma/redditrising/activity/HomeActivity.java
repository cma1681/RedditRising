package cma.redditrising.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.UUID;

import cma.redditrising.R;
import cma.redditrising.network.RedditNetworkUtil;
import cma.redditrising.util.RLog;

public class HomeActivity extends AppCompatActivity {

    private View loginButton;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        loginButton = findViewById( R.id.home_login );
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                try {
                    RedditNetworkUtil networkUtil = RedditNetworkUtil.getInstance();
                    String redditAuthUrl = networkUtil.getRedditAuthUrl( UUID.randomUUID().toString() );

                    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( redditAuthUrl ) );
                    startActivity( intent );
                } catch ( Exception e ) {
                    Toast.makeText( getBaseContext(), R.string.failed_login_click, Toast.LENGTH_SHORT );
                    RLog.e( e, "Failed to handle log-in click" );
                }
            }
        } );
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginButton.setOnClickListener( null );
    }
}
