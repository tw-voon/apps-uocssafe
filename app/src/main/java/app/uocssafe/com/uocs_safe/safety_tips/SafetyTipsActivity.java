package app.uocssafe.com.uocs_safe.safety_tips;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import app.uocssafe.com.uocs_safe.Helper.database_helper;
import app.uocssafe.com.uocs_safe.Helper.internet_helper;
import app.uocssafe.com.uocs_safe.News.SinglePost;
import app.uocssafe.com.uocs_safe.R;

public class SafetyTipsActivity extends BaseActivity {

    private List<CategoryModel> categories;
    private RecyclerView categoryList;
    CategoryAdapter categoryAdapter;
    database_helper mydb;
    public static final String SAFETY_TIPS = "api/tips_categories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_safety_tips, contentFrameLayout);
        mydb = new database_helper(SafetyTipsActivity.this);

        categories = new ArrayList<>();
        categoryList = (RecyclerView) findViewById(R.id.safetytips_list);

        categoryAdapter = new CategoryAdapter(categories);
        categoryList.setAdapter(categoryAdapter);
        categoryList.setLayoutManager(new LinearLayoutManager(SafetyTipsActivity.this));
        categoryList.setItemAnimator(new DefaultItemAnimator());
        categoryList.setNestedScrollingEnabled(false);

        if(internet_helper.isNetworkStatusAvialable(SafetyTipsActivity.this)){
            loadOfflineData();
            getSafetyTipsList();
        } else {
            loadOfflineData();
            Toast.makeText(SafetyTipsActivity.this, "No Internet Connection, loading offline data", Toast.LENGTH_SHORT).show();
        }
    }

    private void getSafetyTipsList() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_TipsCategory,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Status: ", response);
                        mydb.insertData(response, SAFETY_TIPS, "GET");
                        processSafetyTips(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SafetyTipsActivity.this, "An Error occur, Please try again later " + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void loadOfflineData(){

        Cursor res = mydb.getOfflineData(SAFETY_TIPS, "GET");
        if(res.getCount() == 0){
            Toast.makeText(getApplicationContext(), "No offline data available", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder buffer = new StringBuilder();
        while(res.moveToNext())
        {
            buffer.append(res.getString(3));
        }
//        showMessage(buffer.toString());
        String buffers = buffer.toString();
        try {
            JSONArray jArray = new JSONArray(buffers);
            categories.clear();

            for(int i=0; i<jArray.length(); i++)
            {
                JSONObject result = jArray.getJSONObject(i);
                CategoryModel category = new CategoryModel();
                category.setCategory_id(result.getInt("id"));
                category.setCategoryName(result.getString("category_name"));
                categories.add(category);
            }
            categoryAdapter.notifyDataSetChanged();
        } catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(SafetyTipsActivity.this, "Fail to load from database", Toast.LENGTH_SHORT).show();
            Toast.makeText(SafetyTipsActivity.this, "Error \n" + e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void processSafetyTips(String response) {

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(response);
            categories.clear();
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject result = jsonArray.getJSONObject(i);
                CategoryModel category = new CategoryModel();
                category.setCategory_id(result.getInt("id"));
                category.setCategoryName(result.getString("category_name"));
                categories.add(category);
            }
            categoryAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
