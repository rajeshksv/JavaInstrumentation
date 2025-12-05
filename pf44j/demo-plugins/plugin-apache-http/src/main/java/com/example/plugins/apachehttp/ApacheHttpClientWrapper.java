package com.example.plugins.apachehttp;

import com.example.api.NetworkPlugin;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.pf4j.Extension;

import java.io.IOException;

@Extension
public class ApacheHttpClientWrapper implements NetworkPlugin {

    @Override
    public String fetchContent(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String content = EntityUtils.toString(entity);
                    return "ApacheHttpClientWrapper: " + content;
                }
                return "ApacheHttpClientWrapper: No content returned";
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
