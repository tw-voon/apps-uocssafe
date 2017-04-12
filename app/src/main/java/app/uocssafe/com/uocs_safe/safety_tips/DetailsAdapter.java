package app.uocssafe.com.uocs_safe.safety_tips;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.uocssafe.com.uocs_safe.R;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.MyViewHolder> {

    List<DetailModel> details;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tip_name, tip_desc;
        public MyViewHolder(View itemView) {
            super(itemView);
            tip_name = (TextView) itemView.findViewById(R.id.tips_name);
            tip_desc = (TextView) itemView.findViewById(R.id.tips_desc);
        }
    }

    DetailsAdapter(List<DetailModel> details){
        this.details = details;
    }

    @Override
    public DetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detaillists, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DetailsAdapter.MyViewHolder holder, int position) {

        DetailModel detail = details.get(position);
        holder.tip_name.setText(detail.getTips_name());
        holder.tip_desc.setText(detail.getTips_desc());

    }

    @Override
    public int getItemCount() {
        return details.size();
    }


}
