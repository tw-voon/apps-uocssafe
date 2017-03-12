package app.uocssafe.com.uocs_safe.Message;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.R;

public class MessageActivity extends AppCompatActivity {

    RecyclerView messageList;
    MessageAdapter messageAdapter;
    DatabaseReference userRef;
    Session session;
    ArrayList<Message> message = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new Session(this);
        userRef = FirebaseDatabase.getInstance().getReference("chat_room");

        Query selectRoom = userRef.child("users");

        selectRoom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Log.d("Value: ---", postSnapshot.child(session.getFirebaseID()).getValue(String.class)+"");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error: ---", databaseError.toString());
            }
        });

        create_result();
    }

    public void create_result(){
        for (int i = 0; i<10; i++){
            Message messages = new Message();
            messages.setUsername("User"+i);
            messages.setPreviewContent("Sample messages");
            messages.setImage_resource(R.drawable.ic_person_outline_black_24dp);
            message.add(messages);
        }
        Log.d("Result", message.toString());
        messageList = (RecyclerView) findViewById(R.id.message_recycler_view);
        messageAdapter = new MessageAdapter(MessageActivity.this, message);
        messageList.setAdapter(messageAdapter);
        messageList.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        MenuItem item = menu.findItem(R.id.addUser);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUser(query);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    public void searchUser(final String query){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SearchUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response----", response);
                        try {
                            JSONObject result = new JSONObject(response);
                            if(result.getString("status").equals("success")){
                                JSONObject dataObject = new JSONObject(result.getString("data"));
                                showMessage("User Found", "Name: "+ dataObject.getString("name"));
                            }
                            else showMessage("User not found", result.getString("data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MessageActivity.this,error.toString(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("name",query);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void Adduser(){
//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setCancelable(true);
//        alert.setTitle("Do you want to add this user");
//        alert.setMessage(content);
//        alert.show();
    }

    public void showMessage(String title, String content){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle(title);
        alert.setMessage(content);
        alert.show();
    }
}
