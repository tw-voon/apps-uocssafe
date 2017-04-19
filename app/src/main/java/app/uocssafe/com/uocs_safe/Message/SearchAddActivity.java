package app.uocssafe.com.uocs_safe.Message;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.doodle.android.chips.ChipsView;

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
import app.uocssafe.com.uocs_safe.Message.Models.NewUser;
import app.uocssafe.com.uocs_safe.R;

public class SearchAddActivity extends BaseActivity
        implements SearchView.OnQueryTextListener, SearchAddAdapter.AdapterCallback, View.OnClickListener{

    ArrayList<NewUser> data = new ArrayList<NewUser>();
    private RecyclerView userlist;
    private SearchAddAdapter searchAddAdapter;
    private GridView selectedUser;
    final ArrayList<String> list = new ArrayList<String>();
    final ArrayList<String> track_id = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    FloatingActionButton add_user;
    Session session;
    TextInputLayout textInputLayout;
    EditText edtGroup_name;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_search_add, contentFrameLayout);
        if(getSupportActionBar() != null)
        getSupportActionBar().setTitle("Add user");
        session = new Session(SearchAddActivity.this);
        textInputLayout = (TextInputLayout) findViewById(R.id.labelGroup);
        selectedUser = (GridView) findViewById(R.id.selectedUser);
        edtGroup_name = (EditText) findViewById(R.id.etGroupName);
        submit = (Button) findViewById(R.id.add);
        submit.setOnClickListener(this);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        selectedUser.setAdapter(adapter);
        selectedUser.setMinimumHeight(180);
        selectedUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SearchAddActivity.this, view.getId()+" " + i, Toast.LENGTH_SHORT).show();
            }
        });
        userlist = (RecyclerView) findViewById(R.id.newuser_recycler_view);
        searchAddAdapter = new SearchAddAdapter(SearchAddActivity.this, data);
        userlist.setAdapter(searchAddAdapter);
        userlist.setLayoutManager(new LinearLayoutManager(SearchAddActivity.this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.uoc, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Log.d(getLocalClassName(), query);
        getUser(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void getUser(final String query){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SEARCH_User,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(getLocalClassName(), response);
                        processResponse(response);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SearchAddActivity.this, "An Error occur, Please try again later", Toast.LENGTH_SHORT).show();

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

    private void processResponse(String result){
        data.clear();
        try {
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("status").equals("success")){
                JSONArray response = jsonObject.getJSONArray("data");
                for(int i =0; i < response.length(); i++){
                    JSONObject user = response.getJSONObject(i);
                    NewUser newuser = new NewUser(
                            user.getString("id"),
                            user.getString("name"),
                            user.getString("avatar_link"));
                    data.add(newuser);
                }
                searchAddAdapter.notifyDataSetChanged();
            } else {
                data.clear();
                searchAddAdapter.notifyDataSetChanged();
                Toast.makeText(SearchAddActivity.this, "No user found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addUser() {

        final String group_name = edtGroup_name.getText().toString();
        Log.d("group", "Group name: " + group_name + " is empty? " + group_name.equals(""));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_CHAT_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("status", response);
                        if(response.equals("room found"))
                            Toast.makeText(SearchAddActivity.this, "room found", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("status", error.toString());
                        Toast.makeText(SearchAddActivity.this, "An Error occur, Please try again later", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("target_user_id",String.valueOf(track_id));
                map.put("user_id", session.getUserID());
                map.put("group_name", group_name);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SearchAddActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onUserClick(String target_user_id, String name, int position, Boolean is_checked) {
        if(is_checked && !list.contains(name)){
            list.add(name);
            track_id.add(target_user_id);
            adapter.notifyDataSetChanged();
            submit.setVisibility(list.size() > 0 ? View.VISIBLE : View.GONE);
        } else if(!is_checked && list.contains(name)) {
            Toast.makeText(SearchAddActivity.this, name + " already in list", Toast.LENGTH_SHORT).show();
            list.remove(name);
            track_id.remove(target_user_id);
            adapter.notifyDataSetChanged();
            submit.setVisibility(list.size() > 0 ? View.VISIBLE : View.GONE);
        }
        selectedUser.smoothScrollToPosition(list.size() - 1);
    }

    private void showMessage(String title, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add:
                String group_name = edtGroup_name.getText().toString();
                if(group_name.equals(""))
                    showMessage("Group name is empty", "Please fill in the group name");
                else
                    addUser();
                break;
        }
    }
}
