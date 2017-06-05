package savitskiy.com.fuelbuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Created by Andrey on 31.05.2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> implements Filterable {
    private List<GasModel> gasModels;
    private List<GasModel> filteredGasModels;
    private Context context;
    private MarkerListener markerListener;
    private CustomFilter customFilter;

    public RecyclerAdapter(List<GasModel> gasModels, Context context, MarkerListener markerListener) {
        this.gasModels = gasModels;
        this.filteredGasModels = gasModels;
        this.context = context;
        this.markerListener = markerListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        GasModel gasModel = gasModels.get(position);
        holder.textViewCost.setText(gasModel.getCost());
        holder.textViewHours.setText(gasModel.getHours());
        holder.textViewName.setText(gasModel.getName());
        holder.textViewAdress.setText(gasModel.getAdress());
        holder.textViewDistance.setText(gasModel.getDistance());
        holder.imageViewLogo.setImageDrawable(getDrawableIcon(gasModel, context));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerListener.onMarkerClick(position);
            }
        });
    }


    public interface MarkerListener {
        public void onMarkerClick(int position);
        public List<GasModel> getModels();
    }

    public static Drawable getDrawableIcon(GasModel gasModel, Context context) {
        String uri = "@drawable/" + gasModel.getIcon(); // where myresource (without the extension) is the file
        int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
        Drawable res = context.getResources().getDrawable(imageResource);
        return res;

    }

    @Override
    public int getItemCount() {
        return gasModels.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewCost, textViewHours, textViewName, textViewAdress, textViewDistance;
        private ImageView imageViewLogo;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            textViewCost = (TextView) itemView.findViewById(R.id.textViewCost);
            textViewHours = (TextView) itemView.findViewById(R.id.textViewHours);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewAdress = (TextView) itemView.findViewById(R.id.textViewAdress);
            textViewDistance = (TextView) itemView.findViewById(R.id.textViewDistance);
            imageViewLogo = (ImageView) itemView.findViewById(R.id.imageViewLogo);
        }
    }


    @Override
    public Filter getFilter() {
        if (customFilter == null) {
            customFilter = new CustomFilter();
        }
        return customFilter;
    }

    class CustomFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                constraint = constraint.toString().toUpperCase();
                ArrayList<GasModel> filters = new ArrayList<>();
                for (int i = 0; i < filteredGasModels.size(); i++) {
                    if (filteredGasModels.get(i).getAdress().toUpperCase().contains(constraint)) {
                        filters.add(new GasModel(filteredGasModels.get(i).getCost(), filteredGasModels.get(i).getHours(), filteredGasModels.get(i).getIcon(), filteredGasModels.get(i).getName(), filteredGasModels.get(i).getAdress(), filteredGasModels.get(i).getDistance(), filteredGasModels.get(i).getLat(), filteredGasModels.get(i).getLng()));
                    }
                    if (filteredGasModels.get(i).getName().toUpperCase().contains(constraint)) {
                        filters.add(new GasModel(filteredGasModels.get(i).getCost(), filteredGasModels.get(i).getHours(), filteredGasModels.get(i).getIcon(), filteredGasModels.get(i).getName(), filteredGasModels.get(i).getAdress(), filteredGasModels.get(i).getDistance(), filteredGasModels.get(i).getLat(), filteredGasModels.get(i).getLng()));
                    }
                }
                filterResults.count = filters.size();
                filterResults.values = filters;
            } else {
                filterResults.count = filteredGasModels.size();
                filterResults.values = filteredGasModels;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            gasModels = (ArrayList<GasModel>) results.values;
            notifyDataSetChanged();
        }
    }


}