package bill.bai.hackthehammer;

import com.google.android.gms.maps.GoogleMap;

public class MapObject {
    private GoogleMap mapObj;
    private String name;
    private String description;

    public MapObject(GoogleMap mapObj, String name, String description){
        this.mapObj = mapObj;
        this.name = name;
        this.description = description;


    }
}
