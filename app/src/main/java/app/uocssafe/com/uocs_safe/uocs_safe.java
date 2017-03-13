package app.uocssafe.com.uocs_safe;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;

import app.uocssafe.com.uocs_safe.Helper.Session;

/**
 * Created by User on 9/3/2017.
 */

public class uocs_safe extends Application{

    private static uocs_safe instances;
    private static Session pref;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static synchronized uocs_safe getInstance() {
        return instances;
    }

    public RequestQueue getmRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public Session getPrefManager() {
        if (pref == null) {
            pref = new Session(this);
        }

        return pref;
    }
}
