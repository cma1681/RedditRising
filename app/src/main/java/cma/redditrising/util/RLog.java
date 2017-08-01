package cma.redditrising.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RLog {

    private static final String LOG_TAG = "redditrising";
    private static final int MAX_CHARS_OUTPUT = 4000;

    public static void d( String str ) {
        for ( String s : getSubStrings( str ) ) {
            Log.d( LOG_TAG, s );
        }
    }

    public static void e( String str ) {
        for ( String s : getSubStrings( str ) ) {
            Log.e( LOG_TAG, s );
        }
    }

    public static void e( Throwable e ) {
        e( e, "" );
    }

    public static void e( Throwable e, String str ) {
        for ( String s : getSubStrings( str ) ) {
            Log.e( LOG_TAG, s, e );
        }
    }

    private static List<String> getSubStrings( String orig ) {
        List<String> out = new ArrayList<>();
        while ( orig.length() > MAX_CHARS_OUTPUT ) {
            out.add( orig.substring( 0, MAX_CHARS_OUTPUT ) );
            orig = orig.substring( MAX_CHARS_OUTPUT, orig.length() );
        }
        out.add( orig );
        return out;
    }
}
