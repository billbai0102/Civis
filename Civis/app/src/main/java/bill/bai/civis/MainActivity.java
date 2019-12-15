package bill.bai.civis;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.RequiresApi;

import android.util.Log;

import androidx.core.view.GravityCompat;

import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.*;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // This is called upon application start
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // App startup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fetch API data
        System.out.println("Fetching API data");
        fetchAPIData();


        // What does this do?
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            Log.e("newToken", newToken);
            this.getPreferences(Context.MODE_PRIVATE).edit().putString("fb", newToken).apply();
        });

        Log.d("newToken", this.getPreferences(Context.MODE_PRIVATE).getString("fb", "empty :("));


        // Keep this last
        System.out.println("Loading maps");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Start the data auto refresh timer
        updateDataAsyncTask();
    }


    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    public static ArrayList<MapObject> mapObjects = new ArrayList<>();

    public static void fetchAPIData() {
        try {
            System.out.println("Fetching API data");

            mapObjects.clear();
            ArrayList<MapObject> objects = API.fetchData();
            mapObjects.addAll(objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDataAsyncTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(MainActivity::fetchAPIData);
            }
        };

        timer.schedule(task, 60 * 1000, 60 * 1000);  // Every 1 minute
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
        // plotPoints(mMap, mapObjects);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.257165, -79.900799), 13));

//        drawHeatMap(mMap, mapObjects);

        // Ploting points is first
        plotPoints(mMap, mapObjects);
    }

    public void plotPoints(GoogleMap googleMap, List<MapObject> mapObjects) {
        mMap = googleMap;

        for (MapObject mapObject : mapObjects) {
            float color;
            switch (mapObject.getCategory()) {
                case "Emergency":
                    color = BitmapDescriptorFactory.HUE_RED;
                    break;
                case "Criminal Activity":
                    color = BitmapDescriptorFactory.HUE_ORANGE;
                    break;
                case "Fire":
                    color = BitmapDescriptorFactory.HUE_VIOLET;
                    break;
                case "Natural":
                    color = BitmapDescriptorFactory.HUE_BLUE;
                    break;
                default:
                    color = BitmapDescriptorFactory.HUE_AZURE;
                    break;
            }


            Marker marker = mMap.addMarker(new MarkerOptions().
                    position(mapObject
                            .getLatLng())
                    .title(mapObject
                            .getName())
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(color)
                    )
            );

            mapObject.marker = marker;
        }

        mMap.setOnMarkerClickListener(this);
    }


    public void reportButton(View f) {
        Intent intent = new Intent(MainActivity.this, ReportActivity.class);
        startActivityForResult(intent, 0);
    }

    // The button cycles between the 3 options
    int toggleButtonStage = 0;
    public void toggleButton(View f) {
        mMap.clear();

        toggleButtonStage++;
        if (toggleButtonStage >= 3)
            toggleButtonStage = 0;

        switch (toggleButtonStage) {
            case 0:
                plotPoints(mMap, mapObjects);
                break;
            case 1:
                drawHeatMap(mMap, mapObjects);
                break;
            case 2:
                drawDangerMap();
                break;
        }
    }

    /**
     * Draws a heatmap on the map
     */
    public void drawHeatMap(GoogleMap googleMap, List<MapObject> mapObjects) {
        if (mapObjects.size() == 0)
            return;

        mMap = googleMap;
        int[] gradientColors = {
                Color.rgb(195, 107, 0),
                Color.rgb(207, 0, 0)
        };
        float[] gradientStartPoints = {
                0.2f,
                1f
        };
        Gradient gradient = new Gradient(gradientColors, gradientStartPoints);

        List<LatLng> locations = new ArrayList<>();
        for (MapObject mapObject : mapObjects) {
            locations.add(mapObject.getLatLng());
        }

        mProvider = new HeatmapTileProvider.Builder()
                .data(locations)
                .radius(50)
                .gradient(gradient)
                .build();

        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
//        Resources res = getResources();
        String text = " ";
        String title = " ";
        String cat = " ";
        for (MapObject mapObject : mapObjects) {
            if (marker.getPosition().equals(mapObject.getLatLng())) {
                text = (mapObject.getDescription());
                title = mapObject.getName();
                cat = mapObject.getCategory();
                break;
            }
        }

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        onButtonShowPopupWindow(viewGroup, text, title, cat);


        return false;
    }

    public void onButtonShowPopupWindow(View view, String text, String title, String category) {

        mMap.moveCamera(CameraUpdateFactory.zoomIn());

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        TextView t = popupView.findViewById(R.id.popuptext);
        t.setText(text);
        TextView e = popupView.findViewById(R.id.title);
        e.setText(title);
        TextView x = popupView.findViewById(R.id.category);
        x.setText(category);

        /*


        URL url = new URL("https://cdn.cms.prod.nypr.digital/images/subwayduck120419.2e16d0ba.fill-661x496.jpg");
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        imageView.setImageBitmap(bmp);


         */

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            // TODO Show unshow image based on R/L swipes
            public void onSwipeRight() {
                // Show image
                System.out.println("R swipe");
            }
            public void onSwipeLeft() {
                // Hide image
                System.out.println("L swipe");
            }

            // Dismiss when swipped down
            public void onSwipeBottom() {
                mMap.moveCamera(CameraUpdateFactory.zoomOut());
                popupWindow.dismiss();
            }
        });
    }

    // ###########################################
    // ###########################################
    // danger map
    // ###########################################
    // ###########################################

    /**
     * Utilises points on screen to draw a danger heatmap
     */
    private void drawDangerMap() {
        System.out.println("generating danger map...");
        // 43.257009, -79.900810

        List<LatLng> dangerLocations = new ArrayList<>();
        for (int i = 0; i < mapObjects.size(); i++) {
            dangerLocations.add(mapObjects.get(i).getLatLng());
        }

        /*
            ADD PREDICTED DANGERS HERE.............
         */

        // BASE COLOR
        System.out.println("generating danger map... BASE");
        {
            List<LatLng> locations = new ArrayList<>();

            Random rand = new Random();

            for (double lon = -80; lon < -79.8; lon += 0.001) {
                for (double lat = 43.1; lat < 43.3; lat += 0.001) {
                    // Do some danger predicting here, add different numbers of points
                    LatLng ln = new LatLng(lat, lon);
                    locations.add(ln);

                    // 1 in 80 chance of adding blip
//                    if (rand.nextInt(200) == 0) {
//                        dangerLocations.add(ln);
//                    }
                }
            }

            // Non alert colors
            int[] gradientColors = {
                    Color.rgb(117, 243, 188)
            };
            float[] gradientStartPoints = {
                    1f
            };

            Gradient gradient = new Gradient(gradientColors, gradientStartPoints);
            mProvider = new HeatmapTileProvider.Builder()
                    .data(locations)
                    .radius(50)
                    .gradient(gradient)
                    .build();

            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }

        // DANGERS COLOR
        System.out.println("generating danger map... DANGERS");
        {
            // Non alert colors
            int[] gradientColors = {
                    Color.rgb(255, 175, 27),
                    Color.rgb(255, 43, 27),
            };
            float[] gradientStartPoints = {
                    0.1f,
                    0.5f,
            };

            Gradient gradient = new Gradient(gradientColors, gradientStartPoints);
            mProvider = new HeatmapTileProvider.Builder()
                    .data(dangerLocations)
                    .radius(50)
                    .gradient(gradient)
                    .build();

            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }
    }
}
