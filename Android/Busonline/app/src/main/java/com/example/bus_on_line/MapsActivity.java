package com.example.bus_on_line;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private LocationRequest locationRequest;
    GoogleApiClient googleApiClient;
    LatLng bus_location;
    int count=0;
    TextView crowdCountM,crowdCountf,female;
    LinearLayout swipe_layoutMale,swipe_layoutFemale,swipe_layout;
    int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        //locationButton.setForegroundGravity(Gravity.BOTTOM);
        //locationButton.gra
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom

        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 100);
        mapFragment.getMapAsync(this);
        swipe_layoutMale=findViewById(R.id.swipelayoutMale);
        swipe_layoutMale.setY(2000);

        swipe_layoutFemale=findViewById(R.id.swipelayoutFemale);
        swipe_layoutFemale.setY(2000);

        female=findViewById(R.id.female);
        crowdCountM=findViewById(R.id.crowdCountmale);
        crowdCountf=findViewById(R.id.crowdCountFemale);

        Log.i("yoyoyooy",String.valueOf(getIntent().getStringExtra("gender")));
        if(getIntent().getStringExtra("gender").equals("Male"))
            swipe_layout=swipe_layoutMale;
        else swipe_layout=swipe_layoutFemale;

        swipe_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==1){
                swipe_layout.animate().y(1200);
                flag=2;
                }
                else if(flag==2){
                    swipe_layout.animate().y(2000);
                    flag=0;
                }
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

        // Add a marker in Sydney and move the camera
        bulidgoogleApiClient();
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        if(count==0){
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        count++;
        }
        //mMap.moveCamera(Ca);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Log.i("haa haa","heyy i am here");

        if(count==1){
            count++;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
        }

        DataModel dataModel = getdata();

       /* mMap.addMarker(new MarkerOptions()
                .position(new LatLng(dataModel.getLatitude(),dataModel.longitude))
                .title("Bus is here  females : "+dataModel.getFemalecount()+" \n  males : "+dataModel.getMalecount()));
*/
    }

    protected synchronized void bulidgoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }
    DataModel data ;
    Marker bus_marker;
    DataModel getdata() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        Api api = retrofit.create(Api.class);



        Call<DataModel> call= api.getData();
        call.enqueue(new Callback<DataModel>() {
            @Override
            public void onResponse(Call<DataModel> call, Response<DataModel> response) {
                data=response.body();
                Log.i("latitude  longitude", data.getLatitude()+" -- "+data.getLongitude());
                if(bus_marker!=null)
                    bus_marker.remove();

                bus_marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(data.getLatitude(),data.getLongitude())).anchor(0.5f,0.5f).title("BUS ID : 0121512AVX").snippet(" Click to know more")
                        );
                bus_marker.showInfoWindow();
                bus_marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.bus1_foreground));
                String maleS=data.getMalecount();
                String femaleS=data.getFemalecount();
                int crowd = Integer.parseInt(maleS)+Integer.parseInt(femaleS);

                    crowdCountM.setText(crowd+"");
                    crowdCountf.setText(crowd+"");
                    female.setText(femaleS);


                //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(28.483995,77.074197)));


                //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(data.getLatitude(),data.getLongitude())));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            }

            @Override
            public void onFailure(Call<DataModel> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                Log.i("errorssssss",t.getMessage());
            }
        });

        return data;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        swipe_layout.animate().y(1670);
        flag=1;
      //  Toast.makeText(this,"heyyy broooo",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(flag==0)
            super.onBackPressed();
        else
        {swipe_layout.animate().y(2000);
        flag=0;}
    }
}
