package bill.bai.hackthehammer;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private boolean isHeatMap = false;

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

        // The emergency
        mapObjects.add(new MapObject(
                "Possible Gunshots",
                "Gunshots have been heard near Main St West & Osler Dr, please be extremely cautious. One person seen laying on the ground",
                "Emergency",
                new LatLng(43.258296, -79.935160)
        ));
        mapObjects.add(new MapObject(
                "Gunshots Heard",
                "Gunshots heard at Main St West & Osler Dr. Injured woman here!",
                "Emergency",
                new LatLng(43.257496, -79.935150)
        ));
        mapObjects.add(new MapObject(
                "Woman shot",
                "Gunshots have been heard near Main St West & Osler Dr, please be extremely cautious. One person seen laying on the ground",
                "Emergency",
                new LatLng(43.257286, -79.936150)
        ));
        mapObjects.add(new MapObject(
                "Heard gunshots",
                "I heard gunshots near Main St West & Osler Dr. No authorities shown up, be safe.",
                "Emergency",
                new LatLng(43.257796, -79.935150)
        ));
        mapObjects.add(new MapObject(
                "Gunshots WARNING",
                "Heard 3 or so gunshots near Main St West & Osler Dr. Injured lady near scene, no police!",
                "Emergency",
                new LatLng(43.255286, -79.936150)
        ));

        // Criminal Activity
        mapObjects.add(new MapObject(
                "Heist?",
                "Jewelery Heist near McMaster MIP. Robbers wearing black masks and baggy clothing.",
                "Criminal Activity",
                new LatLng(43.267165, -79.904799)
        ));
        mapObjects.add(new MapObject(
                "Burning Building",
                "TD Bank Building is engulfed in fire, first responders haven't reached the scene yet. Take caution",
                "Fire",
                new LatLng(43.263290, -79.902583)
        ));
        mapObjects.add(new MapObject(
                "McMaster",
                "3",
                "Natural",
                new LatLng(43.247165, -79.900799)
        ));

        mapObjects.add(new MapObject(
                "McMaster",
                "4",
                "Other",
                new LatLng(43.258165, -79.900799)
        ));
        mapObjects.add(new MapObject(
                "McMaster",
                "5",
                "Other",
                new LatLng(43.267165, -79.910799)
        ));
        mapObjects.add(new MapObject(
                "McMaster",
                "6",
                "Other",
                new LatLng(43.255165, -79.909799)
        ));
        mapObjects.add(new MapObject(
                "McMaster",
                "aaa",
                "Other",
                new LatLng(43.248165, -79.900799)
        ));

//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(43.257165, -79.900799);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("McMaster"));


        // plotPoints(mMap, mapObjects);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.257165, -79.900799), 13));
        drawHeatMap(mMap, mapObjects);
    }

    public void plotPoints(GoogleMap googleMap, List<MapObject> mapObjects){
        mMap = googleMap;

        for(int x = 0; x < mapObjects.size(); x++){
            float color = 0;
            if(mapObjects.get(x).getCategory().equals("Emergency")) {
                color = BitmapDescriptorFactory.HUE_RED;
            }else if(mapObjects.get(x).getCategory().equals("Criminal Activity")){
                color = BitmapDescriptorFactory.HUE_ORANGE;
            }else if(mapObjects.get(x).getCategory().equals("Fire")){
                color = BitmapDescriptorFactory.HUE_VIOLET;
            }else if(mapObjects.get(x).getCategory().equals("Natural")){
                color = BitmapDescriptorFactory.HUE_BLUE;
            }else{
                color = BitmapDescriptorFactory.HUE_AZURE;
            }


            Marker marker = mMap.addMarker(new MarkerOptions().
                    position(mapObjects.get(x)
                            .getLatLng())
                    .title(mapObjects
                            .get(x)
                            .getName())
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(color)
                    )
            );

            mapObjects.get(x).marker = marker;
        }

        mMap.setOnMarkerClickListener(this);
    }


    public void reportButton(View f){
        Intent intent = new Intent(MapsActivity.this, ReportActivity.class);
        startActivityForResult(intent, 0);
    }

    public void toggleButton(View f){
        isHeatMap = !isHeatMap;
        mMap.clear();
        if(isHeatMap){
            plotPoints(mMap, mapObjects);
        }else{
            drawHeatMap(mMap, mapObjects);
        }
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
                .radius(30)
                .gradient(gradient)
                .build();

        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Resources res = getResources();
        String text = " ";
        String title = " ";
        String cat = " ";
        for(int x = 0; x < mapObjects.size(); x++){
            if(marker.getPosition().equals(mapObjects.get(x).getLatLng())){
                text = (mapObjects.get(x).getDescription());
                title = mapObjects.get(x).getName();
                cat = mapObjects.get(x).getCategory();
                break;
            }
        }

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        onButtonShowPopupWindow(viewGroup, text, title, cat);



        return false;
    }

    public void onButtonShowPopupWindow(View view, String text, String title, String cat){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        TextView t = popupView.findViewById(R.id.popuptext);
        t.setText(text);
        TextView e = popupView.findViewById(R.id.title);
        e.setText(title);
        TextView x = popupView.findViewById(R.id.category);
        x.setText(cat);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
