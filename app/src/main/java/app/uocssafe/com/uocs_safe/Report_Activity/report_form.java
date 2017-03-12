package app.uocssafe.com.uocs_safe.Report_Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.util.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.R;
import app.uocssafe.com.uocs_safe.Helper.Request_Handler;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.Helper.Utility;

public class report_form extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public Button selectPic, upload, selectLocation;
    public EditText editTitle, description;
    public TextView categoryName, selectedLocation;
    public ImageView imageView;
    private Bitmap bitmap;
    String userChoosenTask, userID, userLocation, selectedLongitute, selectedLatitute;
    GoogleApiClient mGoogleApiClient;

    private int REQUEST_CAMERA = 1888, SELECT_FILE = 1;
    Session session;
    private int PICK_IMAGE_REQUEST = 1;
    Request_Handler rh = new Request_Handler();

    final static int PLACE_PICKER_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = new Session(this);

        editTitle = (EditText) findViewById(R.id.editTitle);
        description = (EditText) findViewById(R.id.description);
        categoryName = (TextView) findViewById(R.id.categoryName);
        selectedLocation = (TextView) findViewById(R.id.selectedLocation);
        imageView = (ImageView) findViewById(R.id.img_post);

        selectPic = (Button) findViewById(R.id.selectPicture);
        upload = (Button) findViewById(R.id.upload);
        selectLocation = (Button) findViewById(R.id.edLocation);

        selectPic.setOnClickListener(this);
        upload.setOnClickListener(this);
        selectLocation.setOnClickListener(this);
        selectedLocation.setOnClickListener(this);

        String name = getIntent().getStringExtra("CATEGORY_NAME");
        userID = session.getUserID();
        categoryName.append(name);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).enableAutoManage(this, this).build();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.selectPicture:
                selectImage();
                break;
            case R.id.edLocation:
                Toast.makeText(report_form.this, "Constructing", Toast.LENGTH_SHORT).show();
                break;
            case R.id.upload:
                uploadImage();
                break;
            case R.id.selectedLocation:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(report_form.this);
                    startActivityForResult(intent, PLACE_PICKER_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            onCaptureImageResult(data);
        }

        if (requestCode == PLACE_PICKER_CODE && resultCode == RESULT_OK && data != null) {
            Place place = PlacePicker.getPlace(this, data);

            userLocation = String.format("%s", place.getName());
            String name = String.format("%s", place.getName());
            selectedLatitute = String.valueOf(place.getLatLng().latitude);
            selectedLongitute = String.valueOf(place.getLatLng().longitude);

            selectedLocation.append(name);

        }
    }

    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        Log.d("imagetag----", bitmap.toString());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imageView.setImageBitmap(bitmap);

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

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(report_form.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(report_form.this);
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

    private void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    public String getStringImage(Bitmap bmp){
        Log.d("bmp----------------", bmp.toString());
        String encodedImage;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        byte[] imageBytes = output.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void uploadImage(){
        final String title = editTitle.getText().toString().trim();
        final String desc = description.getText().toString().trim();
        final String image = getStringImage(bitmap);

        class UploadImage extends AsyncTask<Void, Void, String>{

            ProgressDialog loading;
            Context context;
            AppConfig config;

            private UploadImage(Context context){
                this.context = context;
                loading = new ProgressDialog(context);
                config = new AppConfig();
            }

            @Override
            protected void onPreExecute() {
                Log.d("testing--------", config.URL_ReportPost.toString());
                loading.setMessage("Please wait.... Uploading");
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected void onPostExecute(String s) {
                loading.dismiss();
                Toast.makeText(report_form.this,s, Toast.LENGTH_LONG).show();
//                showMessage(s);
                Log.d("MyActivity", "Result: "+s);
//                startActivity(new Intent(report_form.this, UOCSActivity.class));
//                finish();
            }

            @Override
            protected String doInBackground(Void... params) {
                String result;

                HashMap<String,String> param = new HashMap<String, String>();
                param.put("title",title);
                param.put("description",desc);
                param.put("userID", userID);
                param.put("typeID", getIntent().getStringExtra("CATEGORY_ID"));
                param.put("location_name", userLocation);
                param.put("location_latitude", selectedLatitute);
                param.put("location_longitude", selectedLongitute);
                param.put("image",image);
                result = rh.sendPostRequest(config.URL_ReportPost, param);
                return result;
            }
        }
        UploadImage u = new UploadImage(report_form.this);
        u.execute();
    }

    public void showMessage(String result){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(true);
        alert.setTitle("Result from server");
        alert.setMessage(result);
        alert.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
