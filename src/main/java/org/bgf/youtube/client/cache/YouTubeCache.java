package org.bgf.youtube.client.cache;

import org.bgf.youtube.api.client.YouTubeClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class YouTubeCache {
    private final Map<String, YouTubeClient.ChannelDTO> channels = new ConcurrentHashMap<>();
    private final Map<String, YouTubeClient.VideoDTO> videos = new ConcurrentHashMap<>();
    private final Map<String, YouTubeClient.PlaylistDTO> playlists = new ConcurrentHashMap<>();

    public YouTubeClient.ChannelDTO getChannel(String id) {
        return channels.get(id);
    }

    public void putChannel(YouTubeClient.ChannelDTO dto) {
        if (dto != null) channels.put(dto.channelId(), dto);
    }

    public YouTubeClient.VideoDTO getVideo(String id) {
        return videos.get(id);
    }

    public void putVideo(YouTubeClient.VideoDTO dto) {
        if (dto != null) videos.put(dto.id(), dto);
    }

    public YouTubeClient.PlaylistDTO getPlaylist(String id) {
        return playlists.get(id);
    }

    public void putPlaylist(YouTubeClient.PlaylistDTO dto) {
        if (dto != null) playlists.put(dto.playlistId(), dto);
    }

    public void clear() {
        channels.clear();
        videos.clear();
        playlists.clear();
    }
}
