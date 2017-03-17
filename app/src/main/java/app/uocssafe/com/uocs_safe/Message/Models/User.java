package app.uocssafe.com.uocs_safe.Message.Models;

import java.io.Serializable;

/**
 * Created by Shi Chee on 16-Mar-17.
 */

public class User implements Serializable{
    private String id, name;

    public User(){}
    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id){this.id = id;}
    public void setName(String name){this.name = name;}
    public String getId(){return id;}
    public String getName(){return name;}
}
