package app.uocssafe.com.uocs_safe.Message;

import java.lang.reflect.Array;

/**
 * Created by User on 7/3/2017.
 */

public class Message {
    public int chatRoom, image_resource;
    public Array userDetails;
    public String imageURL, message;

    public String username, previewContent;

    public void setImage_resource(int image_resource){
        this.image_resource = image_resource;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setPreviewContent(String previewContent){
        this.previewContent = previewContent;
    }

    public void setChatRoom (int chatRoom){
        this.chatRoom = chatRoom;
    }

    public void setUserDetails(Array userDetails){
        this.userDetails = userDetails;
    }

    public void setImageURL(String imageURL){
        this.imageURL = imageURL;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getImageURL(){
        return imageURL;
    }

    public String getMessage(){
        return message;
    }

    public Array getUserDetails(){
        return userDetails;
    }

    public int getChatRoom(){
        return chatRoom;
    }

    public int getImage_resource(){ return image_resource;}

    public String getUsername(){ return username; };

    public String getPreviewContent() { return previewContent; };
}
