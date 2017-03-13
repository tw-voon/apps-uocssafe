package app.uocssafe.com.uocs_safe.Helper;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Shi Chee on 13-Mar-17.
 */

public class MyInstanceIDListenerService extends InstanceIDListenerService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */

    @Override
    public void onTokenRefresh(){
        Log.d("TAG--line14", "On Token Refresh");
        Intent intent = new Intent (this, GcmIntentService.class);
        startService(intent);
    }
}
