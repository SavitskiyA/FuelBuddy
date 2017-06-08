package savitskiy.com.fuelbuddy;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Andrey on 05.06.2017.
 */

public class FragmentListByDistance extends Fragment implements MapsActivity.FragmentFilterable{

    private RecyclerView recyclerView;

    public RecyclerAdapter getRecyclerAdapter() {
        return recyclerAdapter;
    }

    private RecyclerAdapter.MarkerListener markerListener;

    private RecyclerAdapter recyclerAdapter;
    private static final Comparator<GasModel> sortByDistance = new Comparator<GasModel>() {
        @Override
        public int compare(GasModel o1, GasModel o2) {
            return Double.compare(Double.parseDouble(o1.getDistance()), Double.parseDouble(o2.getDistance()));
        }
    };

    private List<GasModel> gasModels;

    public FragmentListByDistance() {
        this.gasModels=new ArrayList<GasModel>();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_by_distance_layout, null);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

//        gasModels=initGasModels();
        if (markerListener != null && gasModels != null) {
            Collections.sort(gasModels, sortByDistance);
            recyclerAdapter = new RecyclerAdapter(gasModels, getContext(), markerListener);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(recyclerAdapter);
                    } else {
            Toast.makeText(getContext(), getResources().getString(R.string.fragment_attach_error), Toast.LENGTH_SHORT).show();
        }

        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof RecyclerAdapter.MarkerListener) {
            markerListener = (RecyclerAdapter.MarkerListener) activity;
            this.gasModels.addAll(markerListener.getModels());
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.fragment_attach_error), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void filter(CharSequence query) {
        recyclerAdapter.getFilter().filter(query);
    }
}
