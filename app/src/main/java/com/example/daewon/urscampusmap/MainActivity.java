package com.example.daewon.urscampusmap;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonArray;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener

{



    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPosition;

    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    private static final String api_url = "http://172.16.17.14:8000/api/";
    public static final int REQUEST_CODE_PERMISSIONS = 1000;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    // 위치 정보 얻는 객체
    private FusedLocationProviderClient mFusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);


        Log.d(TAG, "onCreate");



        findViewById(R.id.Testbutton).setOnClickListener(
                        new Button.OnClickListener() {
                            @Override
                            public void onClick(View view) {

//                                RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), "1");

                                PostTask postTask = new PostTask("");
                                postTask.execute();


//                                Retrofit retrofit = new Retrofit.Builder()
//                                        .baseUrl("http://172.16.17.14:8000/api/")
//                                        .addConverterFactory(GsonConverterFactory.create())
//                                        .build();
//                                File logo_file = new File("res/drawable/loading.png");
//                                final String basicAuth = "Basic " + Base64.encodeToString("jara@example.com:jarajara".getBytes(), Base64.NO_WRAP);
//
//                                Map<String, RequestBody> map = new HashMap<>();
//                                map.put("university", toRequestBody("1")); // 유저아이디값(string)
//                                map.put("addr_x", toRequestBody("127.34678")); // 유저아이디값(string)
//                                map.put("addr_y", toRequestBody("36.387777")); // 유저아이디값(string)
//                                map.put("category", toRequestBody("CD")); // 유저아이디값(string)
//                                map.put("title", toRequestBody("someone Idk")); // 유저아이디값(string)
//                                map.put("comment", toRequestBody("classic agizagi")); // 유저아이디값(string)
//                                map.put("url_upload\"; filename=\"photo.png\"", RequestBody.create(MediaType.parse("image/png"), logo_file));
//                                SpotService service = retrofit.create(SpotService.class);
//                                Call<Spot> spots = service.createSpot(basicAuth,map);
//                                spots.enqueue(new Callback<Spot>(){
//                                    @Override
//                                    public void onResponse(Call<Spot> call, Response<Spot> response) {
//                                        // Code...
//                                        System.out.println("------------success----------------");
//                                    }
//                                    @Override
//                                    public void onFailure(Call<Spot> call, Throwable t) {
//                                        // Code...
//                                        System.out.println("------------failure----------------");
//
//                                    }
//                                });

                            }
                        }
        );
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

    public RequestBody toRequestBody(String value)
    {
        return RequestBody.create(MediaType.parse("text/plain"), "1");
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
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
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

            String result; // 요청 결과를 저장할 변수.
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


    public class PostTask extends AsyncTask<Void, Void, String> {

        private String url;

        public PostTask(String url) {

            this.url = url;

        }



        @Override
        protected String doInBackground(Void... params) {

            MultipartUtility multipart = null;
            Resources res =  getApplication().getApplicationContext().getResources();


            Bitmap testImg = BitmapFactory.decodeResource(res, R.drawable.loading);

            File filesDir = getApplication().getApplicationContext().getFilesDir();
            File loading_file = new File(filesDir, "loa" + ".jpg");

            OutputStream os;
            try {
                os = new FileOutputStream(loading_file);
                testImg.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
                multipart = new MultipartUtility("http://172.16.17.14:8000/api/spots/", "UTF-8");
                multipart.addFormField("university","1");
                multipart.addFormField("addr_x","127.34678");
                multipart.addFormField("addr_y","36.87777");
                multipart.addFormField("category","C");
                multipart.addFormField("title","something IDK");
                multipart.addFormField("comment","classic arigari");
                multipart.addFilePart("picture",loading_file);
                List<String> response = multipart.finish();
                System.out.println(response);
                List<String> responses = multipart.finish();
                System.out.println(responses);
                return responses.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "false";
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("-----------------------------------");
            System.out.println(s);
            System.out.println("-----------------------------------");

            super.onPostExecute(s);


        }
    }

    public class StoryPostTask extends AsyncTask<Void, Void, String> {

        private String content;
        private int spot_id;

        public StoryPostTask(int spot_id, String content) {

            this.spot_id = spot_id;
            this.content = content;

        }



        @Override
        protected String doInBackground(Void... params) {


            try {
                ;
                MultipartUtility multipart = new MultipartUtility("http://172.16.17.14:8000/api/stories/", "UTF-8");
                multipart.addFormField("spot",String.valueOf(this.spot_id));
                multipart.addFormField("content",this.content);

                List<String> responses = multipart.finish();
                return responses.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "false";
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
