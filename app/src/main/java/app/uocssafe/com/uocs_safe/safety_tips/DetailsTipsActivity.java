package app.uocssafe.com.uocs_safe.safety_tips;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import app.uocssafe.com.uocs_safe.Helper.internet_helper;
import app.uocssafe.com.uocs_safe.R;

public class DetailsTipsActivity extends BaseActivity {

    private List<DetailModel> detailModelList;
    private RecyclerView detailsView;
    private DetailsAdapter detailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_details_tips, contentFrameLayout);
        int category_id = getIntent().getIntExtra("category_id",0);
        Log.d("result: ", String.valueOf(category_id) + " ");

        detailModelList = new ArrayList<>();
        detailsView = (RecyclerView) findViewById(R.id.detailList);
        detailsAdapter = new DetailsAdapter(detailModelList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        detailsView.setLayoutManager(layoutManager);
        detailsView.setItemAnimator(new DefaultItemAnimator());
        detailsView.setAdapter(detailsAdapter);

        if(internet_helper.isNetworkStatusAvialable(DetailsTipsActivity.this)){
            getDetailsTips(category_id);
        } else{
            Toast.makeText(DetailsTipsActivity.this, "Internet not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDetailsTips(final int category_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_DetailTips,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        processNews(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(DetailsTipsActivity.this, "An Error occur, Please try again later", Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("category_id",String.valueOf(category_id));
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void processNews(String response) {
        Log.d("Reponse: ", response);
        try {
            JSONArray jsonArray = new JSONArray(response);
            for(int i = 0 ; i < jsonArray.length(); i++){
                JSONObject result = jsonArray.getJSONObject(i);
                DetailModel model = new DetailModel(result.getString("tip_name"), result.getString("tip_desc"));
                detailModelList.add(model);
            }
            detailsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
