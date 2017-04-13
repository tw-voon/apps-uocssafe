package app.uocssafe.com.uocs_safe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.Message.MessageActivity;
import app.uocssafe.com.uocs_safe.News.NewsActivity;
import app.uocssafe.com.uocs_safe.Virtual_map.MapsActivity;
import app.uocssafe.com.uocs_safe.login_register.Login;
import app.uocssafe.com.uocs_safe.safety_tips.SafetyTipsActivity;

public class BaseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    Session session;
    AppConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uocs);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        session = new Session(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.mainmenu:
                        Intent main = new Intent(getApplicationContext(), UOCSActivity.class);
                        startActivity(main);
//                        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//                        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
//                        toolbar.setNavigationIcon(getResources().getColor(R.color.colorWhite));
//                        changeStatusBarColor(R.color.colorYellow, UOCSActivity.class);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.map:
                        Intent map = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(map);
                        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//                        changeStatusBarColor(R.color.colorPrimaryDark);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.news:
                        Intent news = new Intent(getApplicationContext(), NewsActivity.class);
                        startActivity(news);
                        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//                        changeStatusBarColor(R.color.colorPrimaryDark);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.message:
                        Intent message = new Intent(getApplicationContext(), MessageActivity.class);
                        startActivity(message);
                        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//                        changeStatusBarColor(R.color.colorPrimaryDark);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.tips:
                        Intent safetytips = new Intent(getApplicationContext(), SafetyTipsActivity.class);
                        startActivity(safetytips);
                        toolbar.setBackgroundColor(getResources().getColor(R.color.colorOrange));
//                        changeStatusBarColor(R.color.colorOrange);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.out:
                        session.setLoggedin(false);
                        Intent out = new Intent(getApplicationContext(), Login.class);
                        startActivity(out);
                        drawerLayout.closeDrawers();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        actionBarDrawerToggle.syncState();
    }
}
