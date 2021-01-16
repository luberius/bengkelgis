package com.example.bengkelgis_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String[] id, nama, telp, email, alamat, hari, jam_buka, jam_tutup, gambar, rating;
    int numData;
    LatLng[] latLng;
    LatLng myLatLng;
    Boolean[] markerD;
    private Double[] latitude, longitude;
    int REQUEST_CHECK_SETTINGS = 10;
    double distance = 0;
    double duration = 0;
    List<LatLng> coordinates;
    Polyline polylineDirection;

    // Views

    CardView card;
    TextView textNama, textAlamat, textJarakWaktu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLokasi();

        card = findViewById(R.id.card);
        textNama = findViewById(R.id.textNama);
        textAlamat = findViewById(R.id.textAlamat);
        textJarakWaktu = findViewById(R.id.textJarakWaktu);
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
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        // check location access permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CHECK_SETTINGS);
            return;
        }
        getCurrentLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "Tidak dapat mengambil lokasi sekarang. Cek akses lokasi di setting", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    //LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng currentLatLng = new LatLng(-6.1669575, 106.790465);
                    myLatLng = currentLatLng;
                    mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Lokasi saat ini"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.5f));
                    Log.d("My Current location", "Lat : " + location.getLatitude() + " Long : " + location.getLongitude());
                }
            }
        });
    }

    private void getLokasi() {
        String url = "https://api.bengkelreza.develop.syahril.dev/";
        JsonArrayRequest request = new JsonArrayRequest
                (Request.Method.GET, url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        numData = response.length();
                        Log.d("DEBUG_", "Parse JSON");
                        latLng = new LatLng[numData];
                        markerD = new Boolean[numData];
                        nama = new String[numData];
                        alamat = new String[numData];
                        telp = new String[numData];
                        email = new String[numData];
                        hari = new String[numData];
                        jam_buka = new String[numData];
                        jam_tutup = new String[numData];
                        rating = new String[numData];
                        id = new String[numData];
                        gambar = new String[numData];
                        latitude = new Double[numData];
                        longitude = new Double[numData];

                        Log.d("Map Response", "onResponse: " + numData);

                        for (int i = 0; i < numData; i++) {
                            try {
                                JSONObject data = response.getJSONObject(i);
                                id[i] = data.getString("id");
                                latLng[i] = new LatLng(data.getDouble("latitude"),
                                        data.getDouble("longitude"));
                                nama[i] = data.getString("nama");
                                alamat[i] = data.getString("alamat");
                                telp[i] = data.getString("telp");
                                email[i] = data.getString("email");
                                hari[i] = data.getString("hari");
                                jam_buka[i] = data.getString("jam_buka");
                                jam_tutup[i] = data.getString("jam_tutup");
                                rating[i] = data.getString("rating");
                                gambar[i] = data.getString("gambar");
                                latitude[i] = data.getDouble("latitude");
                                longitude[i] = data.getDouble("longitude");
                                Log.d("PRINT_TEST", "onResponse: " + id[i]);
                                markerD[i] = false;
                                mMap.addMarker(new MarkerOptions()
                                        .position(latLng[i])
                                        .title(nama[i])
                                        .snippet(alamat[i])
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_pin_red)));

                            } catch (JSONException je) {
                                Log.d("Map Response", je.getMessage());
                            }
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng[i], 15.5f));
                        }

                        //MARKER KLIK
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                resetDirection();

                                textNama.setText(marker.getTitle());
                                textAlamat.setText(marker.getSnippet());

                                if (marker.getTitle().equals("Lokasi saat ini")) {
                                    textNama.setText(null);
                                    textAlamat.setText(null);
                                    textJarakWaktu.setText(null);
                                    return true;
                                }

                                Toast.makeText(MapsActivity.this, "Memuat Jalur...", Toast.LENGTH_SHORT).show();
                                getDirection(myLatLng, marker.getPosition());
                                return true;
                            }

                        });
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        builder.setTitle("Error!");
                        builder.setMessage("No Internet Connection");
                        builder.setIcon(android.R.drawable.ic_dialog_alert);
                        builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getLokasi();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
        Volley.newRequestQueue(this).add(request);
    }

    private void getDirection(final LatLng start, final LatLng end) {
        String key = "5b3ce3597851110001cf6248756f97c098c345c9826ad8f9182537c6";
        String url = "https://api.openrouteservice.org/v2/directions/driving-car?" +
                "api_key=" + key +
                "&start=" + start.longitude + "," + start.latitude +
                "&end=" + end.longitude + "," + end.latitude;

        coordinates = new ArrayList<>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Direction response", response.toString());
                    JSONArray features = response.getJSONArray("features");
                    if (features.length() > 0) {
                        JSONObject summary = features.getJSONObject(0).getJSONObject("properties").getJSONObject("summary");
                        JSONArray coors = features.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");
                        distance = summary.getDouble("distance");
                        duration = summary.getDouble("duration");

                        for (int i = 0; i < coors.length(); i++) {
                            JSONArray coor = coors.getJSONArray(i);
                            coordinates.add(new LatLng(coor.getDouble(1), coor.getDouble(0)));
                        }

                        drawDirection();

                        LatLngBounds.Builder latLongBuilder = new LatLngBounds.Builder();
                        latLongBuilder.include(start);
                        latLongBuilder.include(end);

                        LatLngBounds bounds = latLongBuilder.build();

                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels - (card.getHeight() * 2);
                        int paddingMap = (int) (width * 0.2);

                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, paddingMap);
                        mMap.animateCamera(cu);
                    } else {
                        Toast.makeText(MapsActivity.this, "Gagal mendapakan jalur", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Error!");
                builder.setMessage("No Internet Connection");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getLokasi();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    private void drawDirection() {
        polylineDirection = mMap.addPolyline(new PolylineOptions()
                .addAll(coordinates)
                .width(8f)
                .color(Color.argb(255, 56, 167, 252)));

        String textDistance = round(distance) + " m";
        String textDuration = round(duration) + " dtk";

        if (distance > 999) {
            textDistance = round((distance / 1000)) + " km";
        }

        if (duration > 60) {
            duration = duration / 60;
            textDuration = round(duration) + " mnt";
            if (duration > 60) {
                int tempDur = (int) duration / 60;
                double mnt = duration - (tempDur * 60);

                textDuration = round(tempDur) + " jam " + round(mnt) + " mnt";
            }
        }

        String finalText = textDistance + ", " + textDuration;
        textJarakWaktu.setText(finalText);
    }

    private void resetDirection() {
        if (polylineDirection != null) {
            polylineDirection.remove();
        }
    }

    public static double round(double value) {
        double scale = Math.pow(10, 2);
        return Math.round(value * scale) / scale;
    }
}
