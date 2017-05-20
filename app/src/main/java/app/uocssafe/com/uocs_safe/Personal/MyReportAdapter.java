package app.uocssafe.com.uocs_safe.Personal;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.News.SinglePost;
import app.uocssafe.com.uocs_safe.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static app.uocssafe.com.uocs_safe.BaseActivity.decodeBase64;

public class MyReportAdapter extends RecyclerView.Adapter<MyReportAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<MyReportModel> data = new ArrayList<>();
    private Session session;
    private BottomSheetCallback bottomSheetCallback;

    public MyReportAdapter(Context context, ArrayList<MyReportModel> data) {
        this.context = context;
        this.data=data;
        session = new Session(context);
//        this.bottomSheetCallback = bottomSheetCallback;
        try {
            this.bottomSheetCallback = ((BottomSheetCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement BottomSheetCallback.");
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, timestamp, title;
        public ImageView imgStatus, imgMoreOption;
        public CircleImageView profile_image;
        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            timestamp = (TextView)itemView.findViewById(R.id.timestamp);
            title = (TextView) itemView.findViewById(R.id.title);
            imgStatus = (ImageView) itemView.findViewById(R.id.status);
            imgMoreOption = (ImageView) itemView.findViewById(R.id.more_option);
            profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
        }

    }

    @Override
    public MyReportAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.my_report_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyReportAdapter.MyViewHolder holder, int position) {

        final MyReportModel model = data.get(position);

        if(session.getUserAvatar() == null)
            holder.profile_image.setImageResource(R.drawable.head_1);
        else
            Picasso.with(context).load(session.getUserAvatar()).into(holder.profile_image);

        holder.timestamp.setText(model.getTimestamp());
        holder.title.setText(model.getTitle());
        holder.username.setText(model.getUsername());

        if(model.getStatus().equals("Approved")){
            holder.imgStatus.setImageResource(R.drawable.status_approve);
        } else if(model.getStatus().equals("Pending")){
            holder.imgStatus.setImageResource(R.drawable.status_pending);
        } else if(model.getStatus().equals("Canceled")){
            holder.imgStatus.setImageResource(R.drawable.status_cancel);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), SinglePost.class);
                intent.putExtra("report_ID", model.getPost_id());
                context.startActivity(intent);
            }
        });

        holder.imgMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "clicked", Toast.LENGTH_SHORT).show();
                bottomSheetCallback.onUserClick(model.getStatus(), model.getReason(), model.getAction_taken());
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static interface BottomSheetCallback {
        void onUserClick(String status, String reason, String action_taken);
    }
}
