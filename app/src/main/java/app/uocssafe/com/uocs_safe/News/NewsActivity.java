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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.R;
import app.uocssafe.com.uocs_safe.Helper.Request_Handler;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.Helper.internet_helper;

public class NewsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

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
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(hasInternet.isNetworkStatusAvialable(NewsActivity.this))
            getNewsFeed();
        else
            Toast.makeText(NewsActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();

    }

    public void getNewsFeed(){

        Session session = new Session(this);
        final String userID = session.getUserID();

        class getNewsFeed extends AsyncTask<String, String, String> {

            ProgressDialog loading;
            Context context;

            private getNewsFeed(Context context){
                this.context = context;
                loading = new ProgressDialog(context);
            }

            @Override
            protected void onPreExecute() {
                loading.setMessage("Loading News Feed");
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected void onPostExecute(String result) {
                loading.dismiss();
                Toast.makeText(NewsActivity.this, result, Toast.LENGTH_SHORT).show();

                try {
                    JSONArray decodedResult = new JSONArray(result);
                    for (int i = 0; i<decodedResult.length(); i++){
                        JSONObject json_data = decodedResult.getJSONObject(i);
                        News news = new News();
                        news.setUsername(json_data.getString("name"));
                        news.setNewsTitle(json_data.getString("report_Title"));
                        news.setDescription(json_data.getString("report_Description"));
                        news.setImageLink(json_data.getString("image"));
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
            protected String doInBackground(String... params) {
                String result;

                HashMap<String,String> param = new HashMap<String,String>();
                param.put("userID", userID);
                result = rh.sendPostRequest(AppConfig.URL_GetReport, param);
                return result;
            }
        }

        getNewsFeed u = new getNewsFeed(NewsActivity.this);
        u.execute();
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
