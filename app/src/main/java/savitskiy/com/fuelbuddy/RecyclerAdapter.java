package savitskiy.com.fuelbuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;


/**
 * Created by Andrey on 31.05.2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private List<GasModel> gasModels;
    private Context context;

    public RecyclerAdapter(List<GasModel> gasModels, Context context) {
        this.gasModels = gasModels;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        GasModel gasModel = gasModels.get(position);
        holder.textViewCost.setText(gasModel.getCost());
        holder.textViewHours.setText(gasModel.getHours());
        holder.textViewName.setText(gasModel.getName());
        holder.textViewAdress.setText(gasModel.getAdress());
        holder.textViewDistance.setText(gasModel.getDistance());
        holder.imageViewLogo.setImageDrawable(getDrawableIcon(gasModel));
    }


    private Drawable getDrawableIcon(GasModel gasModel){
        String uri = "@drawable/"+gasModel.getIcon(); // where myresource (without the extension) is the file
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
            textViewCost = (TextView)itemView.findViewById(R.id.textViewCost);
            textViewHours = (TextView)itemView.findViewById(R.id.textViewHours);
            textViewName = (TextView)itemView.findViewById(R.id.textViewName);
            textViewAdress = (TextView)itemView.findViewById(R.id.textViewAdress);
            textViewDistance = (TextView)itemView.findViewById(R.id.textViewDistance);
            imageViewLogo = (ImageView) itemView.findViewById(R.id.imageViewLogo);
        }
    }
}

