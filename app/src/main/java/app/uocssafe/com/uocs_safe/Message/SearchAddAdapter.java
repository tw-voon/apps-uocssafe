package app.uocssafe.com.uocs_safe.Message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.Message.Models.NewUser;
import app.uocssafe.com.uocs_safe.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Shi Chee on 17-Apr-17.
 */

public class SearchAddAdapter extends RecyclerView.Adapter<SearchAddAdapter.MyViewHolder> {

    private ArrayList<NewUser> newUsers = new ArrayList<>();
    public Context context;
    Session session;
    private AdapterCallback mAdapterCallback;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, previewMessage, count, timestamp;
        CircleImageView userProfile;
        CheckBox selected;
        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.chatUserName);
            previewMessage = (TextView) itemView.findViewById(R.id.previewContentMessage);
            userProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);
            selected = (CheckBox) itemView.findViewById(R.id.selected);
            count = (TextView) itemView.findViewById(R.id.count);
            count.setVisibility(View.GONE);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            timestamp.setVisibility(View.GONE);
        }
    }

    SearchAddAdapter(Context context,  ArrayList<NewUser> newUsers){
        this.context = context;
        this.newUsers = newUsers;
        try {
            this.mAdapterCallback = ((AdapterCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    @Override
    public SearchAddAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final int current = position;
        final NewUser user = newUsers.get(position);
        holder.username.setText(user.getName());
        holder.selected.setOnCheckedChangeListener(null);
        holder.selected.setChecked(user.getSelected());
        holder.previewMessage.setText("");

        holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean is_check) {
                user.setSelected(is_check);
                mAdapterCallback.onUserClick(user.getId(), user.getName(), current, user.getSelected());
            }
        });

        if(!user.getAvatarLink().equals("null"))
            Picasso.with(context).load(user.getAvatarLink()).fit().into(holder.userProfile);
        else
            holder.userProfile.setImageResource(R.drawable.ic_person_outline_black_24dp);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!user.getSelected())
                    user.setSelected(true);
                else
                    user.setSelected(false);

                holder.selected.setChecked(user.getSelected());
                mAdapterCallback.onUserClick(user.getId(), user.getName(), current, user.getSelected());
            }
        });
    }

    @Override
    public int getItemCount() {
        return newUsers.size();
    }

    public static interface AdapterCallback {
        void onUserClick(String target_user_id, String name, int position, Boolean is_checked);
    }
}
