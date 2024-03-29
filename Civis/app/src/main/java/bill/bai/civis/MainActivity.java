package bill.bai.civis;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.RequiresApi;

import android.util.Log;

import androidx.core.view.GravityCompat;

import androidx.core.view.ViewCompat;
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
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // This is called upon application start
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // App startup
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT > 15) {
            addNotifBar();
        }


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

    @TargetApi(21)
    public void addNotifBar(){
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
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

        if (Build.VERSION.SDK_INT > 15) {
            addNotifBar();
        }

        mMap = googleMap;
        // plotPoints(mMap, mapObjects);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.257165, -79.900799), 13));

//        drawHeatMap(mMap, mapObjects);

        // Ploting points is first
        plotPoints(mMap, mapObjects);
        findViewById(R.id.pinButtonImage).setVisibility(View.VISIBLE);
        findViewById(R.id.heatButtonImage).setVisibility(View.INVISIBLE);
        findViewById(R.id.heat2ButtonImage).setVisibility(View.INVISIBLE);

    }

//    public static Camera getCameraInstance(){
//        Camera c  = null;
//        try{
//            c = Camera.open();
//        }catch(Exception e){
//
//        }
//        return c;
//    }

    public void plotPoints(GoogleMap googleMap, List<MapObject> mapObjects) {
        mMap = googleMap;

        for (MapObject mapObject : mapObjects) {
            float color;
            switch (mapObject.getCategory().toLowerCase()) {
                case "emergency":
                    color = BitmapDescriptorFactory.HUE_RED;
                    break;
                case "criminal activity":
                    color = BitmapDescriptorFactory.HUE_ORANGE;
                    break;
                case "fire":
                    color = BitmapDescriptorFactory.HUE_YELLOW;
                    break;
                case "natural":
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

        Marker marker = mMap.addMarker(new MarkerOptions().
                position(new LatLng(43.256813, -79.900789))
                .title("Your Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallguy)));

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
                findViewById(R.id.pinButtonImage).setVisibility(View.VISIBLE);
                findViewById(R.id.heatButtonImage).setVisibility(View.INVISIBLE);
                findViewById(R.id.heat2ButtonImage).setVisibility(View.INVISIBLE);
                plotPoints(mMap, mapObjects);
                break;
            case 1:
                findViewById(R.id.pinButtonImage).setVisibility(View.INVISIBLE);
                findViewById(R.id.heatButtonImage).setVisibility(View.VISIBLE);
                findViewById(R.id.heat2ButtonImage).setVisibility(View.INVISIBLE);
                drawHeatMap(mMap, mapObjects);
                break;
            case 2:
                findViewById(R.id.pinButtonImage).setVisibility(View.INVISIBLE);
                findViewById(R.id.heatButtonImage).setVisibility(View.INVISIBLE);
                findViewById(R.id.heat2ButtonImage).setVisibility(View.VISIBLE);
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

        Marker marker = mMap.addMarker(new MarkerOptions().
                position(new LatLng(43.256813, -79.900789))
                .title("Your Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallguy)));
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
//        Resources res = getResources();
        String text = " ";
        String title = " ";
        String cat = " ";
        String imgUrl = "";

        for (MapObject mapObject : mapObjects) {
            if (marker.getPosition().equals(mapObject.getLatLng())) {
                text = (mapObject.getDescription());
                title = mapObject.getName();
                cat = mapObject.getCategory();
                imgUrl = mapObject.getImageUrl();
                break;
            }
        }

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        onButtonShowPopupWindow(viewGroup, text, title, cat, imgUrl);


        return false;
    }


    // ---------------------------------------------------------------
    // POPUP
    // Loads images
//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//
//
//        }
//    }

    public void onButtonShowPopupWindow(View view, String text, String title, String category, String imgUrl) {

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

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        ImageView image = findViewById(R.id.popupSwipeImage);

        TextView hasImageIndicator = popupView.findViewById(R.id.hasPictureIndicator);
        // Show arrow if image exists
        if (imgUrl.isEmpty()) {
            hasImageIndicator.setVisibility(View.INVISIBLE);
        }
        else {
            hasImageIndicator.setVisibility(View.VISIBLE);
        }


        popupView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            // Show unshow image based on R/L swipes
            public void onSwipeRight() {
                // Show image
                Picasso.get().load(imgUrl).into(image);
                image.setVisibility(View.VISIBLE);

                view.bringToFront();

                // Hide the popup
                popupWindow.dismiss();

                System.out.println("R swipe SHow popup image");
            }

            // Dismiss when swipped down
            public void onSwipeBottom() {
                mMap.moveCamera(CameraUpdateFactory.zoomOut());
                popupWindow.dismiss();
                image.setVisibility(View.INVISIBLE);
            }
        });

        image.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeLeft() {
                // Hide image
                image.setVisibility(View.INVISIBLE);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                System.out.println("L swipe Hide popup image");
            }

            // Dismiss when swipped down
            public void onSwipeBottom() {
                mMap.moveCamera(CameraUpdateFactory.zoomOut());
                popupWindow.dismiss();
                image.setVisibility(View.INVISIBLE);
            }
        });

        // Show the popup after setting all the listeners
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

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

            Marker marker = mMap.addMarker(new MarkerOptions().
                    position(new LatLng(43.256813, -79.900789))
                    .title("Your Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallguy)));
        }
    }
}
