package bill.bai.hackthehammer;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapObject {
    private String name;
    private String description;
    private String category;
    private LatLng latLng;
    public Marker marker;

    public MapObject(String name, String description, String category, LatLng latLng){
        this.name = name;
        this.description = description;
        this.category = category;
        this.latLng = latLng;

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
        return latLng;
    }
}
