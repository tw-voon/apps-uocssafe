package app.uocssafe.com.uocs_safe.login_register;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
    ProgressDialog loading;

    String[] permissionsRequired = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        config = new AppConfig();
        session = new Session(this);
        loading = new ProgressDialog(this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setTitle("Signing in...");

        config.changeStatusBarColor(R.color.colorPrimary, Login.this);

        txtEmail = (TextView) findViewById(R.id.etUsername);
        txtPassword = (TextView) findViewById(R.id.etPass);
        onClick();

        if(ActivityCompat.checkSelfPermission(Login.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Login.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(Login.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(Login.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login.this,permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login.this,permissionsRequired[2])){

                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(Login.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

            } else if (session.getPermission(permissionsRequired[0])) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant  Camera and Location", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }  else {
                //just request the permission
                ActivityCompat.requestPermissions(Login.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
            }
            session.setPermission(permissionsRequired[0],true);
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }

        txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    login();
                    return true;
                }
                return false;

            }
        });

        if(session.loggedin()){
            startActivity(new Intent(Login.this,UOCSActivity.class));
            finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CALLBACK_CONSTANT){
            //check if all permissions are granted
            boolean allgranted = false;
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if(allgranted){
                proceedAfterPermission();
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(Login.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login.this,permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(Login.this,permissionsRequired[2])){
//                txtPermissions.setText("Permissions Required");
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(Login.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(Login.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {
//        txtPermissions.setText("We've got all permissions");
        Toast.makeText(getBaseContext(), "We got All Permissions", Toast.LENGTH_LONG).show();
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

        loading.show();
        final String name = txtEmail.getText().toString().trim();
        final String pass = txtPassword.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);
                        loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
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
                session.putData(dataObject.getString("id"), dataObject.getString("name"), dataObject.getString("avatar_link"));
                registerKey(dataObject.getString("id"));
                session.putUserAvatar(dataObject.getString("avatar_link"));
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

    @Override
    public void onBackPressed() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loading.dismiss();
    }
}
