package app.uocssafe.com.uocs_safe.Message.Models;

/**
 * Created by Shi Chee on 17-Apr-17.
 */

public class NewUser {
    public String id, name, avatarLink;

    public NewUser(String id, String name, String avatarLink){
        this.id = id;
        this.name = name;
        this.avatarLink = avatarLink;
    }

    public String getId(){return id;}
    public String getName(){return name;}
    public String getAvatarLink(){return avatarLink;}
}
