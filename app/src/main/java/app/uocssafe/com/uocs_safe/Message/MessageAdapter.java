package app.uocssafe.com.uocs_safe.Message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.uocssafe.com.uocs_safe.Emergency_Contact.contactAdapter;
import app.uocssafe.com.uocs_safe.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    ArrayList<Message> messageList = new ArrayList<>();
    public Context context;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, previewMessage;
        CircleImageView userProfile;

        MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.chatUserName);
            previewMessage = (TextView) itemView.findViewById(R.id.previewContentMessage);
            userProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);
        }
    }

    MessageAdapter(Context context, ArrayList<Message> messageList){
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageAdapter.MyViewHolder holder, int position) {
        final Message message = messageList.get(position);
        holder.username.setText(message.getUsername());
        holder.previewMessage.setText(message.getPreviewContent());
        holder.userProfile.setImageResource(message.getImage_resource());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You have clicked on" + message.username, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


}
