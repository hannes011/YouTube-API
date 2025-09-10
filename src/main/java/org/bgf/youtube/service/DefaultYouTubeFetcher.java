package org.bgf.youtube.service;

import org.bgf.youtube.api.YouTubeChannel;
import org.bgf.youtube.api.YouTubeFetcher;
import org.bgf.youtube.api.YouTubePlaylist;
import org.bgf.youtube.api.YouTubeVideo;
import org.bgf.youtube.api.client.YouTubeClient;
import org.bgf.youtube.client.GoogleApiYouTubeClient;
import org.bgf.youtube.model.LazyYouTubeChannel;
import org.bgf.youtube.model.LazyYouTubePlaylist;
import org.bgf.youtube.model.LazyYouTubeVideo;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultYouTubeFetcher implements YouTubeFetcher {

    private final YouTubeClient client;

    public DefaultYouTubeFetcher(String apiKey) {
        this(new GoogleApiYouTubeClient(() -> apiKey));
    }

    public DefaultYouTubeFetcher(YouTubeClient client) {
        this.client = client;
    }

    @Override
    public List<YouTubeChannel> getAssociatedChannels(String channelId) {
        return client.listAssociatedChannels(channelId).stream()
                .map(c -> new LazyYouTubeChannel(c.channelId(), c.title(), c.language(), client))
                .collect(Collectors.toList());
    }

    @Override
    public List<YouTubeVideo> getVideos(String channelId) {
        return client.listAllVideos(channelId).stream()
                .filter(v -> !"private".equalsIgnoreCase(v.visibility()))
                .map(v -> new LazyYouTubeVideo(v.id(), v, client))
                .collect(Collectors.toList());
    }

    @Override
    public List<YouTubePlaylist> getPlayLists(String channelId) {
        return client.listPlaylists(channelId).stream()
                .filter(pl -> !"private".equalsIgnoreCase(pl.visibility()))
                .filter(pl -> !pl.isUploadsList())
                .map(pl -> new LazyYouTubePlaylist(pl.playlistId(), pl.name(), pl.description(), pl.publishedAt(), client))
                .collect(Collectors.toList());
    }

    @Override
    public String getChannelIdForChannelHandle(String handle) {
        return client.getChannelId(handle);
    }
}
