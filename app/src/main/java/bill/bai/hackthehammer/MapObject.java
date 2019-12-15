package bill.bai.hackthehammer;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapObject {
    private String name;
    private String description;
    private String category;

    private double latitude;
    private double longitude;

    public Marker marker;


    public MapObject(String name, String description, String category, LatLng latLng){
        this.name = name;
        this.description = description;
        this.category = category;
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public MapObject(String name, String description, String category, double latitude, double longitude){
        this.name = name;
        this.description = description;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
}
