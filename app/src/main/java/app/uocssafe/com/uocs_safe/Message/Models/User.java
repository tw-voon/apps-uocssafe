package app.uocssafe.com.uocs_safe.Message.Models;

import java.io.Serializable;

public class User implements Serializable{
    private String id, name;
    private String avatarLink, last_message;

    public User(){}
    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id){this.id = id;}
    public void setName(String name){this.name = name;}
    public void setAvatarLink(String link) { this.avatarLink = link;}
    public void setLast_message(String last_message){this.last_message = last_message;}
    public String getId(){return id;}
    public String getName(){return name;}
    public String getAvatarLink(){return avatarLink;}
    public String getLast_message(){return last_message;}
}
