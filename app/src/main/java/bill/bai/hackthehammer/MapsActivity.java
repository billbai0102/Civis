package bill.bai.hackthehammer;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //TODO: Yes
    public static ArrayList<MapObject> mapObjects = new ArrayList<MapObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
        mMap = googleMap;

        mapObjects.add(new MapObject(
                "McMaster",
                "Bad stuff",
                "Yes",
                new LatLng(43.257165, -79.900799)
        ));

//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(43.257165, -79.900799);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("McMaster"));


        plotPoints(mMap, mapObjects);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.257165, -79.900799), 13));
    }

    public void plotPoints(GoogleMap googleMap, List<MapObject> mapObjects){
        mMap = googleMap;

        for(int x = 0; x < mapObjects.size(); x++){
            mMap.addMarker(new MarkerOptions().position(mapObjects.get(x).getLatLng()).title(mapObjects.get(x).getName()));
        }
    }

    public void reportButton(View f){
        Intent intent = new Intent(MapsActivity.this, ReportActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * Draws a heatmap on the map
     */
    private void drawHeatMap(GoogleMap googleMap){

    }
}
