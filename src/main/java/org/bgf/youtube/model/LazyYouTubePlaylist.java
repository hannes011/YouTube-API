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

    private volatile String name;
    private volatile String description;
    private volatile Instant publishedAt;
    private volatile Integer itemCount;
    private volatile List<YouTubeVideo> videos; // cached resolved videos

    public LazyYouTubePlaylist(String playlistId, YouTubeClient.PlaylistDTO preload, YouTubeClient client) {
        this.playlistId = playlistId;
        this.client = client;
        if (preload != null) apply(preload);
    }

    private void apply(YouTubeClient.PlaylistDTO dto) {
        this.name = dto.name();
        this.description = dto.description();
        this.publishedAt = dto.publishedAt();
        this.itemCount = dto.itemCount();
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
    public int getVideoCount() {
        return itemCount == null ? 0 : itemCount;
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
