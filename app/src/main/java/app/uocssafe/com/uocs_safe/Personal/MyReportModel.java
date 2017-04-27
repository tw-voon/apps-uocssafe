package app.uocssafe.com.uocs_safe.Personal;

public class MyReportModel {
    private String timestamp, username, avatar, title, reason, action_taken, post_id;
    private String status;

    MyReportModel(
            String username, String avatar, String timestamp, String title, String reason,
            String action_taken, String post_id, String status){
        this.username = username;
        this.timestamp = timestamp;
        this.title = title;
        this.avatar = avatar;
        this.reason = reason;
        this.action_taken = action_taken;
        this.post_id = post_id;
        this.status = status;
    }

    public String getTimestamp(){return timestamp;}
    public String getUsername(){return username;}
    public String getAvatar(){return avatar;}
    public String getTitle(){return title;}
    public String getReason(){return reason;}
    public String getAction_taken(){return action_taken;}
    public String getPost_id(){return post_id;}
    public String getStatus(){return status;}
}
