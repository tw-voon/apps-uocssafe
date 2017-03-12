package app.uocssafe.com.uocs_safe.News;

/**
 * Created by VoonTw on 24-Feb-17.
 */

public class News {

    private String username, title, description, imageLink;

    public String getUsername(){
        return username;
    }

    public String getReportTitle(){
        return  title;
    }

    public String getNewsDescription(){
        return description;
    }

    public String getImageLink(){
        return imageLink;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setNewsTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setImageLink(String imageLink){
        this.imageLink = imageLink;
    }

}
