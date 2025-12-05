package com.example.plugins.javanet;

import com.example.api.NetworkPlugin;
import org.pf4j.Extension;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Extension
public class JavaNetClient implements NetworkPlugin {

    @Override
    public String fetchContent(String urlString) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return "JavaNetClient: " + result.toString();
    }
}
