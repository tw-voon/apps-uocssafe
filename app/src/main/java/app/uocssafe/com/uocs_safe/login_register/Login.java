package app.uocssafe.com.uocs_safe.login_register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.R;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.UOCSActivity;

public class Login extends AppCompatActivity {

    private Button btnLogin, btnRegister, btnGuest;
    private TextView txtEmail, txtPassword;
    private Session session;
    private AppConfig config;
    FirebaseDatabase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        config = new AppConfig();
        session = new Session(this);

        txtEmail = (TextView) findViewById(R.id.etUsername);
        txtPassword = (TextView) findViewById(R.id.etPass);
        onClick();

        if(session.loggedin()){
            startActivity(new Intent(Login.this,UOCSActivity.class));
            finish();
        }

    }

    private void onClick() {

        btnLogin = (Button) findViewById(R.id.btnLgn);
        btnRegister = (Button) findViewById(R.id.btnReg);
        btnGuest = (Button) findViewById(R.id.btnGuest);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }

        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Register.class));
                finish();
            }
        });

        btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,UOCSActivity.class));
            }
        });
    }

    private void login() {

        final String name = txtEmail.getText().toString().trim();
        final String pass = txtPassword.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, "Line109: " + error.toString() + error.getCause(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<String,String>();
                map.put("name",name);
                map.put("pass",pass);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void processResponse(String result){

        Log.d("MyActivity", result);
        try {
            JSONObject jObject = new JSONObject(result);

            if(jObject.getString("status").equals("success")){
                JSONObject dataObject = new JSONObject(jObject.getString("data"));
                Toast.makeText(Login.this, "Success", Toast.LENGTH_SHORT).show();
                session.setLoggedin(true);
                session.putData(dataObject.getString("id"), dataObject.getString("name"));
                registerKey(dataObject.getString("id"));
                session.putFirebaseID(dataObject.getString("firebaseID"));
                Toast.makeText(Login.this, dataObject.getInt("id")+ " " + dataObject.getString("name") + " " + dataObject.getString("firebaseID"), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this, UOCSActivity.class));
                finish();
            }
            else
                Toast.makeText(Login.this, jObject.getString("data"), Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void registerKey(final String userID){

        final String token =  FirebaseInstanceId.getInstance().getToken();
        Log.d("token: 155", token);
        Log.d("token: 156", userID);

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, AppConfig.URL_RegisterFirebaseKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Login.this, "Key successfull stored", Toast.LENGTH_SHORT).show();
                        Log.d("response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this,error.toString(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map = new HashMap<String,String>();
                map.put("token",token);
                map.put("userID", userID);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}
