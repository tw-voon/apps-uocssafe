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
    public static String URL_SearchUser = URL_DOMAIN + "api/searchUser";
    public static String PREF_NAME = "uocs-safe";

}
