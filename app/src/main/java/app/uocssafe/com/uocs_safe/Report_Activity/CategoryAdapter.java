package app.uocssafe.com.uocs_safe.Report_Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import app.uocssafe.com.uocs_safe.R;

public class CategoryAdapter extends ArrayAdapter {

    private Context context;
    ArrayList<Category> category = new ArrayList<>();

    public  CategoryAdapter (Context context, int resourceViewID, ArrayList<Category> category){
        super(context, resourceViewID, category);
        this.context = context;
        this.category = category;
    }

    @Override
    public int getCount() {
        return category.size();
    }

    @Override
    public Object getItem(int i) {
        return category.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView = convertView;

        if(gridView == null){

            gridView = new View(context);
            gridView = inflater.inflate(R.layout.single_category, null);
            TextView category_name = (TextView) gridView.findViewById(R.id.grid_text);
            ImageView category_image = (ImageView) gridView.findViewById(R.id.grid_image);
            category_name.setText(category.get(position).getCategoryName());
            category_image.setImageResource(category.get(position).getCategoryIcon());

        }

        return gridView;

    }
}
