package app.uocssafe.com.uocs_safe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;

import app.uocssafe.com.uocs_safe.Emergency_Contact.Contact;
import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.GcmIntentService;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.Helper.database_helper;
import app.uocssafe.com.uocs_safe.login_register.Login;
import de.hdodenhof.circleimageview.CircleImageView;


public class UOCSActivity extends BaseActivity{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    //button declaration
    public TableRow emergencyContact;
    public TableRow reporting;
    public TableRow virtualMap;
    public LinearLayout siren;
    private TextView name;
    database_helper myDB;
    Session session;
    AppConfig config;
    MediaPlayer mp;
    boolean sound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remember this is the FrameLayout area within your activity_main.xml
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.content_uocs, contentFrameLayout);
        myDB = new database_helper(this);
        session = new Session(this);
        config = new AppConfig();
        Contact contact = new Contact("Manual contact", "0123456789", "sample desc");

        onClick();

        siren = (LinearLayout) findViewById(R.id.sirens);


        siren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("sound", sound+"");

                if(mp == null){
                    mp = MediaPlayer.create(UOCSActivity.this, R.raw.siren);
                    mp.setLooping(true);
                    siren.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                    mp.start();
                } else {
                    mp.stop();
                    mp.release();
                    mp = null;
                    siren.setBackgroundColor(getResources().getColor(R.color.colorAmber));
                }
//                if(!mp.isPlaying()) {
//                    sound = true;
//                    mp.start();
//                    siren.setBackgroundColor(getResources().getColor(R.color.colorOrange));
//                }
//                else{
//                    sound = false;
//                    mp.pause();
//                    siren.setBackgroundColor(getResources().getColor(R.color.colorAmber));
//                }
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(AppConfig.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    String token = intent.getStringExtra("token");

//                    Toast.makeText(getApplicationContext(), "GCM registration token: " + token, Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(AppConfig.SENT_TOKEN_TO_SERVER)) {
                    // gcm registration id is stored in our server's MySQL

//                    Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                } else if (intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)) {
                    // new push notification is received

//                    Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();
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
        reporting = (TableRow) findViewById(R.id.reporting);
        virtualMap = (TableRow) findViewById(R.id.Map);

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
                    Intent intent = new Intent("app.uocssafe.com.uocs_safe.Virtual_map.MapsActivity");
                    startActivity(intent);
                }
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
