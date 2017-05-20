package app.uocssafe.com.uocs_safe.News;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.transition.Visibility;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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
import java.util.Map;

import app.uocssafe.com.uocs_safe.BaseActivity;
import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.R;
import app.uocssafe.com.uocs_safe.Helper.Request_Handler;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.Helper.internet_helper;
import app.uocssafe.com.uocs_safe.uocs_safe;

public class NewsActivity extends BaseActivity implements SearchView.OnQueryTextListener{

    private RecyclerView newslist;
    private NewsAdapter newsAdapter;
    ProgressBar loading;
    ArrayList<News> newsData = new ArrayList<>();
    internet_helper hasInternet;
    Request_Handler rh = new Request_Handler();
    TextView noContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_news, contentFrameLayout);
        newslist = (RecyclerView) findViewById(R.id.report_recyclerView);
        loading = (ProgressBar) findViewById(R.id.loading);
        newslist.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        noContent = (TextView) findViewById(R.id.no_content);

        newsAdapter = new NewsAdapter(NewsActivity.this, newsData);
        newslist.setAdapter(newsAdapter);
        newslist.setLayoutManager(new LinearLayoutManager(NewsActivity.this));

        if(internet_helper.isNetworkStatusAvialable(NewsActivity.this))
            getNewsFeed();
        else
            Toast.makeText(NewsActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();

    }

    private void getNewsFeed(){

        Session session = new Session(this);
        final String userID = session.getUserID();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_GetReport,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        processNews(response);

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(NewsActivity.this, "An Error occur, Please try again later", Toast.LENGTH_SHORT).show();

                }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("userID",userID);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void processNews(String result) {

        try {
            JSONArray decodedResult = new JSONArray(result);
            Log.d("result", result);
            newsData.clear();
            for (int i = 0; i<decodedResult.length(); i++){
                JSONObject json_data = decodedResult.getJSONObject(i);
                News news = new News(
                        json_data.getString("name"),
                        json_data.getString("report_Title"),
                        json_data.getString("report_Description"),
                        json_data.getString("image"),
                        json_data.getString("ids"),
                        json_data.getString("created_at"),
                        json_data.getString("location_name"),
                        json_data.getString("avatar_link")
                );
                newsData.add(news);
                newsAdapter.notifyDataSetChanged();
            }

            if(decodedResult.length() == 0){
                noContent.setVisibility(View.VISIBLE);
            }

            newslist.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();
        ArrayList<News> newList = new ArrayList<>();
        for(News news : newsData)
        {
            String title = news.getReportTitle().toLowerCase();
            String content = news.getNewsDescription();
            String location = news.getLocation_name().toLowerCase();

            if(title.contains(newText) || content.contains(newText) || location.contains(newText))
            {
                newList.add(news);
//                Toast.makeText(Emergency_Contact.this, "value" + contacts.getContact_no(), Toast.LENGTH_SHORT).show();
            }

        }
        if(newList.isEmpty())
            Toast.makeText(NewsActivity.this, "No Result Found", Toast.LENGTH_SHORT).show();

        newsAdapter.filter(newList);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNewsFeed();
    }
}
