package app.uocssafe.com.uocs_safe.News;

public class News {

    private String username, title, description, imageLink, newsID, timestamp, location_name, avatar_link;

    public News(
            String username, String title, String description, String imageLink, String newsID,
            String timestamp, String location_name, String avatar_link
    ) {
        this.username = username;
        this.title = title;
        this.description = description;
        this.imageLink = imageLink;
        this.newsID = newsID;
        this.timestamp = timestamp;
        this.location_name = location_name;
        this.avatar_link = avatar_link;
    }

    public String getUsername(){
        return username;
    }

    public String getReportTitle(){
        return  title;
    }

    public String getNewsDescription(){
        return description;
    }

    public String getTimestamp() { return timestamp; }

    public String getImageLink(){
        return imageLink;
    }

    public String getNewsID() { return newsID; }

    public String getLocation_name(){ return location_name;}

    public String getAvatar_link(){return avatar_link;}

    public void setUsername(String username){
        this.username = username;
    }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public void setNewsTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setImageLink(String imageLink){
        this.imageLink = imageLink;
    }

    public void setNewsID(String newsID){ this.newsID = newsID; }

    public void setLocation_name(String location_name){this.location_name = location_name;}

}
