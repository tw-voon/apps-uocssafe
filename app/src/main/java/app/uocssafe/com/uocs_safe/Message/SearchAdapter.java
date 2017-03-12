package app.uocssafe.com.uocs_safe.Message;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Circle;

import app.uocssafe.com.uocs_safe.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    public Context context;
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView userAvatar;
        TextView username;
        public MyViewHolder(View itemView) {
            super(itemView);
            userAvatar = (CircleImageView)itemView.findViewById(R.id.profile_image);
            username = (TextView) itemView.findViewById(R.id.foundUser);
        }
    }
}
