package app.uocssafe.com.uocs_safe.Message.Models;

import java.io.Serializable;

/**
 * Created by Shi Chee on 16-Mar-17.
 */

public class Messages implements Serializable {
    String id, message, created_at;
    User user;

    public Messages(String id, String message, String created_at, User user) {
        this.id = id;
        this.message = message;
        this.created_at = created_at;
        this.user = user;
    }

    public Messages(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
