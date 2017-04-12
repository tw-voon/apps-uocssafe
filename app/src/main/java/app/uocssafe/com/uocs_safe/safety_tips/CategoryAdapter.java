package app.uocssafe.com.uocs_safe.safety_tips;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.uocssafe.com.uocs_safe.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<CategoryModel> category;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public MyViewHolder(View itemView) {
            super(itemView);
            categoryName = (TextView) itemView.findViewById(R.id.category_name);
        }
    }

    CategoryAdapter(List<CategoryModel> category){
        this.category = category;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tips_category_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final CategoryModel categories = category.get(position);
        holder.categoryName.setText(categories.getCategoryName());
        holder.categoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getDetails = new Intent(v.getContext(), DetailsTipsActivity.class);
                getDetails.putExtra("category_id", categories.getCategory_id());
                v.getContext().startActivity(getDetails);
            }
        });

    }

    @Override
    public int getItemCount() {
        return category.size();
    }


}
