package app.uocssafe.com.uocs_safe.Personal;

public class MyActivityModel {
    public String report_id, action, timestamp, avatar_link;

    public MyActivityModel(String report_id, String action, String timestamp, String avatar_link){
        this.report_id = report_id;
        this.action = action;
        this.timestamp = timestamp;
        this.avatar_link= avatar_link;
    }

    public String getReport_id(){return report_id;}
    public String getAction(){return action;}
    public String getTimestamp(){return timestamp;}
    public String getAvatar_link(){return avatar_link;}
}
