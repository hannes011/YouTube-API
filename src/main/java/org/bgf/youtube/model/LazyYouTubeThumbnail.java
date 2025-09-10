package org.bgf.youtube.model;

import org.bgf.youtube.api.YouTubeThumbnail;
import org.bgf.youtube.api.YouTubeThumbnailType;
import org.bgf.youtube.api.client.UrlFetcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LazyYouTubeThumbnail implements YouTubeThumbnail {

    private final String videoId;
    private final YouTubeThumbnailType type;
    private final UrlFetcher urlFetcher;

    public LazyYouTubeThumbnail(String videoId, YouTubeThumbnailType type, UrlFetcher urlFetcher) {
        this.videoId = videoId;
        this.type = type;
        this.urlFetcher = urlFetcher;
    }

    @Override
    public URL getUrl() {
        return toUrl(videoId, type);
    }

    @Override
    public InputStream fetch() throws IOException {
        try {
            return urlFetcher.open(getUrl());
        } catch (IOException e) {
            if (type == YouTubeThumbnailType.MAXRES) {
                // Fallback to HIGH if maxres not available
                try {
                    return urlFetcher.open(toUrl(videoId, YouTubeThumbnailType.HIGH));
                } catch (MalformedURLException ex) {
                    throw new IOException(ex);
                }
            }
            throw e;
        }
    }

    @Override
    public File fetchFile(Path target) throws IOException {
        Files.createDirectories(target.getParent());
        try (var in = fetch(); var out = Files.newOutputStream(target, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            in.transferTo(out);
        }
        return target.toFile();
    }

    private URL toUrl(String videoId, YouTubeThumbnailType thumbnailType) {
        try {
            return new URI("https://img.youtube.com/vi/" + videoId + "/" + imageVariant(thumbnailType)).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static String imageVariant(YouTubeThumbnailType type) {
        return switch (type) {
            case MIN, DEFAULT -> "default.jpg"; // MIN maps to default
            case MEDIUM -> "mqdefault.jpg";
            case HIGH -> "hqdefault.jpg";
            case MAXRES -> "maxresdefault.jpg";
        };
    }
}

