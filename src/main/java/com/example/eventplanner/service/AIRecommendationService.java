package com.example.eventplanner.service;

import okhttp3.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AIRecommendationService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .callTimeout(90, TimeUnit.SECONDS)
            .build();

    public String generate(String finalPrompt) throws Exception {

        JSONObject payload = new JSONObject()
                .put("contents", new JSONArray()
                        .put(new JSONObject()
                                .put("role", "user")
                                .put("parts", new JSONArray()
                                        .put(new JSONObject().put("text", finalPrompt))
                                )
                        )
                );

        Request request = new Request.Builder()
                .url(API_URL + "?key=" + apiKey)
                .post(RequestBody.create(payload.toString(), MediaType.get("application/json")))
                .build();

        Response response = client.newCall(request).execute();
        String raw = response.body().string();

        if (!response.isSuccessful()) {
            return "❌ AI Error: " + raw;
        }

        try {
            JSONObject obj = new JSONObject(raw);
            return obj.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

        } catch (Exception e) {
            return "⚠ Parsing failed \nRaw output:\n" + raw;
        }
    }
}
