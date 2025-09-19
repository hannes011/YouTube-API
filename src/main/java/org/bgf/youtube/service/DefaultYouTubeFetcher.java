package org.bgf.youtube.service;

import org.bgf.youtube.api.YouTubeChannel;
import org.bgf.youtube.api.YouTubeFetcher;
import org.bgf.youtube.api.YouTubePlaylist;
import org.bgf.youtube.api.YouTubeVideo;
import org.bgf.youtube.api.client.YouTubeClient;
import org.bgf.youtube.model.LazyYouTubeChannel;
import org.bgf.youtube.model.LazyYouTubePlaylist;
import org.bgf.youtube.model.LazyYouTubeVideo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultYouTubeFetcher implements YouTubeFetcher {

    private final YouTubeClient client;

    public DefaultYouTubeFetcher(YouTubeClient client) {
        this.client = client;
    }

    @Override
    public YouTubeChannel getChannel(String channelId) {
        var dto = client.getChannel(channelId);
        if (dto == null) {
            return null;
        }
        return new LazyYouTubeChannel(dto.channelId(), dto, client);
    }

    @Override
    public List<YouTubeChannel> getAssociatedChannels(String channelId) {
        var channels = client.listAssociatedChannels(channelId);
        if (channels == null || channels.isEmpty()) {
            return List.of();
        }
        return channels.stream()
                .filter(Objects::nonNull)
                .map(c -> new LazyYouTubeChannel(c.channelId(), c, client))
                .collect(Collectors.toList());
    }

    @Override
    public List<YouTubeVideo> getVideos(String channelId) {
        var videos = client.listAllVideos(channelId);
        if (videos == null || videos.isEmpty()) {
            return List.of();
        }
        return videos.stream()
                .filter(Objects::nonNull)
                .filter(v -> !"private".equalsIgnoreCase(v.visibility()))
                .map(v -> new LazyYouTubeVideo(v.id(), v, client))
                .collect(Collectors.toList());
    }

    @Override
    public List<YouTubePlaylist> getPlayLists(String channelId) {
        var playlists = client.listPlaylists(channelId);
        if (playlists == null || playlists.isEmpty()) {
            return List.of();
        }
        return playlists.stream()
                .filter(Objects::nonNull)
                .filter(pl -> !"private".equalsIgnoreCase(pl.visibility()))
                .filter(pl -> !pl.isUploadsList())
                .map(pl -> new LazyYouTubePlaylist(pl.playlistId(), pl, client))
                .collect(Collectors.toList());
    }

    @Override
    public String getChannelIdForChannelHandle(String handle) {
        return client.getChannelId(handle);
    }
}
