package com.example.daewon.urscampusmap;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener

{

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    String result;
    private AppCompatActivity mActivity;
    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    private static final String api_url = "http://172.16.17.14:8000/api/";
    public static final int REQUEST_CODE_PERMISSIONS = 1000;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    Button restaurant, cafe, drink, place;

    // 위치 정보 얻는 객체
    private FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);


        drink = findViewById(R.id.drink);
        place = findViewById(R.id.place);

        findViewById(R.id.restaurant).setOnClickListener(
                new Button.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray data = jsonObject.getJSONArray("results");
                            int list_cnt = jsonObject.getInt("count");
                            Double[] latitude = new Double[data.length()];
                            Double[] logitude = new Double[data.length()];
                            int[] id = new int[data.length()];
                            String[] name = new String[data.length()];
                            System.out.println(data.length());
                            for(int i=0; i < data.length(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                latitude[i] = object.getDouble("addr_y");
                                logitude[i] = object.getDouble("addr_x");
                                id[i] = object.getInt("id");
                                name[i] = object.getString("title");
                            }
                            for(int i =0; i< data.length(); i++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions
                                        .position(new LatLng(latitude[i],logitude[i]))
                                        .title(name[i]);
                                mMap.addMarker(markerOptions);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        findViewById(R.id.cafe).setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray data = jsonObject.getJSONArray("results");
                            int list_cnt = jsonObject.getInt("count");
                            Double[] latitude = new Double[data.length()];
                            Double[] logitude = new Double[data.length()];
                            int[] id = new int[data.length()];
                            String[] name = new String[data.length()];
                            System.out.println(data.length());
                            for(int i=0; i < data.length(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                latitude[i] = object.getDouble("addr_y");
                                logitude[i] = object.getDouble("addr_x");
                                id[i] = object.getInt("id");
                                name[i] = object.getString("title");
                            }
                            for(int i =0; i< data.length(); i++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions
                                        .position(new LatLng(latitude[i],logitude[i]))
                                        .title(name[i]);
                                mMap.addMarker(markerOptions);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        Log.d(TAG, "onCreate");
        // GoogleAPIClient의 인스턴스 생성
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
               .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }


    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onLastLocationButtonClicked(View view) {
        // 권한 체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSIONS);
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    // 현재 위치
                    //LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng myLocation = new LatLng(36.370298, 127.345933); // 임시로 대전디폴트
                    mMap.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("현재 위치"));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));


                    // 카메라 줌
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 체크 거부 됨", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        NetworkTask networkTask = new NetworkTask(api_url+"spots/", null);
        networkTask.execute();
    }


    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            // 요청 결과를 저장할 변수.
            RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("-----------------------------------");
            System.out.println(s);
            System.out.println("-----------------------------------");

            super.onPostExecute(s);

        }
    }
}
