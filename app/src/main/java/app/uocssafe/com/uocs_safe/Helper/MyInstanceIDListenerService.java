package app.uocssafe.com.uocs_safe.Helper;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */

    Session session;

    @Override
    public void onTokenRefresh(){
        super.onTokenRefresh();
        Log.d("TAG--line14", "On Token Refresh");
        session = new Session(getApplicationContext());
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        session.putFirebaseID(refreshedToken);
        Log.d("token42: ", refreshedToken + " ");
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String token){

        session = new Session(getApplicationContext());
        final String userID = session.getUserID();
        Log.d("token: ", token + " ");

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, AppConfig.URL_RegisterFirebaseKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Successfully created key" + response, Toast.LENGTH_SHORT).show();
                        Intent registrationComplete = new Intent(AppConfig.SENT_TOKEN_TO_SERVER);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(registrationComplete);
                        session.putFirebaseID(token);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"line68: " + error.toString() + error.getCause(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("token",session.getFirebaseID());
                map.put("userID", userID);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
