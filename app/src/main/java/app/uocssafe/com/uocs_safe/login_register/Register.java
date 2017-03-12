package app.uocssafe.com.uocs_safe.login_register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.DBHelper_Login;
import app.uocssafe.com.uocs_safe.R;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.UOCSActivity;

public class Register extends AppCompatActivity {

    private Button btnRegister;
    private TextView backToLogin;
    private EditText edtEmail, edtPassword;
    private Session session;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtEmail = (EditText) findViewById(R.id.etEmail);
        edtPassword = (EditText) findViewById(R.id.etPass);
        session = new Session(this);
        userRef = FirebaseDatabase.getInstance().getReference();

        onClick();

    }

    private void onClick(){

        btnRegister = (Button) findViewById(R.id.btnReg);
        backToLogin = (TextView) findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerUser();

            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,Login.class));
                finish();
            }
        });
    }

    private void registerUser() {

        final String email = edtEmail.getText().toString();
        final String pass = edtPassword.getText().toString();

        if(email.isEmpty() || pass.isEmpty()) {
            displayToast("Username/password field empty");
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_Register,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("testing--------", response);
                            processResponse(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(Register.this,error.toString(),Toast.LENGTH_LONG ).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> map = new HashMap<String,String>();
                    map.put("name",email);
                    map.put("password",pass);
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }

    private void registerKey(final String key, final String userID){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_RegisterFirebaseKey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Register.this, "Key successfull stored", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Register.this,error.toString(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("key",key);
                map.put("userID", userID);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void processResponse(String result) {

        Log.d("MyActivity", result);
        Toast.makeText(Register.this, result, Toast.LENGTH_SHORT).show();
        try {
            JSONObject jObject = new JSONObject(result);

            if (jObject.getString("status").equals("success")) {
                JSONObject dataObject = new JSONObject(jObject.getString("data"));
                getFirebaseKey(dataObject.getString("name"), dataObject.getString("id"));
                Toast.makeText(Register.this, "Success", Toast.LENGTH_SHORT).show();
                session.setLoggedin(true);
                session.putData(dataObject.getString("id"), dataObject.getString("name"));
                Toast.makeText(Register.this, dataObject.getInt("id") + " " + dataObject.getString("name"), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Register.this, UOCSActivity.class));
                finish();
            } else
                Toast.makeText(Register.this, jObject.getString("data"), Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getFirebaseKey(String username, String userID){
//        String username = dataObject.getString("name");
        String mGroupId = userRef.child("users").push().getKey();
        userRef.child("users").child(mGroupId).child("user_name").setValue(username);
        userRef.child("users").child(mGroupId).child("user_id").setValue(userID);
        userRef.child("users").child(mGroupId).child("chat_rooms").setValue(0);

        registerKey(mGroupId, userID);
        session.putFirebaseID(mGroupId);
    }


    private void displayToast(String s) {
        Toast.makeText(Register.this,s,Toast.LENGTH_SHORT).show();
    }
}