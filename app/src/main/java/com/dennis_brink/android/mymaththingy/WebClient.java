package com.dennis_brink.android.mymaththingy;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dennis_brink.android.mymaththingy.gamecore.GameCore;
import com.dennis_brink.android.mymaththingy.gamecore.Hmac;
import com.dennis_brink.android.mymaththingy.gamecore.Player;
import com.dennis_brink.android.mymaththingy.gamecore.RankSet;
import com.dennis_brink.android.mymaththingy.gamecore.ScoreSet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebClient implements IRegistrationConstants {

    private boolean isOperational;
    private String apiKey, apiUrl, apiApp;
    private final Context context;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public WebClient(Context context) {
        this.context = context;
    }

    public void initWebClient() {
        try {
            getApiConfiguration();
        } catch (IOException e) {
            this.isOperational = false;
            Log.d("DENNIS_B", "Error WebClient.WebClient(): " + e.getMessage());
        }
        this.isOperational = true;
    }

    private void getApiConfiguration() throws IOException {

        String sCharacter = "";
        InputStream inputStream = null;
        ApiConfiguration apiConfiguration;

        AssetManager assetManager = this.context.getAssets();
        inputStream = assetManager.open("datasource.config.json");

        StringBuilder str = new StringBuilder();

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((sCharacter = bufferedReader.readLine()) != null) {
                str.append(sCharacter);
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        apiConfiguration = mapper.readValue(str.toString(), ApiConfiguration.class);

        this.apiKey = apiConfiguration.getKey();
        this.apiApp = apiConfiguration.getApp();
        this.apiUrl = apiConfiguration.getUrl();
    }

    public void uploadSubSet() throws JsonProcessingException {

        Player player = GameCore.getPlayer();
        ScoreSet scoreSet = GameCore.getScoreSet();

        OkHttpClient okHttpClient = new OkHttpClient();
        String sBody;

        ObjectMapper objectMapper = new ObjectMapper();
        sBody = objectMapper.writeValueAsString(scoreSet.getSubSetAsArray());

        Log.d("DENNIS_B", "uploadSubSetBody " + sBody);

        RequestBody body = RequestBody.create(sBody, JSON);

        try {
            Log.d("DENNIS_B", "token " +  Hmac.generateHmacSha256(this.apiKey, this.apiApp + "." + player.getDeviceId(), true));
        } catch (Exception e) {
            Log.d("DENNIS_B", "Error " + e.getMessage());
            throw new RuntimeException(e);
        }

        Request request = null;
        try {
            request = new Request.Builder()
                    .header("token", Hmac.generateHmacSha256(this.apiKey, this.apiApp + "." + player.getDeviceId(), true))
                    .header("device", player.getDeviceId())
                    .post(body)
                    .url(String.format("%s/score", this.apiUrl))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("DENNIS_B", "Retrieval of ranking failed " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                JSONObject jsonObject;
                RankSet rankSet = null;
                if(response.isSuccessful()) {
                    try {
                        jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            rankSet = mapper.readValue(jsonObject.toString(), RankSet.class);
                        } catch(Exception e){
                            Log.d("DENNIS_B", "conversion response to POJO failed " + e.getMessage());
                        }

                    } catch (JSONException e) {
                        Log.d("DENNIS_B", "conversion response to JSON failed " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                    if (rankSet != null) {
                        Log.d("DENNIS_B", "goed gegaan " + rankSet.toString());
                        ScoreSet scoreSet = GameCore.getScoreSet();
                        scoreSet.updateScoreSetByRanking(rankSet);
                        GameCore.saveDataStructure(scoreSet);
                        Log.d("DENNIS_B", "Alles bijgewerkt --> check highscores ? ");
                    }else {
                        Log.d("DENNIS_B", "rankSet blijkt niet gevuld");
                    }
                    // send something to mainactivity so it can update the scores
                } else {
                    // retrieve custom message sent from the API if any. If there is a custom message
                    // it must have the structure like this { type: <http code>, message: <message> }
                    if(response.body()!=null) {
//                        ObjectMapper mapper = new ObjectMapper();
//                        JsonNode jsonNode = mapper.readTree(response.body().string());
//                        sendRegistrationFailure(jsonNode.get("type").asText() + " " + jsonNode.get("message").asText());
                        Log.d("DENNIS_B", "NIET goed gegaan " + response.toString());
                    } else {
//                        sendRegistrationFailure(response.code() + " " + response.message());
                        Log.d("DENNIS_B", "NIET goed gegaan ZONDER response" );
                    }
                }
            }
        });

    }

    public void savePlayer(Player player) throws JsonProcessingException {

        OkHttpClient okHttpClient = new OkHttpClient();
        String sBody;

        ObjectMapper objectMapper = new ObjectMapper();
        sBody = objectMapper.writeValueAsString(player);

        Log.d("DENNIS_B", sBody);

        RequestBody body = RequestBody.create(sBody, JSON);

        try {
            Log.d("DENNIS_B", "token " +  Hmac.generateHmacSha256(this.apiKey, this.apiApp + "." + player.getDeviceId(), true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Request request = null;
        try {
            request = new Request.Builder()
                    .header("token", Hmac.generateHmacSha256(this.apiKey, this.apiApp + "." + player.getDeviceId(), true))
                    .header("device", player.getDeviceId())
                    .post(body)
                    .url(String.format("%s/player", this.apiUrl))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("DENNIS_B", "Error: " + e.getMessage());
                sendRegistrationFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    sendRegistrationSuccess();
                } else {
                    // retrieve custom message sent from the API if any. If there is a custom message
                    // it must have the structure like this { type: <http code>, message: <message> }
                    if(response.body()!=null) {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode jsonNode = mapper.readTree(response.body().string());
                        sendRegistrationFailure(jsonNode.get("type").asText() + " " + jsonNode.get("message").asText());
                    } else {
                        sendRegistrationFailure(response.code() + " " + response.message());
                    }
                }
            }
        });
    }

    private void sendRegistrationFailure(String msg) {
        Intent i = new Intent();
        i.setAction(ONLINE_REGISTRATION_FAILURE);
        i.putExtra("MSG", msg);
        this.context.sendBroadcast(i);
    }

    private void sendRegistrationSuccess() {
        Intent i = new Intent();
        i.setAction(ONLINE_REGISTRATION_SUCCESS);
        this.context.sendBroadcast(i);
    }

    @NonNull
    @Override
    public String toString() {
        return "WebClient{" +
                "isOperational=" + isOperational +
                ", apiKey='" + apiKey + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                '}';
    }
}
