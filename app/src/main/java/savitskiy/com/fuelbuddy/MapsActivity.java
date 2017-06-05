package savitskiy.com.fuelbuddy;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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
import java.util.ArrayList;
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
    private ViewPager viewPager;

    private List<GasModel> gasModels;
    private Map<GasModel, Marker> map;
    private Map<Marker, GasModel> mMap;
    private ImageView imageViewMan, imageViewSettings, imageViewMarker, imageViewAdd;
    private EditText editTextSearch;
    private SectionPageAdapter sectionPageAdapter;
    private Adaptable adaptableFragment;
    private boolean fragmentAttached;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initialize google map
        initGoogleMap();


        //
        gasModels = initGasModels();

        map = new HashMap<>();
        mMap = new HashMap<>();

        bottomSheet = findViewById(R.id.bottom_sheet);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        imageViewMan = (ImageView) findViewById(R.id.imageViewMan);
        imageViewSettings = (ImageView) findViewById(R.id.imageViewSettings);
        imageViewMarker = (ImageView) findViewById(R.id.imageViewMarker);
        imageViewAdd = (ImageView) findViewById(R.id.imageViewAdd);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        tabLayout = (TabLayout) findViewById(R.id.tabs);


        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        imageViewMan.setOnClickListener(this);
        imageViewSettings.setOnClickListener(this);
        imageViewMarker.setOnClickListener(this);
        imageViewAdd.setOnClickListener(this);

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(sectionPageAdapter);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                sectionPageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (fragmentAttached) adaptableFragment.getAdapter().getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof Adaptable) {
            this.adaptableFragment = (Adaptable) fragment;
            fragmentAttached = true;
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
                    setInffoWindowClickListener(model.getName());
                }
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
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


        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (isInfoWindowShow(marker)) {
                    return false;
                } else {
                    Toast.makeText(MapsActivity.this, getResources().getString(R.string.another_region), Toast.LENGTH_SHORT).show();
                    return true;
                }

            }
        });
    }

    private void setInffoWindowClickListener(final String string) {
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapsActivity.this, string, Toast.LENGTH_SHORT).show();
            }
        });
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
            if (isInfoWindowShow(marker)) {
                marker.showInfoWindow();
            } else {
                Toast.makeText(this, getResources().getString(R.string.another_region), Toast.LENGTH_SHORT).show();
            }

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

    }

    @Override
    public List<GasModel> getModels() {
        return gasModels;
    }


    private boolean isInfoWindowShow(Marker marker) {
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> addresses = new ArrayList<Address>();
        Address address = null;
        try {
            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
            address = addresses.get(0);
            String locality = address.getLocality();
            if (getResources().getString(R.string.moscow).equals(locality)) return true;
        } catch (IOException e1) {
            e1.printStackTrace();
            Toast.makeText(this, e1.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewMan:
                Toast.makeText(this, getResources().getString(R.string.action_clicke_profile), Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageViewSettings:
                Toast.makeText(this, getResources().getString(R.string.action_clicke_settings), Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageViewMarker:
                Toast.makeText(this, getResources().getString(R.string.action_clicke_location), Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageViewAdd:
                Toast.makeText(this, getResources().getString(R.string.action_clicke_add_location), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public interface Adaptable {
        public RecyclerAdapter getAdapter();
    }

    private List<GasModel> initGasModels() {
        List<GasModel> gasModels = new ArrayList<>();
        InputStreamReader is = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                is = new InputStreamReader(getAssets()
                        .open(getResources().getString(R.string.csv_file_name)), StandardCharsets.UTF_8);
                gasModels = DataHelper.parse(is, DataHelper.DEFAULT_SEPARATOR, this);
                return gasModels;
            } else {
                Toast.makeText(this, getResources().getString(R.string.min_required_sdk), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return gasModels;
    }
}
