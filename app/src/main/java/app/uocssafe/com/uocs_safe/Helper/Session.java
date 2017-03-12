package app.uocssafe.com.uocs_safe.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;

public class Session {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context ctx;
    private AppConfig config;

    public Session(Context ctx){
        this.ctx = ctx;
        config = new AppConfig();
        prefs = ctx.getSharedPreferences(config.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedin(boolean logggedin){
        editor.putBoolean("loggedInmode",logggedin);
        editor.commit();
    }

    public void putData(String userID, String username){

        editor.putString("userID", userID);
        editor.putString("username", username);
        editor.commit();

    }

    public void putFirebaseID(String firebaseID){
        editor.putString("firebaseID", firebaseID);
        editor.commit();
    }

    public String getFirebaseID(){
        return prefs.getString("firebaseID", null);
    }

    public boolean loggedin(){
            return prefs.getBoolean("loggedInmode", false);
        }

    public String getUsername(){
        return prefs.getString("username", null);
    }

    public String getUserID(){
        return prefs.getString("userID", null);
    }

    public void clearPreference()
    {
        prefs.edit().clear().commit();
    }

}
