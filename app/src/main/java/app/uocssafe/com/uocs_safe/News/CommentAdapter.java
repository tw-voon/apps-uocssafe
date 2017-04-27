package app.uocssafe.com.uocs_safe.News;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.List;

import app.uocssafe.com.uocs_safe.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private List<CommentModel> comments;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, message, time;
        public CircleImageView profile;
        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            message = (TextView) itemView.findViewById(R.id.commentmsg);
            time = (TextView) itemView.findViewById(R.id.time);
            profile = (CircleImageView) itemView.findViewById(R.id.profile_image);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
        }
    }

    CommentAdapter(Context context, List<CommentModel> comments){
        this.comments = comments;
        this.context = context;
    }

    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.MyViewHolder holder, int position) {

        CommentModel comment = comments.get(position);
        if(comment.getAvatar_link().equals(""))
            holder.profile.setImageResource(R.drawable.ic_person_outline_black_24dp);
        else
            Picasso.with(context).load(comment.getAvatar_link()).into(holder.profile);
        holder.username.setText(comment.getUsername());
        holder.message.setText(comment.getCommentmsg());
        holder.time.setText(comment.getTimeago());

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


}
