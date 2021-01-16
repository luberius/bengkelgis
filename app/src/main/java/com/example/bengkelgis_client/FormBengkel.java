package com.example.bengkelgis_client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class FormBengkel extends AppCompatActivity {

    Button btn_submit, btn_take;
    EditText fullName, userEmailId, mobileNumber, address,openday,start,close,longitude,latitude;
    private static String HOST = "http://192.168.1.9/bengkel_gis_api/Bengkel_api/";
    public static String SAVE_DATA   = HOST + "AddData";
    CircleImageView profile_image;

    private final int GALLERY = 1;

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String KEY_FULLNAME = "fullname";
    private String KEY_EMAIL    = "email";
    private String KEY_ADDRESS = "address";
    private String KEY_LONGITUDE = "longitude";
    private String KEY_LATITUDE = "latitude";
    private String KEY_MOBILE = "mobile";

    private String KEY_OPENDAY = "openday";
    private String KEY_START    = "start";
    private String KEY_CLOSE = "close";

    Bitmap bitmap;
    RequestQueue rQueue;
    JSONObject jsonObject;

    Uri file;

    boolean is_poto=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_bengkel);

        fullName = findViewById(R.id.fullName);
        userEmailId = findViewById(R.id.userEmailId);
        mobileNumber = findViewById(R.id.mobileNumber);
        address = findViewById(R.id.address);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        openday = findViewById(R.id.txtopenday);
        start   = findViewById(R.id.txtjambuka);
        close   = findViewById(R.id.txtjamtutup);
        profile_image = findViewById(R.id.profile_image);

        btn_take = findViewById(R.id.btn_take);
        btn_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fullName.getText().toString().trim().length() == 0) {
                    fullName.setError("Please Enter Fullname");
                }else if (userEmailId.getText().toString().trim().length() == 0){
                    userEmailId.setError("Please Enter Your Email");
                }else if (mobileNumber.getText().toString().trim().length() == 0){
                    mobileNumber.setError("Please Enter Your Mobile Number");
                }else if (address.getText().toString().trim().length() == 0){
                    address.setError("Please Enter Your Address");
                }else if (longitude.getText().toString().trim().length() == 0){
                    longitude.setError("Please Enter Your Longitude");
                }else if (latitude.getText().toString().trim().length() == 0){
                    latitude.setError("Please Enter Your Latitude");
                }else if (openday.getText().toString().trim().length() == 0){
                    openday.setError("Please Enter Your Openday");
                }else if (start.getText().toString().trim().length() == 0){
                    start.setError("Please Enter Your start time");
                }else if (close.getText().toString().trim().length() == 0){
                    close.setError("Please Enter Your close time");
                }
                else{
                    if (is_poto){
                        SaveData(bitmap);
                    }else{
                        Toast.makeText(getApplicationContext(), "Poto belum diambil", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(FormBengkel.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file = Uri.fromFile(getOutputMediaFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                    startActivityForResult(intent, 100);
                    is_poto = true;

                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, GALLERY);
                    is_poto = true;

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    profile_image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profile_image.setImageURI(file);
            }
        }
    }

    private void SaveData(Bitmap bitmap){
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Mohon tunggu...", false, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        try {
            jsonObject = new JSONObject();
            String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
            jsonObject.put("name", imgname);
            jsonObject.put("image", encodedImage);
            jsonObject.put(KEY_FULLNAME, fullName.getText().toString().trim());
            jsonObject.put(KEY_EMAIL, userEmailId.getText().toString().trim());
            jsonObject.put(KEY_MOBILE, mobileNumber.getText().toString().trim());
            jsonObject.put(KEY_ADDRESS, address.getText().toString().trim());
            jsonObject.put(KEY_LONGITUDE, longitude.getText().toString().trim());
            jsonObject.put(KEY_LATITUDE, latitude.getText().toString().trim());
            jsonObject.put(KEY_OPENDAY, openday.getText().toString().trim());
            jsonObject.put(KEY_START, start.getText().toString().trim());
            jsonObject.put(KEY_CLOSE, close.getText().toString().trim());

        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SAVE_DATA, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("aaaaaaa", jsonObject.toString());
                        rQueue.getCache().clear();
                        try {
                            Toast.makeText(getApplication(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loading.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("aaaaaaa", volleyError.toString());
                loading.dismiss();
            }
        });

        rQueue = Volley.newRequestQueue(getBaseContext());
        rQueue.add(jsonObjectRequest);

    }
}
