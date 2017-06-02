package savitskiy.com.fuelbuddy;

/**
 * Created by Andrey on 31.05.2017.
 */

public class GasModel {
    private String cost, hours, icon, name, adress, distance;
    private double lat, lng;

    public GasModel() {
    }

    public GasModel(String cost, String hours, String icon, String name, String adress, String distance, double lat, double lng) {
        this.cost = cost;
        this.hours = hours;
        this.icon = icon;
        this.name = name;
        this.adress = adress;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
    }

    public String getCost() {
        return cost;
    }

    public String getHours() {
        return hours;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getAdress() {
        return adress;
    }

    public String getDistance() {
        return distance;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GasModel gasModel = (GasModel) o;

        if (Double.compare(gasModel.lat, lat) != 0) return false;
        return Double.compare(gasModel.lng, lng) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lng);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
