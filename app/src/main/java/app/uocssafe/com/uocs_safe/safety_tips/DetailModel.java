package app.uocssafe.com.uocs_safe.safety_tips;

/**
 * Created by Shi Chee on 07-Apr-17.
 */

public class DetailModel {
    public String tips_name, tips_desc;

    DetailModel(String tips_name, String tips_desc){
        this.tips_desc = tips_desc;
        this.tips_name = tips_name;
    }

    public String getTips_name(){return tips_name; }
    public String getTips_desc(){ return tips_desc; }
}
