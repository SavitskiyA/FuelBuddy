package savitskiy.com.fuelbuddy;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey on 31.05.2017.
 */

public class DataHelper {
    public final static String DEFAULT_SEPARATOR = ";";
    public static List<GasModel> parse(InputStreamReader isr, String separator, Context context) {
        List<GasModel> models = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line = "";
            boolean isTitleBar = true;
            while ((line = bufferedReader.readLine()) != null) {
                if (!isTitleBar) {
                    String[] rowArray = line.split(separator);
                    String cost = rowArray[0];
                    String hours = rowArray[1];
                    String icon = rowArray[2];
                    String name = rowArray[3];
                    String adress = rowArray[4];
                    String distance = rowArray[5];
                    double lat = Double.parseDouble(rowArray[6]);
                    double lng = Double.parseDouble(rowArray[7]);
                    GasModel gasModel = new GasModel(cost, hours, icon, name, adress, distance, lat, lng);
                    models.add(gasModel);
                }
                isTitleBar = false;
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return models;
    }
}
