package app.uocssafe.com.uocs_safe.News;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.uocssafe.com.uocs_safe.BaseActivity;
import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.Helper.StatusBottomSheetDialogFragment;
import app.uocssafe.com.uocs_safe.Helper.internet_helper;
import app.uocssafe.com.uocs_safe.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class SinglePost extends BaseActivity{

    private List<CommentModel> comments;
    private RecyclerView commentlist;
    private CommentAdapter commentAdapter;
    private String status, reason, action;
    internet_helper hasInternet;
    TextView desc, location, title, username, no_comment, timestamp;
    Button btnsendComment;
    EditText edtcomment;
    ImageView pic, extraoption;
    CircleImageView user_profile;
    Session session;
    RelativeLayout inforpart, commentsection;
    LinearLayout sendComment;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_single_post, contentFrameLayout);
        comments = new ArrayList<>();
        commentlist = (RecyclerView) findViewById(R.id.comment_list_view);
        final String report_ID = getIntent().getStringExtra("report_ID");
        session = new Session(this);

        desc = (TextView) findViewById(R.id.post_description);
        location = (TextView) findViewById(R.id.locationName);
        title = (TextView) findViewById(R.id.stickyView);
        username = (TextView) findViewById(R.id.username);
        user_profile =(CircleImageView) findViewById(R.id.profile_image);
        pic = (ImageView) findViewById(R.id.heroImageView);
        extraoption = (ImageView) findViewById(R.id.extraoption);
        extraoption.setVisibility(View.VISIBLE);
        no_comment = (TextView) findViewById(R.id.no_comment);
        btnsendComment = (Button) findViewById(R.id.btn_send);
        edtcomment = (EditText) findViewById(R.id.message);
        timestamp = (TextView) findViewById(R.id.timestamp);
        commentsection = (RelativeLayout) findViewById(R.id.commentsection);
        inforpart = (RelativeLayout) findViewById(R.id.info_part);
        sendComment = (LinearLayout) findViewById(R.id.sendComment);
        loading = (ProgressBar) findViewById(R.id.loading);

        btnsendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = edtcomment.getText().toString().trim();
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
                } else
                addComment(report_ID, msg);
            }
        });

        extraoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusBottomSheetDialogFragment bottomSheetDialogFragment = new StatusBottomSheetDialogFragment();
                bottomSheetDialogFragment.setData(status, reason, action);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");
            }
        });

        commentAdapter = new CommentAdapter(SinglePost.this, comments);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        commentlist.setLayoutManager(layoutManager);
        commentlist.setItemAnimator(new DefaultItemAnimator());
        commentlist.setAdapter(commentAdapter);
        commentlist.setFocusable(false);
        commentlist.setNestedScrollingEnabled(false);

        if(internet_helper.isNetworkStatusAvialable(SinglePost.this) && report_ID != null){
            getSinglePost(report_ID);
        }
    }

    private void addComment(final String report_id, final String message){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_AddComment,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Status: ", response);
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(response);
                            JSONObject result = jsonArray.getJSONObject(0);

                            CommentModel comment = new CommentModel(
                                    result.getString("name"),
                                    result.getString("comment_msg"),
                                    result.getString("created_at"),
                                    result.getInt("user_id"),
                                    result.getInt("id"),
                                    result.getString("avatar_link")
                            );
                            comments.add(comment);

                            commentAdapter.notifyDataSetChanged();
                            no_comment.setVisibility(View.GONE);
                            edtcomment.setText("");
                            commentlist.getLayoutManager().smoothScrollToPosition(commentlist, null, commentAdapter.getItemCount() - 1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SinglePost.this, "An Error occur, Please try again later " + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("report_id",report_id);
                map.put("user_id", session.getUserID());
                map.put("comment", message);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void getSinglePost(final String report_ID){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GetSingleReport,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Status: ", response);
                        processPost(response);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SinglePost.this, "An Error occur, Please try again later " + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("report_id",report_ID);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void getComment(final String report_ID){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GetComment,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Status: ", response);
                        if(!response.equals("Fails"));
                            processComment(response);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SinglePost.this, "An Error occur, Please try again later " + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("report_id",report_ID);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void processComment(String response){

        try {
            JSONArray jsonArray = new JSONArray(response);

            if(jsonArray.length() != 0){

                for(int i = 0; i < jsonArray.length(); i++){

                    JSONObject result = jsonArray.getJSONObject(i);
                    CommentModel comment = new CommentModel(
                            result.getString("name"),
                            result.getString("comment_msg"),
                            result.getString("created_at"),
                            result.getInt("user_id"),
                            result.getInt("id"),
                            result.getString("avatar_link")
                    );
                    comments.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
                no_comment.setVisibility(View.GONE);

            } else {
                Toast.makeText(SinglePost.this, "No Comment in this post", Toast.LENGTH_SHORT).show();
                no_comment.setVisibility(View.VISIBLE);
                LinearLayout sendComment = (LinearLayout) findViewById(R.id.sendComment);
//                sendComment.set
            }

            commentsection.setVisibility(View.VISIBLE);
            sendComment.setVisibility(View.VISIBLE);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        loading.setVisibility(View.GONE);
    }

    private void processPost(String response) {
        Log.d("single", response);
        try{
            JSONArray jsonArray = new JSONArray(response);
            JSONObject result = jsonArray.getJSONObject(0);

            desc.setText(result.getString("report_Description"));
            String locate = String.format("at - %s", result.getString("location_name"));
            location.setText(locate);
            username.setText(result.getString("name"));
            title.setText(result.getString("report_Title"));
            if(result.getString("avatar_link").equals("null"))
                user_profile.setImageResource(R.drawable.head_1);
            else
                Picasso.with(SinglePost.this).load(result.getString("avatar_link")).into(user_profile);
            Picasso.with(SinglePost.this).load(result.getString("image")).into(pic);
            timestamp.setText(result.getString("created_at"));

            switch (result.getString("status_id")){
                case "1":
                    status = "Approved";
                    break;

                case "2":
                    status = "Canceled";
                    break;

                case "3":
                    status = "Pending";
                    break;
            }

            reason = result.getString("reason");
            action = result.getString("action_taken");

            inforpart.setVisibility(View.VISIBLE);
            if(result.getString("approve_status").equals("1")) {
                getComment(result.getString("ids"));
            }
            else{
                commentsection.setVisibility(View.GONE);
                sendComment.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

