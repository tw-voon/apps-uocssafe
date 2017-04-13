package app.uocssafe.com.uocs_safe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.GcmIntentService;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.Helper.database_helper;
import app.uocssafe.com.uocs_safe.Message.MessageActivity;
import app.uocssafe.com.uocs_safe.News.NewsActivity;
import app.uocssafe.com.uocs_safe.Virtual_map.MapsActivity;
import app.uocssafe.com.uocs_safe.login_register.Login;


public class UOCSActivity extends BaseActivity{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    //button declaration
    public TableRow emergencyContact;
    public RelativeLayout reporting;
    public RelativeLayout virtualMap;
    private TextView name;
    database_helper myDB;
    Session session;
    AppConfig config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remember this is the FrameLayout area within your activity_main.xml
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.content_uocs, contentFrameLayout);
//        setContentView(R.layout.activity_uocs);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        name = (TextView)findViewById(R.id.username);
        myDB = new database_helper(this);
        session = new Session(this);
        config = new AppConfig();

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        onClick();

        if(session.loggedin()) {
            name.setText(session.getUsername());
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(AppConfig.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String token = intent.getStringExtra("token");

                    Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(AppConfig.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

                    Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();
                }
            }
        };

        if (checkPlayServices()) {
            registerGCM();
        }

    }

    // starting the service to register with GCM
    private void registerGCM() {
        Intent intent = new Intent(UOCSActivity.this, GcmIntentService.class);
        intent.putExtra(GcmIntentService.KEY, "register");
        startService(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("TAG", "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }


    public void onClick() {
        emergencyContact = (TableRow) findViewById(R.id.emergency_contact);
        reporting = (RelativeLayout) findViewById(R.id.reporting);
        virtualMap = (RelativeLayout) findViewById(R.id.Map);

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

/*    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_message) {
            Intent intent = new Intent("app.uocssafe.com.uocs_safe.Message.MessageActivity");
            startActivity(intent);
//            overridePendingTransition(R.transition.fade_in,R.transition.fade_out);
//            finish();

        }  else if (id == R.id.news){
            startActivity(new Intent(UOCSActivity.this, NewsActivity.class));
        } else if (id == R.id.mainmenu){
            Log.d("current: ", this.getClass().getSimpleName());
            if(this.getClass().getSimpleName().equals("UOCSActivity"))
                Log.d("Status: ", "Already in UOCSActivity");
            else
                startActivity(new Intent(UOCSActivity.this, UOCSActivity.class));
        } else if (id == R.id.map){
            startActivity(new Intent(UOCSActivity.this, MapsActivity.class));
        } else if (id== R.id.message){
            startActivity(new Intent(UOCSActivity.this, MessageActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppConfig.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppConfig.PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
