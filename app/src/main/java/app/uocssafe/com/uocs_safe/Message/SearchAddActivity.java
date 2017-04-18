package app.uocssafe.com.uocs_safe.Message;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.uocssafe.com.uocs_safe.BaseActivity;
import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Message.Models.NewUser;
import app.uocssafe.com.uocs_safe.R;

public class SearchAddActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    ArrayList<NewUser> data = new ArrayList<NewUser>();
    private RecyclerView userlist;
    private SearchAddAdapter searchAddAdapter;
    private ListView selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_search_add, contentFrameLayout);
        if(getSupportActionBar() != null)
        getSupportActionBar().setTitle("Add user");
        selectedUser = (ListView) findViewById(R.id.selectedUser);
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
}
