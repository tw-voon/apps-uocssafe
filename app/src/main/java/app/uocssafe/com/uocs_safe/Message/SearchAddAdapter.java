package app.uocssafe.com.uocs_safe.Message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, previewMessage;
        CircleImageView userProfile;
        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.chatUserName);
            previewMessage = (TextView) itemView.findViewById(R.id.previewContentMessage);
            userProfile = (CircleImageView) itemView.findViewById(R.id.profile_image);
        }
    }

    SearchAddAdapter(Context context,  ArrayList<NewUser> newUsers){
        this.context = context;
        this.newUsers = newUsers;
    }

    @Override
    public SearchAddAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final NewUser user = newUsers.get(position);
        holder.username.setText(user.getName());
        Log.d("123", user.getAvatarLink().length() + "");
        if(user.getAvatarLink().length() != 0)
            Picasso.with(context).load(user.getAvatarLink()).fit().into(holder.userProfile);
        else
            holder.userProfile.setImageResource(R.drawable.ic_person_outline_black_24dp);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "you have clicked " + user.getName(), Toast.LENGTH_SHORT).show();
                addUser(user.getId());
            }
        });
    }


    private void addUser(final String id) {
        session = new Session(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_CHAT_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("status", response);
                        Toast.makeText(context, "Successfully added", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context, "An Error occur, Please try again later", Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("target_user_id",String.valueOf(id));
                map.put("user_id", session.getUserID());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return newUsers.size();
    }
}
