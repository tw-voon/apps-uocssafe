package app.uocssafe.com.uocs_safe.Helper;

public class AppConfig {

    private static String URL_DOMAIN = "http://10.64.107.186/uocs-safe/public/";
    public static String URL_LOGIN = URL_DOMAIN + "api/login";
    public static String URL_Register = URL_DOMAIN + "api/register";
    public static String URL_EContacts = URL_DOMAIN + "api/emergency_contact";
    public static String URL_ReportType = URL_DOMAIN + "api/report_type";
    public static String URL_ReportPost = URL_DOMAIN + "api/report_post";
    public static String URL_GetReport = URL_DOMAIN + "api/get_report";
    public static String URL_RegisterFirebaseKey = URL_DOMAIN + "api/registerUserKey";
    public static String URL_RegisterGCMKey = URL_DOMAIN + "api/registerUserKey";
    public static String URL_SearchUser = URL_DOMAIN + "api/searchUser";
    public static String PREF_NAME = "uocs-safe";

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

}
