package app.uocssafe.com.uocs_safe.News;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.uocssafe.com.uocs_safe.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private ArrayList<News> newslist = new ArrayList<>();
    public Context context;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username, report_title, report_desc, timestamp, location_name;
        ImageView report_image;
        CircleImageView profile_image;
        Button comment;

        MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            report_title = (TextView) itemView.findViewById(R.id.report_title);
            report_desc = (TextView) itemView.findViewById(R.id.description);
            report_image = (ImageView) itemView.findViewById(R.id.imageView);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            location_name = (TextView) itemView.findViewById(R.id.location);
            profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            comment = (Button) itemView.findViewById(R.id.comment);
        }
    }

    public NewsAdapter(Context context, ArrayList<News> newsList){
        this.context = context;
        this.newslist = newsList;
    }
    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.MyViewHolder holder, int position) {

        final News news = newslist.get(position);
        holder.username.setText(news.getUsername());
        Log.d("Image Url ---", news.getImageLink());
        Picasso.with(context).load(news.getImageLink()).fit().into(holder.report_image);
        if(news.getAvatar_link().equals(""))
            holder.profile_image.setImageResource(R.drawable.ic_person_outline_black_24dp);
        else
            Picasso.with(context).load(news.getAvatar_link()).into(holder.profile_image);
        holder.report_desc.setText(news.getNewsDescription());
        holder.report_title.setText(news.getReportTitle());
        holder.timestamp.setText(news.getTimestamp());
        String location = String.format("at - <b>%s</b>", news.getLocation_name());
        holder.location_name.setText(Html.fromHtml(location));
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Under constructing comment feature for"+ news.getReportTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context.getApplicationContext(), SinglePost.class);
                intent.putExtra("report_ID", news.getNewsID());
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SinglePost.class);
                intent.putExtra("report_ID", news.getNewsID());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newslist.size();
    }
}
