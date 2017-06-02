package savitskiy.com.fuelbuddy;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private View bottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private GoogleMap mGoogleMap;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private List<GasModel> gasModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initGoogleMap();
        initGasModels();

        bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerAdapter = new RecyclerAdapter(gasModels, this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.byDistance)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.byCost)));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(MapsActivity.this, "" + tab.getPosition(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Method initialize models of Gas stations. All data is pre-defined and downloaded from .csv file
     */
    private void initGasModels() {
        InputStreamReader is = null;
        try {
            is = new InputStreamReader(getAssets()
                    .open(getResources().getString(R.string.fileName)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        gasModels = DataHelper.parse(is, DataHelper.DEFAULT_SEPARATOR, this);
    }

    /**
     * Obtain the SupportMapFragment and get notified when the map is ready to be used.
     */
    private void initGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mGoogleMap = googleMap;
        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window_layout,null);
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window_layout,null);
                return v;
            }
        });

        try {
            goToLocationZoom(55.7563109, 37.6151827, 16);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void goToLocation(double lat, double lng) throws IOException {
        LatLng ll = new LatLng(lat, lng);
        mGoogleMap.addMarker(new MarkerOptions().position(ll).title(getStringLocationFromLatLng(lat, lng)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
    }

    private void goToLocationZoom(double lat, double lng, float zoom) throws IOException {
        LatLng ll = new LatLng(lat, lng);
        setMarker(lat, lng);
//        mGoogleMap.addMarker(new MarkerOptions().position(ll).title(getStringLocationFromLatLng(lat, lng)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom));
    }

    private String getStringLocationFromLatLng(double lat, double lng) throws IOException {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
        Address address = addresses.get(0);
        String location = address.getLocality();
        return location;
    }

    private void setMarker(double lat, double lng) throws IOException {
        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.anchor(2,3);
//        markerOptions.infoWindowAnchor(2,3);
        markerOptions.title(getStringLocationFromLatLng(lat, lng));
        markerOptions.position(new LatLng(lat, lng));
        mGoogleMap.addMarker(markerOptions);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isGoogleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.playServiceError), Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}
