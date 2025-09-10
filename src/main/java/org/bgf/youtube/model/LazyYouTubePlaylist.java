package org.bgf.youtube.model;

import org.bgf.youtube.api.YouTubePlaylist;
import org.bgf.youtube.api.YouTubeVideo;
import org.bgf.youtube.api.client.YouTubeClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class LazyYouTubePlaylist implements YouTubePlaylist {
    private final String playlistId;
    private final YouTubeClient client;

    private final String name;
    private final String description;
    private final Instant publishedAt;
    private volatile List<YouTubeVideo> videos; // cached resolved videos

    public LazyYouTubePlaylist(String playlistId, String name, String description, Instant publishedAt, YouTubeClient client) {
        this.playlistId = playlistId;
        this.name = name;
        this.description = description;
        this.publishedAt = publishedAt;
        this.client = client;
    }

    @Override
    public String getPlaylistId() {
        return playlistId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Instant getPublishedAt() {
        return publishedAt;
    }

    @Override
    public List<YouTubeVideo> fetchVideos() {
        if (videos != null) return videos;
        synchronized (this) {
            if (videos == null) {
                List<YouTubeClient.VideoDTO> dtos = client.listPlaylistVideos(playlistId);
                videos = mapVideos(dtos);
            }
        }
        return videos;
    }

    private List<YouTubeVideo> mapVideos(List<YouTubeClient.VideoDTO> dtos) {
        List<YouTubeVideo> list = new ArrayList<>(dtos.size());
        for (var dto : dtos) {
            list.add(new LazyYouTubeVideo(dto.id(), dto, client));
        }
        return list;
    }
}
