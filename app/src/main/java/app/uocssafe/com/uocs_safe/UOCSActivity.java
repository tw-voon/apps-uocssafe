package app.uocssafe.com.uocs_safe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.Helper.database_helper;
import app.uocssafe.com.uocs_safe.News.NewsActivity;
import app.uocssafe.com.uocs_safe.login_register.Login;


public class UOCSActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //button declaration
    public Button emergencyContact;
    public Button reporting;
    public Button virtualMap;
    private TextView name;
    database_helper myDB;
    Session session;
    AppConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = new database_helper(this);
        session = new Session(this);
        config = new AppConfig();

        setContentView(R.layout.activity_uocs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name = (TextView)findViewById(R.id.username);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        onClick();

        if(session.loggedin()) {
            name.setText(session.getUsername());
        }

    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }


    public void onClick() {
        emergencyContact = (Button) findViewById(R.id.emergency_contact);
        reporting = (Button) findViewById(R.id.reporting);
        virtualMap = (Button) findViewById(R.id.Map);

        emergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("app.uocssafe.com.uocs_safe.Emergency_Contact.Emergency_Contact");
                startActivity(intent);
            }
        });

        reporting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!session.loggedin()){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UOCSActivity.this);
                    alertDialogBuilder.setMessage("You need to login before proceed to this section");
                    alertDialogBuilder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(UOCSActivity.this,Login.class));
                        }
                    });
                    alertDialogBuilder.show();
                } else {
                    Intent intent = new Intent("app.uocssafe.com.uocs_safe.Report_Activity.Reporting_Category");
                    startActivity(intent);
                }


            }
        });

        virtualMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("app.uocssafe.com.uocs_safe.Virtual_map.MapsActivity");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.uoc, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_message) {
            Intent intent = new Intent("app.uocssafe.com.uocs_safe.Message.MessageActivity");
            startActivity(intent);
//            overridePendingTransition(R.transition.fade_in,R.transition.fade_out);
//            finish();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

            session.setLoggedin(false);
            finish();
            startActivity(new Intent(UOCSActivity.this,Login.class));

        } else if (id == R.id.news){
            startActivity(new Intent(UOCSActivity.this, NewsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
