package org.bgf.youtube.client;

import org.bgf.youtube.api.client.UrlFetcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUrlFetcher implements UrlFetcher {

    @Override
    public InputStream open(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);
        conn.setRequestMethod("GET");
        conn.connect();
        int code = conn.getResponseCode();
        if (code >= 200 && code < 300) {
            return conn.getInputStream();
        }
        // propagate as IO error to let callers handle fallback logic
        throw new IOException("HTTP " + code + " for URL: " + url);
    }
}

