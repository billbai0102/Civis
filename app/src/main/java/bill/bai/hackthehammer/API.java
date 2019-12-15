package bill.bai.hackthehammer;

import android.os.AsyncTask;
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

    // Sends string to provided url
    private static class GetFromUrlTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... string) {
            String url_str = string[0];

            try {
                System.out.println(url_str);

                URL url = new URL(url_str);
                InputStreamReader isr = new InputStreamReader(url.openStream());
                BufferedReader in = new BufferedReader(isr);
                StringBuilder returnStr = new StringBuilder();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);

                    returnStr.append(inputLine);
                }
                in.close();


                String result = returnStr.toString();
                System.out.println("Received json string: " + result);
                return result;
            } catch (Exception e){
                e.printStackTrace();
            }

            return "";
        }
    }

    // Fetches map data from database
    public static ArrayList<MapObject> fetchData() {
        // Fetch data
        String jsonStr = null;
        try {
            jsonStr = new GetFromUrlTask().execute(sheetsUrl).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Deserialize to MapObject
        Gson gson = new Gson();
        MapObject[] mapObjectsArray = gson.fromJson(jsonStr, MapObject[].class);

        // Convert array to ArrayList
        ArrayList<MapObject> mapObjects = new ArrayList<>();

        if (mapObjectsArray != null)
            mapObjects.addAll(Arrays.asList(mapObjectsArray));

        return mapObjects;
    }


    // #############################################
    // POST
    // #############################################//
    //
    // Sends string to provided url
    private static class SendPostTask extends AsyncTask<String, Integer, Void> {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... string) {
            try {
                String postUrl = string[0];
                String data = string[1];

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
                e.printStackTrace();
            }

            return null;
        }
    }

    // Posts provided MapObjects to API url
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void postData(ArrayList<MapObject> mapObjects) {
        System.out.println("Posting data to database");
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

                System.out.println(sj.toString());
                new SendPostTask().execute(sheetsUrl, sj.toString()).get();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
