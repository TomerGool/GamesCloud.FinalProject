package com.example.android1finalproject.data;

import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.android1finalproject.MainActivity;
import com.example.android1finalproject.models.Games;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DataService {
    private static ArrayList<Games> arrCat = new ArrayList<>();
    private static Context context;

    public DataService(Context context) {
        this.context = context;
    }

    public static ArrayList<Games> getCategory()
    {
        String Surl = "https://api.rawg.io/api/games?key=d69599dd827649e7837ab8b8b4f1fce2&dates=2019-09-01,2019-09-30&platforms=18,1,7";

        URL url = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        try
        {
            url = new URL(Surl);

        }catch(MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
        HttpURLConnection request = null;
        try{
            request = (HttpURLConnection) url.openConnection();

            request.connect();


            JsonParser js = new JsonParser();

            JsonElement root = js.parse(new InputStreamReader( request.getInputStream()));
            JsonObject rootObj = root.getAsJsonObject();



            JsonElement entrieRes = rootObj.get("results");
            ArrayList<String> arrRes = new ArrayList<>();
            JsonArray entrieResArr = entrieRes.getAsJsonArray();
            for(JsonElement j : entrieResArr)
            {
                JsonObject obj = j.getAsJsonObject();
                JsonElement enterieName = obj.get("name");
                JsonElement entrieAdded = obj.get("added");
                String name = enterieName.toString().replace("\"","");
                String added = entrieAdded.toString().replace("\"","");

                JsonElement entryGenres = obj.get("genres");
                ArrayList<String> arrGenres = new ArrayList<>();

                if (entryGenres != null && entryGenres.isJsonArray()) {
                    JsonArray entryGenresArr = entryGenres.getAsJsonArray();

                    for (JsonElement genreElement : entryGenresArr) {
                        if (genreElement.isJsonObject()) {
                            JsonObject genreObject = genreElement.getAsJsonObject();
                            String genreName = genreObject.get("name").getAsString();
                            arrGenres.add(genreName);
                        }
                    }
                }



                JsonElement entryPlatform = obj.get("parent_platforms");
                ArrayList<String> arrPlatform = new ArrayList<>();

                if (entryPlatform != null && entryPlatform.isJsonArray()) {
                    JsonArray entryPlatformArr = entryPlatform.getAsJsonArray();

                    for (JsonElement platformElement : entryPlatformArr) {
                        if (platformElement.isJsonObject()) {
                            JsonObject platformObject = platformElement.getAsJsonObject().getAsJsonObject("platform");
                            String platformName = platformObject.get("name").getAsString();
                            arrPlatform.add(platformName);
                        }
                    }
                }


                JsonElement entrieImage = obj.get("background_image");
                String image = entrieImage.toString().replace("\"","");


                arrCat.add(new Games(name,added,arrGenres,arrPlatform,image));
                addGame(name,added,arrGenres,arrPlatform,image);
            }

        }catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        return arrCat;
    }
    public static void addGame(String name, String added , ArrayList<String> genres , ArrayList<String> platform , String image)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> game = new HashMap<>();
        Games newGame = new Games(name,added,genres , platform , image);
        DocumentReference documentReference = db.collection("games").document(name);
        documentReference.set(newGame);
        game.put("name",name);
        game.put("added", added);
        game.put("genres", genres);
        game.put("platform", platform);
        game.put("image", image);
        documentReference.set(game).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}
