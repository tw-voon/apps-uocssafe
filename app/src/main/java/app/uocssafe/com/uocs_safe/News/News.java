package app.uocssafe.com.uocs_safe.News;

public class News {

    private String username, title, description, imageLink, newsID, timestamp;

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

}
