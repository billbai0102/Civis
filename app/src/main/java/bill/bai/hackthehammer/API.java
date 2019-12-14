package bill.bai.hackthehammer;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.*;
import java.io.*;

public class API {
    static String sheetsUrl =
            "https://script.google.com/macros/s/AKfycbzQBYHI41CKOZBdN82Ul5hOiLOcOc4V5bDW0lMCNbyCWKgqzuo/exec";

    private static String fetchFromUrl(String url) {
        try {
            URL oracle = new URL(url);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(oracle.openStream()));

            StringBuilder returnStr = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                returnStr.append(inputLine);
            in.close();

            return returnStr.toString();
        } catch (Exception e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

        return "";
    }

    // Fetches map data from database
    public static ArrayList<MapObject> fetchData() {
        // Fetch data
        String jsonStr = fetchFromUrl(sheetsUrl);

        // Deserialize to MapObject
        Gson gson = new Gson();
        MapObject[] mapObjectsArray = gson.fromJson(jsonStr, MapObject[].class);

        // Convert array to ArrayList
        ArrayList<MapObject> mapObjects =
                new ArrayList<>(Arrays.asList(mapObjectsArray));

        return mapObjects;
    }


    // Sends string to provided url
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendStr(String postUrl, String data) {
        try {
            // Sender
            URL url = new URL(postUrl);

            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);

            // Send json
            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();

            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }

        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    // Posts provided MapObjects to API url
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void postData(ArrayList<MapObject> mapObjects) {
        try {
            // Post iteratively
            for (MapObject mapObject: mapObjects) {
                // Arguments
                Map<String, String> arguments = new HashMap<>();

                arguments.put("name", mapObject.getName());
                arguments.put("description", mapObject.getDescription());
                arguments.put("category", mapObject.getCategory());

                LatLng latLon = mapObject.getLatLng();
                arguments.put("latitude", String.valueOf(latLon.latitude));
                arguments.put("longitude", String.valueOf(latLon.longitude));

                StringJoiner sj = new StringJoiner("&");
                for(Map.Entry<String, String> entry : arguments.entrySet())
                    sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                            + URLEncoder.encode(entry.getValue(), "UTF-8"));

                sendStr(sheetsUrl, sj.toString());
            }

        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
