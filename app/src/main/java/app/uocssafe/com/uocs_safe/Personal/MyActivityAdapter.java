package app.uocssafe.com.uocs_safe.Personal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.News.SinglePost;
import app.uocssafe.com.uocs_safe.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyActivityAdapter extends RecyclerView.Adapter<MyActivityAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<MyActivityModel> data = new ArrayList<>();
    private Session session;

    public MyActivityAdapter(Context context, ArrayList<MyActivityModel> data){
        this.context = context;
        this.data = data;
        session = new Session(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView action_name, timestamp;
        CircleImageView profile_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            action_name = (TextView) itemView.findViewById(R.id.action_name);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }

    @Override
    public MyActivityAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.my_activity_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyActivityAdapter.MyViewHolder holder, int position) {

        final MyActivityModel model = data.get(position);
        holder.action_name.setText(Html.fromHtml(model.getAction()));
        holder.timestamp.setText(model.getTimestamp());

        if(model.getUser_id().equals(session.getUserID())) {
            if(session.getUserAvatar() == null || session.getUserAvatar().equals("null"))
                holder.profile_image.setImageResource(R.drawable.head_1);
            else
                Picasso.with(context).load(session.getUserAvatar()).into(holder.profile_image);
        }
        else {
            if(model.getAvatar_link().equals("") || model.getAvatar_link().equals("null"))
                holder.profile_image.setImageResource(R.drawable.head_2);
            else
                Picasso.with(context).load(model.getAvatar_link()).into(holder.profile_image);
        }

//        if(model.getAvatar_link().equals(""))
//            holder.profile_image.setImageResource(R.drawable.ic_person_outline_black_24dp);
//        else
//            Picasso.with(context).load(model.getAvatar_link()).into(holder.profile_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SinglePost.class);
                intent.putExtra("report_ID", model.getReport_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
