package app.uocssafe.com.uocs_safe.Report_Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.uocssafe.com.uocs_safe.BaseActivity;
import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.Helper.Utility;
import app.uocssafe.com.uocs_safe.R;
import app.uocssafe.com.uocs_safe.Helper.database_helper;
import app.uocssafe.com.uocs_safe.Helper.internet_helper;
import app.uocssafe.com.uocs_safe.UOCSActivity;

import static android.R.attr.bitmap;

public class Reporting_Category extends BaseActivity
        implements GoogleApiClient.OnConnectionFailedListener,Spinner.OnItemSelectedListener {

    private static final int read_timeout = 10000;
    private static final int connection_timeout = 15000;
    Spinner categorySpinner;
    EditText postTitle, postDesc;
    ImageView ivimage, ivlocation, imagePreview, locationPreview;
    TextView locationName;
    String selectedLatitute, selectedLongitute, userLocation, userChoosenTask, userID, categoryID;
    CardView card;
    int response_code;
    database_helper myDB;
    final ArrayList<Category> data = new ArrayList<>();
    GridView gridView;
    AppConfig config;
    Session session;
    GoogleApiClient mGoogleApiClient;
    final static int PLACE_PICKER_CODE = 1000, REQUEST_CAMERA = 1888, SELECT_FILE = 1, PICK_IMAGE_REQUEST = 2;
    private ArrayList<String> categoryData;
    private Bitmap bitmap;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_reporting__category, contentFrameLayout);
        myDB = new database_helper(this);
        config = new AppConfig();
        session = new Session(this);
        userID = session.getUserID();
        loading = new ProgressDialog(Reporting_Category.this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        /*Link variable to XML*/
        categorySpinner = (Spinner) findViewById(R.id.category);
        categorySpinner.setOnItemSelectedListener(this);
        postTitle = (EditText) findViewById(R.id.etTitle);
        postDesc = (EditText) findViewById(R.id.etDesc);
        ivimage = (ImageView) findViewById(R.id.ivimage);
        ivlocation = (ImageView) findViewById(R.id.ivlocation);
        imagePreview = (ImageView) findViewById(R.id.imagePreview);
        locationName = (TextView) findViewById(R.id.locationName);
        locationPreview = (ImageView) findViewById(R.id.locationPreview);
        card = (CardView) findViewById(R.id.card_view);
        categoryData = new ArrayList<>();

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).
                addApi(Places.PLACE_DETECTION_API).enableAutoManage(this, this).build();

        if(internet_helper.isNetworkStatusAvialable(Reporting_Category.this))
            getSpinnerData();
        else
            showMessage("No internet connection", "This feature required internet connection");
    }

    private Boolean validate_details(){

        final String title = postTitle.getText().toString().trim();
        final String desc = postTitle.getText().toString().trim();
        boolean status = true;

        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || bitmap == null || userLocation.equals("")){
            status = false;
            showMessage();
        }

        return status;
    }

    private void showMessage(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Reporting_Category.this);
        alertDialogBuilder.setMessage("Make sure you have input all the field");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.show();
    }

    public void submit(View view) {
        if(validate_details())
            UploadtoServer();
    }

    private void UploadtoServer(){

        loading.setMessage("Uploading...");
        loading.setCancelable(true);
        loading.show();

        final String title = postTitle.getText().toString().trim();
        final String desc = postDesc.getText().toString().trim();
        final String image = getStringImage(bitmap);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_ReportPost,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Success")) {
                            loading.dismiss();
                            Toast.makeText(Reporting_Category.this, "Success uploaded to server", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Reporting_Category.this, UOCSActivity.class);
                            startActivity(intent);
                        }
                        else
                            loading.dismiss();
//                            Toast.makeText(Reporting_Category.this, "Error uploading to server", Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(Reporting_Category.this, "An Error occur, Please try again later", Toast.LENGTH_SHORT).show();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<String,String>();
                param.put("title",title);
                param.put("description",desc);
                param.put("userID", userID);
                param.put("typeID", categoryID);
                param.put("location_name", userLocation);
                param.put("location_latitude", selectedLatitute);
                param.put("location_longitude", selectedLongitute);
                param.put("image",image);
                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void selectLocation(View view) {
        loading.setMessage("Loading map...");
        loading.setCancelable(true);
        loading.show();
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent intent;
        try {
            intent = builder.build(Reporting_Category.this);
            startActivityForResult(intent, PLACE_PICKER_CODE);
            loading.dismiss();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_CODE && resultCode == RESULT_OK && data != null) {
            Place place = PlacePicker.getPlace(this, data);

            userLocation = String.format("%s", place.getName());
            String name = String.format("%s", place.getName());
            selectedLatitute = String.valueOf(place.getLatLng().latitude);
            selectedLongitute = String.valueOf(place.getLatLng().longitude);

            String location = String.format("at: %s", name);
            locationName.setText(location);
            locationName.setVisibility(View.VISIBLE);
            getGoogleStaticMap(selectedLatitute, selectedLongitute);
        }

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            onCaptureImageResult(data);
        }

        Log.d("request:", requestCode + "result code: " + resultCode);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(imageStream);
                bitmap = getResizedBitmap(bitmap, bitmap.getWidth() / 5, bitmap.getHeight() / 5);
                imagePreview.setImageBitmap(bitmap);
                imagePreview.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.d("bitmapE", String.valueOf(bitmap));
                e.printStackTrace();
            }
        }
    }

    private void getGoogleStaticMap(String latitute, String longitute){

        Picasso.with(getApplicationContext()).cancelRequest(locationPreview);
        Picasso.with(getApplicationContext())
                .load(config.getStaticMap(latitute, longitute)).error(R.drawable.ic_report_black_24dp).placeholder(R.drawable.circle)
                .into(locationPreview);

        locationPreview.setVisibility(View.VISIBLE);

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

    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        Log.d("imagetag----", bitmap.toString());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imagePreview.setImageBitmap(bitmap);
        imagePreview.setVisibility(View.VISIBLE);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void chooseImage(View view) {

        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Reporting_Category.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(Reporting_Category.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    public String getStringImage(Bitmap bmp){
        Log.d("bmp----------------", bmp.toString() + "");
        String encodedImage;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        byte[] imageBytes = output.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void galleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),PICK_IMAGE_REQUEST);
    }

    private void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void getSpinnerData(){
        Session session = new Session(this);
        final String userID = session.getUserID();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_ReportType,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processCategory(response);
                        Log.d("spinner", response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(Reporting_Category.this, "An Error occur, Please try again later", Toast.LENGTH_SHORT).show();
                        Log.d("spinner", error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("userID",userID);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void processCategory(String result){

        JSONArray jArray = null;
        try {
            jArray = new JSONArray(result);
            for(int i=0; i<jArray.length(); i++)
            {

                JSONObject json_data = jArray.getJSONObject(i);
                Category category = new Category();
                category.setCategoryName(json_data.getString("typeName"));
                category.setCategoryID(json_data.getString("id"));
                category.setCategoryIcon(R.mipmap.ic_launcher);
                categoryData.add(json_data.getString("typeName"));
                data.add(category);
                categorySpinner.setAdapter(new ArrayAdapter<String>(Reporting_Category.this, android.R.layout.simple_spinner_dropdown_item, categoryData));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Category category = data.get(position);
        categoryID = category.getCategoryID();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showMessage(String title, String result){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle(title);
        alert.setMessage(result);
        alert.show();
    }
}
