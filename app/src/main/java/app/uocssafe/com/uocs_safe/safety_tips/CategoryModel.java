package app.uocssafe.com.uocs_safe.safety_tips;

public class CategoryModel {
    private String categoryName;
    private int category_id;

    public String getCategoryName(){ return categoryName; }

    public int getCategory_id() { return category_id; }

    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public void setCategory_id(int category_id) { this.category_id = category_id; }
}
