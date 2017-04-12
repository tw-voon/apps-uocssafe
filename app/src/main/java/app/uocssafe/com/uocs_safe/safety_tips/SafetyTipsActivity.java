package app.uocssafe.com.uocs_safe.safety_tips;

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
import app.uocssafe.com.uocs_safe.Helper.internet_helper;
import app.uocssafe.com.uocs_safe.News.SinglePost;
import app.uocssafe.com.uocs_safe.R;

public class SafetyTipsActivity extends BaseActivity {

    private List<CategoryModel> categories;
    private RecyclerView categoryList;
    CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_safety_tips, contentFrameLayout);

        categories = new ArrayList<>();
        categoryList = (RecyclerView) findViewById(R.id.safetytips_list);

        if(internet_helper.isNetworkStatusAvialable(SafetyTipsActivity.this)){
            getSafetyTipsList();
        } else {
            Toast.makeText(SafetyTipsActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        /*categoryAdapter = new CategoryAdapter(categories);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        categoryList.setLayoutManager(layoutManager);
        categoryList.setItemAnimator(new DefaultItemAnimator());
        categoryList.setAdapter(categoryAdapter);*/

    }

    private void getSafetyTipsList() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_TipsCategory,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Status: ", response);
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

    private void processSafetyTips(String response) {

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(response);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject result = jsonArray.getJSONObject(i);
                CategoryModel category = new CategoryModel();
                category.setCategory_id(result.getInt("id"));
                category.setCategoryName(result.getString("category_name"));
                categories.add(category);
            }

            categoryAdapter = new CategoryAdapter(categories);
            categoryList.setAdapter(categoryAdapter);
            categoryList.setLayoutManager(new LinearLayoutManager(SafetyTipsActivity.this));
            categoryList.setFocusable(false);
            categoryList.setNestedScrollingEnabled(false);
            categoryAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
