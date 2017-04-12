package app.uocssafe.com.uocs_safe.Emergency_Contact;

import android.app.ProgressDialog;
import android.database.Cursor;
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
import android.widget.FrameLayout;
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

public class Emergency_Contact extends BaseActivity implements SearchView.OnQueryTextListener{

    //CONNNECTION DETAILS
    public static final int connection_timeout = 10000;
    public static final int read_timeout = 15000;
    private RecyclerView contactList;
    private contactAdapter contactAdapter;
    int response_code;
    ArrayList<Contact> data = new ArrayList<>();
    database_helper myDB;
    internet_helper hasInternet;
    AppConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_emergency_contact, contentFrameLayout);
//        setContentView(R.layout.activity_emergency_contact);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDB = new database_helper(this);
        config = new AppConfig();


        if(hasInternet.isNetworkStatusAvialable(Emergency_Contact.this))
            new AsyncFetch().execute();
        else
            loadOfflineData();
//            Toast.makeText(Emergency_Contact.this, "Network not available", Toast.LENGTH_SHORT).show();
        //call to AsyncTask

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void loadOfflineData(){

        Cursor res = myDB.getOfflineData("api/emergency_contact", "GET");
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
                Contact contactdata = new Contact();
                contactdata.setTitle(json_data.getString("contact_name"));
                contactdata.setContact_no(json_data.getString("contact_number"));
                contactdata.setDesc(json_data.getString("contact_description"));
                data.add(contactdata);
            }

//            Toast.makeText(Emergency_Contact.this, "Loading view" + response_code, Toast.LENGTH_SHORT).show();
            contactList = (RecyclerView) findViewById(R.id.contact_recycler_view);
            contactAdapter = new contactAdapter(Emergency_Contact.this, data);
            contactList.setAdapter(contactAdapter);
            contactList.setLayoutManager(new LinearLayoutManager(Emergency_Contact.this));

        } catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(Emergency_Contact.this, "Fail to load from database", Toast.LENGTH_SHORT).show();
            Toast.makeText(Emergency_Contact.this, "Error \n" + e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    public void processActivity(ArrayList<Contact> data) {
        Toast.makeText(Emergency_Contact.this, "Loading view" + response_code, Toast.LENGTH_SHORT).show();
        contactList = (RecyclerView) findViewById(R.id.contact_recycler_view);
        contactAdapter = new contactAdapter(Emergency_Contact.this, data);
        contactList.setAdapter(contactAdapter);
        contactList.setLayoutManager(new LinearLayoutManager(Emergency_Contact.this));
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
        ArrayList<Contact> newList = new ArrayList<>();
        for(Contact contacts : data)
        {
            String title = contacts.getTitle().toLowerCase();
            String contact = contacts.getContact_no();
            String desc = contacts.getDesc().toLowerCase();

            if(title.contains(newText) || contact.contains(newText) || desc.contains(newText))
            {
                newList.add(contacts);
//                Toast.makeText(Emergency_Contact.this, "value" + contacts.getContact_no(), Toast.LENGTH_SHORT).show();
            }

        }
        if(newList.isEmpty())
            Toast.makeText(Emergency_Contact.this, "No Result Found", Toast.LENGTH_SHORT).show();

        contactAdapter.filter(newList);
        return true;
    }

    public void showMessage(String result){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("Result from server");
        alert.setMessage(result);
        alert.show();
    }


    private class AsyncFetch extends AsyncTask<String, String, String>
    {

        ProgressDialog loading = new ProgressDialog(Emergency_Contact.this);
        HttpURLConnection connection;
        URL url = null;
        AppConfig config = new AppConfig();

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
                url = new URL(config.URL_EContacts);
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
            Log.d("MyActivity", result);
            try {
                if(response_code == 200)
                    myDB.insertData(result, "api/emergency_contact", "GET");
                JSONArray jArray = new JSONArray(result);

                for(int i=0; i<jArray.length(); i++)
                {

                    JSONObject json_data = jArray.getJSONObject(i);
                    Contact contact = new Contact();
                    contact.setTitle(json_data.getString("contact_name"));
                    contact.setContact_no(json_data.getString("contact_number"));
                    contact.setDesc(json_data.getString("contact_description"));
                    data.add(contact);

                }

                processActivity(data);


            } catch (JSONException error) {

                //                Toast.makeText(Emergency_Contact.this, "Unknown Error Occur" + error.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(Emergency_Contact.this, "Loading Offline Data", Toast.LENGTH_SHORT).show();
                loadOfflineData();
            }
        }
    }


}
