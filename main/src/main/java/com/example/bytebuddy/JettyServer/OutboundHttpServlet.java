package com.example.bytebuddy.JettyServer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet that handles HTTP requests and makes outbound HTTP calls using Apache HTTP synchronous client
 */
public class OutboundHttpServlet extends HttpServlet {

    // Apache HTTP synchronous (non-NIO) client
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OutboundHttpServlet() {
        this.objectMapper = new ObjectMapper();
        // Configure request settings
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)        // Connection timeout: 5 seconds
                .setSocketTimeout(10000)        // Socket timeout: 10 seconds
                .setConnectionRequestTimeout(5000) // Connection request timeout: 5 seconds
                .build();

        // Create HTTP client with connection pooling and configuration
        this.httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setMaxConnTotal(100)           // Maximum total connections
                .setMaxConnPerRoute(20)         // Maximum connections per route
                .build();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getPathInfo();
        if (path == null) {
            path = request.getServletPath();
        }

        // Health check endpoint
        if (path.equals("/health")) {
            handleHealthCheck(response);
            return;
        }

        // Example endpoint that makes a GET request to httpbin.org
        if (path.equals("/example")) {
            handleExampleRequest(response);
            return;
        }

        // Generic HTTP GET endpoint
        if (path.equals("/http-get")) {
            String targetUrl = request.getParameter("url");
            if (targetUrl == null || targetUrl.isEmpty()) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Missing 'url' parameter. Usage: /http-get?url=<target_url>");
                return;
            }
            handleOutboundGet(targetUrl, response);
            return;
        }

        // Default response
        sendError(response, HttpServletResponse.SC_NOT_FOUND, 
            "Endpoint not found. Available endpoints: /health, /example, /http-get?url=<target_url>, /http-post?url=<target_url>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String path = request.getPathInfo();
        if (path == null) {
            path = request.getServletPath();
        }

        // Generic HTTP POST endpoint
        if (path.equals("/http-post")) {
            String targetUrl = request.getParameter("url");
            if (targetUrl == null || targetUrl.isEmpty()) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                    "Missing 'url' parameter. Usage: /http-post?url=<target_url>");
                return;
            }
            
            // Read request body
            StringBuilder body = new StringBuilder();
            try (java.io.BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            }
            
            handleOutboundPost(targetUrl, body.toString(), response);
            return;
        }

        sendError(response, HttpServletResponse.SC_NOT_FOUND, 
            "Endpoint not found. Available POST endpoint: /http-post?url=<target_url>");
    }

    private void handleHealthCheck(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        
        ObjectNode json = objectMapper.createObjectNode();
        json.put("status", "healthy");
        json.put("server", "Jetty");
        json.put("client", "Apache HTTP Sync Client");
        
        out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
    }

    private void handleExampleRequest(HttpServletResponse response) throws IOException {
        String targetUrl = "http://httpbin.org/get";
        handleOutboundGet(targetUrl, response);
    }

    private void handleOutboundGet(String targetUrl, HttpServletResponse response) throws IOException {
        HttpGet httpGet = new HttpGet(targetUrl);
        executeRequest(httpGet, response);
    }

    private void handleOutboundPost(String targetUrl, String body, HttpServletResponse response) throws IOException {
        HttpPost httpPost = new HttpPost(targetUrl);
        if (body != null && !body.isEmpty()) {
            httpPost.setEntity(new StringEntity(body, "UTF-8"));
            httpPost.setHeader("Content-Type", "application/json");
        }
        executeRequest(httpPost, response);
    }

    private void executeRequest(HttpUriRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (CloseableHttpResponse httpResponse = httpClient.execute(request)) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            HttpEntity entity = httpResponse.getEntity();
            String responseBody = entity != null ? EntityUtils.toString(entity) : "";

            // Set response status
            response.setStatus(statusCode);

            // Build JSON response using Jackson
            ObjectNode jsonResponse = objectMapper.createObjectNode();
            
            ObjectNode outboundRequest = objectMapper.createObjectNode();
            outboundRequest.put("method", request.getMethod());
            outboundRequest.put("url", request.getURI().toString());
            jsonResponse.set("outboundRequest", outboundRequest);
            
            ObjectNode outboundResponse = objectMapper.createObjectNode();
            outboundResponse.put("statusCode", statusCode);
            outboundResponse.put("statusReason", httpResponse.getStatusLine().getReasonPhrase());
            // Try to parse response body as JSON, if it fails, treat as string
            try {
                outboundResponse.set("body", objectMapper.readTree(responseBody));
            } catch (Exception e) {
                outboundResponse.put("body", responseBody);
            }
            jsonResponse.set("outboundResponse", outboundResponse);

            out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResponse));
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ObjectNode errorJson = objectMapper.createObjectNode();
            errorJson.put("error", e.getMessage());
            out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJson));
            System.err.println("Error executing outbound HTTP request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        
        ObjectNode errorJson = objectMapper.createObjectNode();
        errorJson.put("error", message);
        out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJson));
    }

    @Override
    public void destroy() {
        super.destroy();
        // Close HTTP client when servlet is destroyed
        try {
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing HTTP client: " + e.getMessage());
        }
    }
}

