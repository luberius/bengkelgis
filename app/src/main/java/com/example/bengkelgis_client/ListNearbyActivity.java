package com.example.bengkelgis_client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListNearbyActivity extends AppCompatActivity implements ListNearbyAdapter.OnItemClickListener {

    private ListView listView;
    String HTTP_JSON_URL = "https://api.bengkelreza.develop.syahril.dev/";
    String Id_Json = "id";
    String Image_Name_JSON = "nama";
    String Image_URL_JSON = "gambar";
    String Telp_JSON = "telp";
    String Email_JSON = "email";

    JsonArrayRequest RequestOfJSonArray;
    RequestQueue requestQueue;
    ArrayList<DataAdapter> ListOfdataAdapter;
    Button button;
    EditText editName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_nearby);

        listView = findViewById(R.id.list);
        button = findViewById(R.id.button);
        editName = findViewById(R.id.editName);

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);
        HTTP_JSON_URL += ("?lat=" + lat + "&lng=" + lng);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HTTP_JSON_URL += ("&search=" + editName.getText().toString());
                Toast.makeText(ListNearbyActivity.this, "Mencari ...", Toast.LENGTH_SHORT).show();
                JSON_HTTP_CALL();
            }
        });

        JSON_HTTP_CALL();
    }

    public void JSON_HTTP_CALL() {
        ListOfdataAdapter = new ArrayList<>();
        RequestOfJSonArray = new JsonArrayRequest(HTTP_JSON_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        ParseJSonResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue = Volley.newRequestQueue(ListNearbyActivity.this);
        requestQueue.add(RequestOfJSonArray);
    }

    public void ParseJSonResponse(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {

            DataAdapter GetDataAdapter2 = new DataAdapter();

            JSONObject json = null;
            try {

                json = array.getJSONObject(i);

                GetDataAdapter2.setImageTitle(json.getString(Image_Name_JSON));

                GetDataAdapter2.setImageUrl(json.getString(Image_URL_JSON));

                GetDataAdapter2.setTelp(json.getString(Telp_JSON));
                GetDataAdapter2.setEmail(json.getString(Email_JSON));
                GetDataAdapter2.setId(json.getString(Id_Json));
                GetDataAdapter2.setAlamat(json.getString("alamat"));
                GetDataAdapter2.setLatitude(json.getString("latitude"));
                GetDataAdapter2.setLongitude(json.getString("longitude"));
                GetDataAdapter2.setDistance(json.getDouble("distance"));

            } catch (JSONException e) {

                e.printStackTrace();
            }
            ListOfdataAdapter.add(GetDataAdapter2);
        }

        listView.setAdapter(new ListNearbyAdapter(this, ListOfdataAdapter, this));
    }

    @Override
    public void onItemClick(DataAdapter data, int pos) {
        Log.d("LIST NEARBY", data.toString());
        Intent intent = new Intent();
        intent.putExtra("lat", data.getLatitude());
        intent.putExtra("lng", data.getLongitude());
        intent.putExtra("nama", data.getImageTitle());
        intent.putExtra("alamat", data.getAlamat());
        setResult(RESULT_OK, intent);
        finish();
    }
}