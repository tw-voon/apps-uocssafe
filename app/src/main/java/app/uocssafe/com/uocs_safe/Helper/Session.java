package app.uocssafe.com.uocs_safe.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;

public class Session {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context ctx;
    private AppConfig config;

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";

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

    public void setGuess(boolean guestmode){
        editor.putBoolean("guestmode",guestmode);
        editor.commit();
    }

    public boolean isGuest(){
        return prefs.getBoolean("guestmode", false);
    }

    public void setPermission(String key, boolean permission){
        editor.putBoolean(key,permission);
        editor.commit();
    }

    public boolean getPermission(String key){return prefs.getBoolean(key, false);}

    public void putData(String userID, String username, String avatar){

        editor.putString("userID", userID);
        editor.putString("username", username);
        editor.putString("avatar_link", avatar);
        editor.commit();

    }

    public void putFirebaseID(String firebaseID){
        editor.putString("firebaseID", firebaseID);
        editor.commit();
    }

    public void putUserAvatar(String encodeImage){
        editor.putString("AVATAR", encodeImage);
        editor.commit();
    }

    public String getUserAvatar() { return prefs.getString("AVATAR", null); }

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

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return prefs.getString(KEY_NOTIFICATIONS, null);
    }

    public void clearPreference()
    {
        prefs.edit().clear().commit();
    }

}
