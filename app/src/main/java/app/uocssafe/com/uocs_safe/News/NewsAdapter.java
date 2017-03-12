package app.uocssafe.com.uocs_safe.News;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private ArrayList<News> newslist = new ArrayList<>();
    public Context context;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView username, report_title, report_desc;
        ImageView report_image;
        Button comment;

        MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            report_title = (TextView) itemView.findViewById(R.id.report_title);
            report_desc = (TextView) itemView.findViewById(R.id.description);
            report_image = (ImageView) itemView.findViewById(R.id.imageView);
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
        holder.report_desc.setText(news.getNewsDescription());
        holder.report_title.setText(news.getReportTitle());
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Under constructing comment feature for"+ news.getReportTitle(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return newslist.size();
    }
}
