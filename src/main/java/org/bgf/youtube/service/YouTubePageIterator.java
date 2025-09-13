package org.bgf.youtube.service;

import com.google.api.client.json.GenericJson;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.bgf.youtube.api.client.PageIterator;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * A generic page iterator for YouTube Data API v3 list requests.
 * It executes the given request immediately to initialize the first page of items and
 * nextPageToken (if available). Subsequent calls lazily load following pages using the
 * stored nextPageToken.
 */
public class YouTubePageIterator<T extends GenericJson> implements PageIterator<T> {

    @FunctionalInterface
    private interface RequestExecutor<T> {
        Page<T> execute(String pageToken) throws IOException;
    }

    private record Page<T>(List<T> items, String nextToken) {}

    public final String INIT_TOKEN = "INIT";

    private final RequestExecutor<T> executor;
    private String nextToken = INIT_TOKEN;

    @SuppressWarnings("unchecked")
    public YouTubePageIterator(YouTube.Channels.List request) {
        this.executor = token -> {
            if (isTokenAvailable(token)) request.setPageToken(token);
            ChannelListResponse resp = request.execute();
            return new Page<>((List<T>) Optional.ofNullable(resp.getItems()).orElse(List.of()), resp.getNextPageToken());
        };
    }

    @SuppressWarnings("unchecked")
    public YouTubePageIterator(YouTube.Playlists.List request) {
        this.executor = token -> {
            if (isTokenAvailable(token)) request.setPageToken(token);
            PlaylistListResponse resp = request.execute();
            return new Page<>((List<T>) Optional.ofNullable(resp.getItems()).orElse(List.of()), resp.getNextPageToken());
        };
    }

    @SuppressWarnings("unchecked")
    public YouTubePageIterator(YouTube.Videos.List request) {
        this.executor = token -> {
            if (isTokenAvailable(token)) {
                request.setPageToken(token);
            }
            VideoListResponse resp = request.execute();
            return new Page<>((List<T>) Optional.ofNullable(resp.getItems()).orElse(List.of()), resp.getNextPageToken());
        };
    }

    @SuppressWarnings("unchecked")
    public YouTubePageIterator(YouTube.Search.List request) {
        this.executor = token -> {
            if (isTokenAvailable(token)) request.setPageToken(token);
            SearchListResponse resp = request.execute();
            return new Page<>((List<T>) Optional.ofNullable(resp.getItems()).orElse(List.of()), resp.getNextPageToken());
        };
    }

    @SuppressWarnings("unchecked")
    public YouTubePageIterator(YouTube.PlaylistItems.List request) {
        this.executor = token -> {
            if (isTokenAvailable(token)) request.setPageToken(token);
            PlaylistItemListResponse resp = request.execute();
            return new Page<>((List<T>) Optional.ofNullable(resp.getItems()).orElse(List.of()), resp.getNextPageToken());
        };
    }

    @SuppressWarnings("unchecked")
    public YouTubePageIterator(YouTube.ChannelSections.List request) {
        this.executor = token -> {
            if (!isTokenAvailable(token) && !INIT_TOKEN.equals(token)) return new Page<>(List.of(), null);
            ChannelSectionListResponse resp = request.execute();
            return new Page<>((List<T>) Optional.ofNullable(resp.getItems()).orElse(List.of()), null);
        };
    }

    private boolean isTokenAvailable(String token) {
        return token != null && !token.isBlank() && !INIT_TOKEN.equals(token);
    }

    @Override
    public List<T> nextPageItems() throws IOException {
        List<T> currentItems;
        synchronized (this) {
            Page<T> p = executor.execute(nextPageToken());
            this.nextToken = p.nextToken();
            currentItems = p.items();
        }
        return currentItems;
    }

    @Override
    public String nextPageToken() {
        return this.nextToken;
    }
}
