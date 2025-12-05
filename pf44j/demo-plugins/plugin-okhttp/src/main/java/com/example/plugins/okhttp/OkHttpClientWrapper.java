package com.example.plugins.okhttp;

import com.example.api.NetworkPlugin;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.pf4j.Extension;

import java.io.IOException;

@Extension
public class OkHttpClientWrapper implements NetworkPlugin {

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public String fetchContent(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return "OkHttpClientWrapper: " + response.body().string();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
