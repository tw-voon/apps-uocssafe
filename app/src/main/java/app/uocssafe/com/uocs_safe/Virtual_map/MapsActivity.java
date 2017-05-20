package app.uocssafe.com.uocs_safe.Virtual_map;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

import app.uocssafe.com.uocs_safe.BaseActivity;
import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.MapBottomSheetDialogFragment;
import app.uocssafe.com.uocs_safe.Helper.StatusBottomSheetDialogFragment;
import app.uocssafe.com.uocs_safe.Message.MessageActivity;
import app.uocssafe.com.uocs_safe.News.NewsActivity;
import app.uocssafe.com.uocs_safe.News.SinglePost;
import app.uocssafe.com.uocs_safe.R;
import app.uocssafe.com.uocs_safe.Helper.Request_Handler;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.UOCSActivity;
import app.uocssafe.com.uocs_safe.login_register.Login;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    ArrayList<Locations> locations = new ArrayList<Locations>();
    Request_Handler rh = new Request_Handler();
    private MarkerOptions options = new MarkerOptions();
    Session session;
    public HashMap<String, Integer> params = new HashMap<String, Integer>();
    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetDialog bottomSheetDialog;
    TextView title, desc;
    RelativeLayout bottomsheet;
    int report_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_maps, contentFrameLayout);

//        bottomSheetDialog = new BottomSheetDialog(MapsActivity.this);
//        View view1 = getLayoutInflater().inflate(R.layout.bottom_sheet_map, null);
//        bottomSheetDialog.setContentView(view1);

        session = new Session(this);

        title = (TextView) findViewById(R.id.post_title);
        desc = (TextView) findViewById(R.id.post_description);
//        bottomsheet = (RelativeLayout) findViewById(R.id.bottom_sheet);
//        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        bottomSheetBehavior.setPeekHeight(0);
//        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//
//                switch (newState) {
//                    case BottomSheetBehavior.STATE_COLLAPSED:
//                        Log.e("Bottom Sheet Behaviour", "STATE_COLLAPSED");
//                        break;
//                    case BottomSheetBehavior.STATE_DRAGGING:
//                        Log.e("Bottom Sheet Behaviour", "STATE_DRAGGING");
//                        break;
//                    case BottomSheetBehavior.STATE_EXPANDED:
//                        Log.e("Bottom Sheet Behaviour", "STATE_EXPANDED");
//                        break;
//                    case BottomSheetBehavior.STATE_HIDDEN:
//                        Log.e("Bottom Sheet Behaviour", "STATE_HIDDEN");
//                        break;
//                    case BottomSheetBehavior.STATE_SETTLING:
//                        Log.e("Bottom Sheet Behaviour", "STATE_SETTLING");
//                        break;
//                }
//
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//                bottomSheet.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        if(event.getAction() == MotionEvent.ACTION_DOWN)
//                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//
//                        return true;
//                    }
//                });
//
//            }
//        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        getActionBar();
    }

    public void getNewa(){

        Session session = new Session(this);
        final String userID = session.getUserID();

        class getNewsFeed extends AsyncTask<String, String, String> {

            ProgressDialog loading;
            Context context;

            private getNewsFeed(Context context){
                this.context = context;
                loading = new ProgressDialog(context);
            }

            @Override
            protected void onPreExecute() {
                loading.setMessage("Loading News Feed");
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected void onPostExecute(String result) {
                loading.dismiss();
//                Toast.makeText(MapsActivity.this, result, Toast.LENGTH_SHORT).show();

                try {
                    JSONArray decodedResult = new JSONArray(result);
                    for (int i = 0; i<decodedResult.length(); i++){
                        JSONObject json_data = decodedResult.getJSONObject(i);
                        Locations location = new Locations();
                        location.setLatitude(json_data.getDouble("location_latitute"));
                        location.setLongitude(json_data.getDouble("location_longitute"));
                        location.setReportID(json_data.getInt("ids"));
                        location.setReportTitle(json_data.getString("report_Title"));
                        location.setReportDescription(json_data.getString("report_Description"));
                        locations.add(location);
                    }
                    addMarkerOnMap(locations);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                String result;
                HashMap<String,String> param = new HashMap<String,String>();
                param.put("userID", userID);
                result = rh.sendPostRequest(AppConfig.URL_GetReport, param);
                return result;
            }
        }

        getNewsFeed u = new getNewsFeed(MapsActivity.this);
        u.execute();


    }

    public void addMarkerOnMap(ArrayList<Locations> locations){

//        for (int i = 0; i < locations.size(); i++){
//            mMap.addMarker(new MarkerOptions()
//                .position(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()))
//                .title(locations.get(i).getReportTitle()).snippet(locations.get(i).getReportDescription()).anchor(0.5f,0.5f));
//        }
        for (int i = 0; i < locations.size(); i++){
            options.position(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()))
                    .title(locations.get(i).getReportTitle())
                    .snippet(locations.get(i).getReportDescription());
            Marker mr = mMap.addMarker(options);
            params.put(mr.getId(), locations.get(i).getReportID());
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(params.get(marker.getId()) == null){
                    Toast.makeText(MapsActivity.this, "Current location", Toast.LENGTH_SHORT).show();
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else {
                    Log.d("YES", marker.isInfoWindowShown() + "");
                    marker.hideInfoWindow();
                    report_id = params.get(marker.getId());
//                    title.setText(marker.getTitle());
//                    desc.setText(marker.getSnippet());
                    MapBottomSheetDialogFragment bottomSheetDialogFragment = new MapBottomSheetDialogFragment();
                    bottomSheetDialogFragment.setData(marker.getTitle(), marker.getSnippet(), report_id);
                    bottomSheetDialogFragment.show(getSupportFragmentManager(), "Dialog");
//                    bottomSheetBehavior.setPeekHeight(60);
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

                return true;
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getNewa();

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("map_fail", i + " ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d("map_fail", connectionResult.toString());

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        bottomSheetBehavior.setPeekHeight(0);

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }

    public void readmore(View view) {
        Toast.makeText(MapsActivity.this, "Hello", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(MapsActivity.this, SinglePost.class);
//        intent.putExtra("report_ID", String.valueOf(report_id));
//        startActivity(intent);
    }
}
