package app.uocssafe.com.uocs_safe.Helper;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

public class AppConfig {

    private static String URL_DOMAIN = "http://192.168.1.103/uocs-safe/public/";
    public static String URL_LOGIN = URL_DOMAIN + "api/login";
    public static String URL_Register = URL_DOMAIN + "api/register";
    public static String URL_EContacts = URL_DOMAIN + "api/emergency_contact";
    public static String URL_ReportType = URL_DOMAIN + "api/report_type";
    public static String URL_ReportPost = URL_DOMAIN + "api/report_post";
    public static String URL_GetReport = URL_DOMAIN + "api/get_report";
    public static String URL_GetSingleReport = URL_DOMAIN + "api/get_single_report";
    public static String URL_RegisterFirebaseKey = URL_DOMAIN + "api/registerUserKey";
    public static String URL_SearchUser = URL_DOMAIN + "api/searchUser";
    public static String URL_GetComment = URL_DOMAIN + "api/getComment";
    public static String URL_AddComment = URL_DOMAIN + "api/addComment";
    public static String URL_TipsCategory = URL_DOMAIN + "api/tips_categories";
    public static String URL_DetailTips = URL_DOMAIN + "api/get_details_tip";
    public static String URL_AddAvatar = URL_DOMAIN + "api/add_avatar";
    public static String URL_SEARCH_User = URL_DOMAIN + "api/search_user";
    public static String URL_ADD_CHAT_USER = URL_DOMAIN + "api/add_chat_user";
    public static String PREF_NAME = "uocs-safe";

    public static String CHAT_ROOM = URL_DOMAIN + "api/fetchChatRoom";
    public static String ADD_MESSAGE = URL_DOMAIN + "api/addMessage";
    public static String FETCH_SINGLE_CHAT = URL_DOMAIN + "api/fetchSingleChatRoom";

    // flag to identify whether to show single line
    // or multi line text in push notification tray
    public static boolean appendNotificationMessages = true;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;

    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public String getStaticMap(String lat, String lon){
        return "http://maps.google.com/maps/api/staticmap?center="
                + lat + "," + lon+"&markers=icon:http://tinyurl.com/2ftvtt6%7C"+ lat +"," + lon
                +"&zoom=16&size=400x400&sensor=false";
    }

        public void changeStatusBarColor(int color, Activity activity){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(activity.getResources().getColor(color));
            }

    }


}
