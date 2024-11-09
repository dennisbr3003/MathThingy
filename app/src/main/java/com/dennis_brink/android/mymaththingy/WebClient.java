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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebClient implements IRegistrationConstants, ILogConstants {

    private boolean isOperational;
    private String apiKey, apiUrl, apiApp;
    private final Context context;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    CountDownLatch countDownLatch = null;

    public WebClient(Context context) {
        this.context = context;
    }

    public void initWebClient() {
        try {
            getApiConfiguration();
        } catch (IOException e) {
            this.isOperational = false;
            Log.d(LOG_TAG, "Error WebClient.WebClient(): " + e.getMessage());
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

        long start = System.currentTimeMillis();

        Player player = GameCore.getPlayer();
        ScoreSet scoreSet = GameCore.getScoreSet();

        OkHttpClient okHttpClient = new OkHttpClient();
        String sBody;

        ObjectMapper objectMapper = new ObjectMapper();
        sBody = objectMapper.writeValueAsString(scoreSet.getSubSetAsArray());

        RequestBody body = RequestBody.create(sBody, JSON);

        Request request = null;
        try {
            request = new Request.Builder()
                    .header("token", Hmac.generateHmacSha256(this.apiKey, this.apiApp + "." + player.getDeviceId(), true))
                    .header("device", player.getDeviceId())
                    .post(body)
                    .url(String.format("%s/score", this.apiUrl))
                    .build();
        } catch (Exception e) {
            sendRegistrationFailure(countDownLatch==null, e.getMessage());
            decreaseCountDownLatch();
            throw new RuntimeException(e);
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(LOG_TAG, "WebClient uploadSubSet() - Retrieval of ranking failed " + e.getMessage());
                sendRegistrationFailure(countDownLatch==null, e.getMessage());
                decreaseCountDownLatch();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                JSONObject jsonObject;
                RankSet rankSet = null;

                if(response.isSuccessful()) {
                    try {
                        jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                        ObjectMapper mapper = new ObjectMapper();
                        rankSet = mapper.readValue(jsonObject.toString(), RankSet.class);
                        if (rankSet != null) {
                            ScoreSet scoreSet = GameCore.getScoreSet();
                            scoreSet.updateScoreSetByRanking(rankSet);
                            GameCore.saveDataStructure(scoreSet);
                        }
                    } catch (Exception e) {
                        Log.d(LOG_TAG, "WebClient uploadSubSet() - processing conversion response to JSON failed " + e.getMessage());
                        sendRegistrationFailure(countDownLatch==null, e.getMessage());
                        decreaseCountDownLatch();
                        throw new RuntimeException(e);
                    }
                    sendRegistrationSuccess(countDownLatch==null);
                    decreaseCountDownLatch();
                    long end = System.currentTimeMillis();
                    Log.d(LOG_TAG, "WebClient uploadSubSet() - success ("+ (end - start) + "ms)");

                } else {
                    // retrieve custom message sent from the API if any. If there is a custom message
                    // it must have the structure like this { type: <http code>, message: <message> }
                    if(response.body()!=null) {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode jsonNode = mapper.readTree(response.body().string());
                        sendRegistrationFailure(countDownLatch==null, jsonNode.get("type").asText() + " " + jsonNode.get("message").asText());
                    } else{
                        sendRegistrationFailure(countDownLatch==null, "500 WebClient.uploadSubSet() - Response is null");
                    }
                    decreaseCountDownLatch();
                }
            }
        });

    }

    public void savePlayer(Player player) throws JsonProcessingException {

        long start = System.currentTimeMillis();

        OkHttpClient okHttpClient = new OkHttpClient();
        String sBody;

        ObjectMapper objectMapper = new ObjectMapper();
        sBody = objectMapper.writeValueAsString(player);

        RequestBody body = RequestBody.create(sBody, JSON);

        Request request = null;
        try {
            request = new Request.Builder()
                    .header("token", Hmac.generateHmacSha256(this.apiKey, this.apiApp + "." + player.getDeviceId(), true))
                    .header("device", player.getDeviceId())
                    .post(body)
                    .url(String.format("%s/player", this.apiUrl))
                    .build();
        } catch (Exception e) {
            sendRegistrationFailure(countDownLatch==null, e.getMessage());
            decreaseCountDownLatch();
            throw new RuntimeException(e);
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(LOG_TAG, "WebClient savePlayer() - save of player failed " + e.getMessage());
                sendRegistrationFailure(countDownLatch==null, e.getMessage());
                decreaseCountDownLatch();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    sendRegistrationSuccess(countDownLatch==null);
                    decreaseCountDownLatch();
                    long end = System.currentTimeMillis();
                    Log.d(LOG_TAG, "WebClient savePlayer() - success ("+ (end - start) + "ms)");
                } else {
                    // retrieve custom message sent from the API if any. If there is a custom message
                    // it must have the structure like this { type: <http code>, message: <message> }
                    if(response.body()!=null) {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode jsonNode = mapper.readTree(response.body().string());
                        sendRegistrationFailure(countDownLatch==null, jsonNode.get("type").asText() + " " + jsonNode.get("message").asText());
                    } else {
                        sendRegistrationFailure(countDownLatch==null, "500 WebClient.savePlayer() - Response is null");
                    }
                    decreaseCountDownLatch();
                }
            }
        });
    }

    public void savePlayerAndScores(Player player) {

        // we only get the player as a param
        // Values can be entered in the form this is called from
        // the scores we get from the app itself that may be there or they may not
        // Scores are handled in 'uploadSubSet()'
        try {

            long start = System.currentTimeMillis();
            ExecutorService ex = Executors.newFixedThreadPool(4); // get 4 to be save

            countDownLatch = new CountDownLatch(2); // create instance for 2 calls

            ex.submit(new Thread(() -> {
                try {
                    savePlayer(player);
                } catch (Exception e) {
                    sendRegistrationFailure(countDownLatch!=null, e.getMessage());
                }
            }));
            ex.submit(new Thread(() -> {
                try {
                    uploadSubSet();
                } catch (Exception e) {
                    sendRegistrationFailure(countDownLatch!=null, e.getMessage());
                }
            }));
            Log.d(LOG_TAG, "WebClient.savePlayerAndScores() - Awaiting numbers of threads: " + countDownLatch.getCount());
            ex.shutdown(); // starts processing threads
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                sendRegistrationFailure(countDownLatch!=null, "500 WebClient.savePlayerAndScores() - " + e.getMessage());
            }
            long end = System.currentTimeMillis();
            Log.d(LOG_TAG, "WebClient.savePlayerAndScores() - Executed in " + (end - start) + "ms");

            sendRegistrationSuccess(countDownLatch!=null);
        } finally{
            countDownLatch = null;
        }

    }

    public void deletePlayerAndScores(Player player) throws JsonProcessingException {
        long start = System.currentTimeMillis();

        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = null;
        try {
            request = new Request.Builder()
                    .header("token", Hmac.generateHmacSha256(this.apiKey, this.apiApp + "." + player.getDeviceId(), true))
                    .header("device", player.getDeviceId())
                    .delete()
                    .url(String.format("%s/player", this.apiUrl))
                    .build();
        } catch (Exception e) {
            sendRegistrationFailure(countDownLatch==null, e.getMessage());
            decreaseCountDownLatch();
            throw new RuntimeException(e);
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(LOG_TAG, "WebClient deletePlayerAndScores() - 'forget me request' failed " + e.getMessage());
                sendRegistrationFailure(countDownLatch==null, e.getMessage());
                decreaseCountDownLatch();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    sendRegistrationSuccess(countDownLatch==null);
                    decreaseCountDownLatch();
                    long end = System.currentTimeMillis();
                    Log.d(LOG_TAG, "WebClient deletePlayerAndScores() - 'forget me request' success ("+ (end - start) + "ms)");
                } else {
                    // retrieve custom message sent from the API if any. If there is a custom message
                    // it must have the structure like this { type: <http code>, message: <message> }
                    if(response.body()!=null) {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode jsonNode = mapper.readTree(response.body().string());
                        sendRegistrationFailure(countDownLatch==null, jsonNode.get("type").asText() + " " + jsonNode.get("message").asText());
                    } else {
                        sendRegistrationFailure(countDownLatch==null, "500 WebClient.deletePlayerAndScores() - Response is null");
                    }
                    decreaseCountDownLatch();
                }
            }
        });

    }

    private void decreaseCountDownLatch() {
        if(countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    private void sendRegistrationFailure(Boolean sendIntent, String msg) {
        if(sendIntent) {
            Intent i = new Intent();
            i.setAction(ONLINE_REGISTRATION_FAILURE);
            i.putExtra("MSG", msg);
            this.context.sendBroadcast(i);
        }
    }

    private void sendRegistrationSuccess(Boolean sendIntent) {
        if(sendIntent) {
            Intent i = new Intent();
            i.setAction(ONLINE_REGISTRATION_SUCCESS);
            this.context.sendBroadcast(i);
        }
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
