package app.uocssafe.com.uocs_safe.News;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
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

    public static final int connection_timeout = 10000;
    public static final int read_timeout = 15000;
    private RecyclerView newslist;
    private NewsAdapter newsAdapter;
    ArrayList<News> newsData = new ArrayList<>();
    internet_helper hasInternet;
    Request_Handler rh = new Request_Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_news, contentFrameLayout);

        if(hasInternet.isNetworkStatusAvialable(NewsActivity.this))
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
            for (int i = 0; i<decodedResult.length(); i++){
                JSONObject json_data = decodedResult.getJSONObject(i);
                News news = new News();
                news.setUsername(json_data.getString("name"));
                news.setNewsTitle(json_data.getString("report_Title"));
                news.setDescription(json_data.getString("report_Description"));
                news.setImageLink(json_data.getString("image"));
                news.setNewsID(json_data.getString("report_ID"));
                news.setTimestamp(json_data.getString("created_at"));
                newsData.add(news);
            }

            newslist = (RecyclerView) findViewById(R.id.report_recyclerView);
            newsAdapter = new NewsAdapter(NewsActivity.this, newsData);
            newslist.setAdapter(newsAdapter);
            newslist.setLayoutManager(new LinearLayoutManager(NewsActivity.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
