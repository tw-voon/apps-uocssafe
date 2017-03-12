package app.uocssafe.com.uocs_safe.Emergency_Contact;

import android.widget.Button;

/**
 * Created by User on 6/2/2017.
 * Declare title, genre and year. Also add the getter/setter methods to each variable
 */

public class Contact {

    private String title, contact_no, desc;
    private Button buttonCall;

    public Contact(String title, String contact_no, String desc) {
        this.title = title;
        this.contact_no = contact_no;
        this.desc = desc;
    }

    public Contact(){

    }

    public String getTitle(){
        return  title;
    }

    public String getContact_no(){
        return  contact_no;
    }

    public String getDesc(){
        return  desc;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setContact_no(String contact_no){
        this.contact_no = contact_no;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }
}
