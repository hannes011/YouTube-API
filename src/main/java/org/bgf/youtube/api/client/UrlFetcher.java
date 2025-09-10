package org.bgf.youtube.api.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface UrlFetcher {
    InputStream open(URL url) throws IOException;
}

