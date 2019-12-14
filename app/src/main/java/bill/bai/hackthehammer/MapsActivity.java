package bill.bai.hackthehammer;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

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
        mapObjects.add(new MapObject(
                "McMaster",
                "Bad stuff",
                "Yes",
                new LatLng(43.267165, -79.900799)
        ));
        mapObjects.add(new MapObject(
                "McMaster",
                "Bad stuff",
                "Yes",
                new LatLng(43.257165, -79.910799)
        ));
        mapObjects.add(new MapObject(
                "McMaster",
                "Bad stuff",
                "Yes",
                new LatLng(43.247165, -79.900799)
        ));

        mapObjects.add(new MapObject(
                "McMaster",
                "Bad stuff",
                "Yes",
                new LatLng(43.258165, -79.900799)
        ));
        mapObjects.add(new MapObject(
                "McMaster",
                "Bad stuff",
                "Yes",
                new LatLng(43.267165, -79.910799)
        ));
        mapObjects.add(new MapObject(
                "McMaster",
                "Bad stuff",
                "Yes",
                new LatLng(43.255165, -79.909799)
        ));
        mapObjects.add(new MapObject(
                "McMaster",
                "Bad stuff",
                "Yes",
                new LatLng(43.248165, -79.900799)
        ));

//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(43.257165, -79.900799);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("McMaster"));


        plotPoints(mMap, mapObjects);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.257165, -79.900799), 13));
        drawHeatMap(mMap, mapObjects);
    }

    public void plotPoints(GoogleMap googleMap, List<MapObject> mapObjects){
        mMap = googleMap;

        for(int x = 0; x < mapObjects.size(); x++){
            mMap.addMarker(new MarkerOptions().position(mapObjects.get(x).getLatLng()).title(mapObjects.get(x).getName()));
        }
    }


    public void reportButton(View f){
    }

    /**
     * Draws a heatmap on the map
     */
    private void drawHeatMap(GoogleMap googleMap, List<MapObject> mapObjects){
        mMap = googleMap;
        int[] gradientColors = {
                Color.rgb(245, 147, 0),
                Color.rgb(207, 0, 0)
        };
        float[] gradientStartPoints = {
                0.2f,
                1f
        };
        Gradient gradient = new Gradient(gradientColors, gradientStartPoints);

        List<LatLng> locations = new ArrayList<>();
        for(int x = 0; x < mapObjects.size(); x++){
            locations.add(mapObjects.get(x).getLatLng());
        }

        mProvider = new HeatmapTileProvider.Builder()
                .data(locations)
                .radius(50)
                .gradient(gradient)
                .build();

        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));


    }
}
