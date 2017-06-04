package savitskiy.com.fuelbuddy;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, RecyclerAdapter.MarkerListener, View.OnClickListener {
    private View bottomSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private GoogleMap mGoogleMap;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private List<GasModel> gasModels;
    private Map<GasModel, Marker> map;
    private Map<Marker, GasModel> mMap;
    private ImageView imageViewMan, imageViewSettings, imageViewMarker, imageViewAdd;
    private EditText editTextSearch;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Initialize google map
        initGoogleMap();
        //get gas list of gas models from .csv file
        initGasModels();
//sort gas model list befor adapter downloading
        Collections.sort(gasModels, sortByDistance);

        map = new HashMap<>();
        mMap = new HashMap<>();

        bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        imageViewMan = (ImageView) findViewById(R.id.imageViewMan);
        imageViewSettings = (ImageView) findViewById(R.id.imageViewSettings);
        imageViewMarker = (ImageView) findViewById(R.id.imageViewMarker);
        imageViewAdd = (ImageView) findViewById(R.id.imageViewAdd);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);

        imageViewMan.setOnClickListener(this);
        imageViewSettings.setOnClickListener(this);
        imageViewMarker.setOnClickListener(this);
        imageViewAdd.setOnClickListener(this);

        if (gasModels != null) {
            recyclerAdapter = new RecyclerAdapter(gasModels, this, this);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(recyclerAdapter);
        } else {
            Toast.makeText(this, getResources().getString(R.string.gas_data_source_error), Toast.LENGTH_SHORT).show();
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.by_distance)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.by_cost)));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        Collections.sort(gasModels, sortByDistance);
                        recyclerAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        Collections.sort(gasModels, sortByCost);
                        recyclerAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MapsActivity.this.recyclerAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public static final Comparator<GasModel> sortByDistance = new Comparator<GasModel>() {
        @Override
        public int compare(GasModel o1, GasModel o2) {
            return Double.compare(Double.parseDouble(o1.getDistance()), Double.parseDouble(o2.getDistance()));
        }
    };


    public static final Comparator<GasModel> sortByCost = new Comparator<GasModel>() {
        @Override
        public int compare(GasModel o1, GasModel o2) {
            return Double.compare(Double.parseDouble(o1.getCost()), Double.parseDouble(o2.getCost()));
        }
    };


    /**
     * Method initialize models of Gas stations. All data is pre-defined and downloaded from .csv file
     */
    private void initGasModels() {
        InputStreamReader is = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                is = new InputStreamReader(getAssets()
                        .open(getResources().getString(R.string.csv_file_name)), StandardCharsets.UTF_8);
                gasModels = DataHelper.parse(is, DataHelper.DEFAULT_SEPARATOR, this);
            } else {
                Toast.makeText(this, getResources().getString(R.string.min_required_sdk), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

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
                View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                TextView textViewStreet = (TextView) v.findViewById(R.id.textViewStreet);
                TextView textViewPrice = (TextView) v.findViewById(R.id.textViewPrice);
                ImageView imageViewIcon = (ImageView) v.findViewById(R.id.imageViewIcon);
                GasModel model = mMap.get(marker);
                if (model != null) {
                    String street = model.getAdress();
                    String price = model.getCost();
                    textViewStreet.setText(street);
                    textViewPrice.setText(price);
                    imageViewIcon.setBackgroundDrawable(RecyclerAdapter.getDrawableIcon(model, MapsActivity.this));
                }



                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
//                View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);
//                ImageView imageViewHeart = (ImageView) v.findViewById(R.id.imageViewHeart);
//                TextView textViewStreet = (TextView) v.findViewById(R.id.textViewStreet);
//                TextView textViewPrice = (TextView) v.findViewById(R.id.textViewPrice);
//                String street = marker.getTitle();
//                textViewStreet.setText(street);
//                mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                    @Override
//                    public void onInfoWindowClick(Marker marker) {
//                        Toast.makeText(MapsActivity.this, "", Toast.LENGTH_SHORT).show();
//                    }
//                });
                return null;
            }

        });


        try {
            goToLocationZoom(55.754284, 37.620125, 11);
            for (GasModel gasModel : gasModels) {
                Marker marker = setMarker(gasModel.getLat(), gasModel.getLng(), gasModel.getAdress());
                map.put(gasModel, marker);
                mMap.put(marker, gasModel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void goToLocation(double lat, double lng) throws IOException {
        LatLng ll = new LatLng(lat, lng);
        mGoogleMap.addMarker(new MarkerOptions().position(ll).title(getStringLocationFromLatLng(lat, lng)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
    }

    private void goToLocationZoom(double lat, double lng, float zoom) throws IOException {
        LatLng ll = new LatLng(lat, lng);
        setMarker(lat, lng, getStringLocationFromLatLng(lat, lng));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, zoom));
    }

    private String getStringLocationFromLatLng(double lat, double lng) throws IOException {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
        Address address = addresses.get(0);
        String location = address.getLocality();
        return location;
    }

    public Marker setMarker(double lat, double lng, String address) throws IOException {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gas_station));
        markerOptions.infoWindowAnchor(3, 0);
        markerOptions.title(address);

        markerOptions.position(new LatLng(lat, lng));
        return mGoogleMap.addMarker(markerOptions);
    }


    @Override
    public void onMarkerClick(int position) {
        Marker marker = map.get(gasModels.get(position));

        if (marker != null) {
            marker.showInfoWindow();
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        }
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onClick(View v) {
switch(v.getId()){
    case R.id.imageViewMan:
        Toast.makeText(this, getResources().getString(R.string.dummy_action), Toast.LENGTH_SHORT).show();
        break;
    case R.id.imageViewSettings:
        Toast.makeText(this, getResources().getString(R.string.dummy_action), Toast.LENGTH_SHORT).show();
        break;
    case R.id.imageViewMarker:
        Toast.makeText(this, getResources().getString(R.string.dummy_action), Toast.LENGTH_SHORT).show();
        break;
    case R.id.imageViewAdd:
        Toast.makeText(this, getResources().getString(R.string.dummy_action), Toast.LENGTH_SHORT).show();
        break;
    default:
        break;
}
    }
}
