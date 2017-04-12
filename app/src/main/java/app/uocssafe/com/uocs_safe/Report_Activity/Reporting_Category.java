package app.uocssafe.com.uocs_safe.Report_Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import app.uocssafe.com.uocs_safe.BaseActivity;
import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.R;
import app.uocssafe.com.uocs_safe.Helper.database_helper;
import app.uocssafe.com.uocs_safe.Helper.internet_helper;

public class Reporting_Category extends BaseActivity {

    private static final int read_timeout = 10000;
    private static final int connection_timeout = 15000;
    int response_code;
    database_helper myDB;
    final ArrayList<Category> data = new ArrayList<>();
    GridView gridView;
    internet_helper hasInternet;
    AppConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_reporting__category, contentFrameLayout);
//        setContentView(R.layout.activity_reporting__category);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDB = new database_helper(this);
        config = new AppConfig();

        if(hasInternet.isNetworkStatusAvialable(Reporting_Category.this))
            new AsyncFetch().execute();
        else
            loadOfflineData();

    }

    private class AsyncFetch extends AsyncTask<String, String, String>
    {

        ProgressDialog loading = new ProgressDialog(Reporting_Category.this);
        HttpURLConnection connection;
        URL url = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            loading.setMessage("Loading");
            loading.setCancelable(false);
            loading.show();
        }
        @Override
        protected String doInBackground(String... strings) {

            try {
                url = new URL(config.URL_ReportType);
            } catch (MalformedURLException e){
                e.printStackTrace();
                return e.toString();
            }

            try {

                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(read_timeout);
                connection.setConnectTimeout(connection_timeout);
                connection.setRequestMethod("GET");

            } catch (IOException error){

                error.printStackTrace();
                return error.toString();

            }

            try {
                response_code = connection.getResponseCode();

                if(response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while((line = reader.readLine())!= null){
                        result.append(line);
                    }

                    return (result.toString());
                }
                else {
                    return ("unsuccessful");
                }
            } catch (IOException error){
                error.printStackTrace();
                return error.toString();
            } finally {
                connection.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String result) {
            loading.dismiss();
            loading.dismiss();
//            showMessage(result);

            try {
                if(response_code == 200)
                    myDB.insertData(result, "api/report_categories", "GET");
                JSONArray jArray = new JSONArray(result);

                for(int i=0; i<jArray.length(); i++)
                {

                    JSONObject json_data = jArray.getJSONObject(i);
                    Category category = new Category();
                    category.setCategoryName(json_data.getString("typeName"));
                    category.setCategoryID(json_data.getString("typeID"));
                    category.setCategoryIcon(R.mipmap.ic_launcher);
//                    category.setDesc(json_data.getString("contact_description"));
                    data.add(category);

                }

                processActivity(data);


            } catch (JSONException error) {

                //                Toast.makeText(Emergency_Contact.this, "Unknown Error Occur" + error.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(Reporting_Category.this, "Loading Offline Data", Toast.LENGTH_SHORT).show();
                loadOfflineData();
            }
        }
    }

    public void showMessage(String result){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("Result from server");
        alert.setMessage(result);
        alert.show();
    }

    public void processActivity(final ArrayList<Category> data) {
        Toast.makeText(Reporting_Category.this, "Loading view", Toast.LENGTH_SHORT).show();
        gridView = (GridView) findViewById(R.id.category_grid);
        CategoryAdapter adapter = new CategoryAdapter(Reporting_Category.this, R.layout.single_category ,data);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapterView.getSelectedItem();
                Category category = data.get(i);
                Toast.makeText(Reporting_Category.this, "You have clicked on " + category.getCategoryName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("app.uocssafe.com.uocs_safe.Report_Activity.report_form");
                intent.putExtra("CATEGORY_ID", category.getCategoryID());
                intent.putExtra("CATEGORY_NAME", category.getCategoryName());
                startActivity(intent);

            }
        });
    }

    public void loadOfflineData(){

        Cursor res = myDB.getOfflineData("api/report_categories", "GET");
        if(res.getCount() == 0){
            showMessage("No offline data found");
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

            for(int i=0; i<jArray.length(); i++)
            {
                JSONObject json_data = jArray.getJSONObject(i);
                Category category = new Category();
                category.setCategoryName(json_data.getString("typeName"));
                category.setCategoryName(json_data.getString("typeID"));
                category.setCategoryIcon(R.mipmap.ic_launcher);
                data.add(category);
            }

//            Toast.makeText(Emergency_Contact.this, "Loading view" + response_code, Toast.LENGTH_SHORT).show();
            processActivity(data);

        } catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(Reporting_Category.this, "Fail to load from database", Toast.LENGTH_SHORT).show();
            Toast.makeText(Reporting_Category.this, "Error \n" + e.toString(), Toast.LENGTH_LONG).show();
        }

    }

}
