package app.uocssafe.com.uocs_safe;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.Message.MessageActivity;
import app.uocssafe.com.uocs_safe.News.NewsActivity;
import app.uocssafe.com.uocs_safe.Personal.PersonalActivity;
import app.uocssafe.com.uocs_safe.Virtual_map.MapsActivity;
import app.uocssafe.com.uocs_safe.login_register.Login;
import app.uocssafe.com.uocs_safe.safety_tips.SafetyTipsActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    Session session;
    AppConfig config;
    public static int PICK_PROFILE_IMAGE = 3;
    TextView user_name;
    CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uocs);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        session = new Session(this);

        View header_view = navigationView.getHeaderView(0);
        TextView username = (TextView) header_view.findViewById(R.id.username);
        CircleImageView user_profile = (CircleImageView) header_view.findViewById(R.id.profile_image);
        user_name = username;
        profileImage = user_profile;

        Log.d("tag_1", session.getUsername() + "");
        if(session.getUsername() == null)
            user_name.setText("Guest User");
        else
            user_name.setText(session.getUsername());
        Log.d("profile", "not avatar"+session.getUserAvatar());
        if(session.isGuest())
            user_profile.setImageResource(R.drawable.head_1);
        else if(session.getUserAvatar().equals("null")) {
            user_profile.setImageResource(R.drawable.head_1);
            Log.d("profile", "not avatar");
        }
        else
            Picasso.with(getApplicationContext()).load(session.getUserAvatar()).into(user_profile);

//        Log.d("avatar", session.getUserAvatar());

        user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"),PICK_PROFILE_IMAGE);

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.mainmenu:
                        Intent main = new Intent(getApplicationContext(), UOCSActivity.class);
                        startActivity(main);
                        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.map:

                        if(check_access()){
                            Intent map = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(map);
                            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            drawerLayout.closeDrawers();
                        }

                        break;

                    case R.id.news:

                        if(check_access()) {
                            Intent news = new Intent(getApplicationContext(), NewsActivity.class);
                            startActivity(news);
                            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            drawerLayout.closeDrawers();
                        }
                        break;

                    case R.id.message:
                        if(check_access()) {
                            Intent message = new Intent(getApplicationContext(), MessageActivity.class);
                            startActivity(message);
                            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            drawerLayout.closeDrawers();
                        }
                        break;

                    case R.id.tips:
                        Intent safetytips = new Intent(getApplicationContext(), SafetyTipsActivity.class);
                        startActivity(safetytips);
                        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.personal:
                        if(check_access()) {
                            Intent personal = new Intent(getApplicationContext(), PersonalActivity.class);
                            startActivity(personal);
                            drawerLayout.closeDrawers();
                        }
                        break;

                    case R.id.out:
                            logout();
                            drawerLayout.closeDrawers();
                        break;
                }
                return false;
            }
        });
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public boolean check_access(){
        if(!session.loggedin()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BaseActivity.this);
            alertDialogBuilder.setMessage("You need to login before proceed to this section");
            alertDialogBuilder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getApplicationContext(),Login.class));
                }
            });
            alertDialogBuilder.show();
            return false;
        } else {
            Log.d("logging?", session.loggedin() + "?");
            return true;
        }
    }

    public boolean logout(){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BaseActivity.this);
            alertDialogBuilder.setMessage("Are you sure want to log out?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    session.setLoggedin(false);
                    session.setGuess(false);
                    session.clearPreference();
                    Intent out = new Intent(getApplicationContext(), Login.class);
                    startActivity(out);
                    finish();
                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.show();
            return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PROFILE_IMAGE && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            Uri uri = data.getData();
            Log.d("url", uri.toString());
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Log.d("result228", imageStream.toString() + " ");
                bitmap = BitmapFactory.decodeStream(imageStream);
                bitmap = getResizedBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4);
                profileImage.setImageBitmap(bitmap);
                profileImage.setVisibility(View.VISIBLE);
                session.putUserAvatar(encodeTobase64(bitmap));
                uploadAvatar();
            } catch (IOException e) {
                Log.d("bitmapE", String.valueOf(e));
                e.printStackTrace();
            }
        }
    }

    public void uploadAvatar(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_AddAvatar,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response----", response);
                        try {
                            JSONObject result = new JSONObject(response);
                            if(result.getString("status").equals("success")){
                                Toast.makeText(BaseActivity.this,"Successfully uploaded profile image",Toast.LENGTH_LONG ).show();
                                session.putUserAvatar(result.getString("avatar"));
                            }
                            else Toast.makeText(BaseActivity.this,"Fail to upload profile image",Toast.LENGTH_LONG ).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BaseActivity.this,error.toString(),Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("image",session.getUserAvatar());
                map.put("user_id", session.getUserID());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
