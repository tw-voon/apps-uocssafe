package app.uocssafe.com.uocs_safe.Report_Activity;

import android.content.Context;

public class Category {

    private String categoryName;
    private int categoryIcon;
    private String categoryID;

    public Category (String categoryName, int categoryIcon, String categoryID){
        this.categoryName = categoryName;
        this.categoryIcon = categoryIcon;
        this.categoryID = categoryID;
    }

    public  Category (){

    }

    public String getCategoryName(){
        return categoryName;
    }

    public int getCategoryIcon(){
        return categoryIcon;
    }

    public String getCategoryID(){ return categoryID;}

    public void setCategoryID(String categoryID){ this.categoryID = categoryID; }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public void setCategoryIcon(int categoryIcon){
        this.categoryIcon = categoryIcon;
    }
}
