package app.uocssafe.com.uocs_safe;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by User on 9/3/2017.
 */

public class uocs_safe extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
