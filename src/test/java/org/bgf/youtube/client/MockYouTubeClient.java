package org.bgf.youtube.client;

import org.bgf.youtube.api.client.YouTubeClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple test-only mock client that returns preconfigured pages and items.
 */
public class MockYouTubeClient implements YouTubeClient {

    public List<YouTubeClient.ChannelDTO> channels = List.of();
    public List<YouTubeClient.VideoDTO> videos = List.of();
    public List<YouTubeClient.PlaylistDTO> playlists = List.of();

    public Map<String, YouTubeClient.VideoDTO> videosById = new HashMap<>();
    public Map<String, YouTubeClient.ChannelDTO> channelsById = new HashMap<>();
    public Map<String, List<String>> captionLanguagesByVideoId = new HashMap<>();

    public MockYouTubeClient() {}

    @Override
    public void flushCache() {}

    @Override
    public String getChannelId(String channelHandle) {
        return channelHandle; // not important
    }

    @Override
    public List<YouTubeClient.ChannelDTO> listAssociatedChannels(String channelId) {
        return new ArrayList<>(channels);
    }

    @Override
    public List<YouTubeClient.VideoDTO> listAllVideos(String channelId) {
        return new ArrayList<>(videos);
    }

    @Override
    public List<YouTubeClient.PlaylistDTO> listPlaylists(String channelId) {
        return new ArrayList<>(playlists);
    }

    @Override
    public YouTubeClient.ChannelDTO getChannel(String channelId) {
        return channelsById.get(channelId);
    }

    @Override
    public List<ChannelDTO> listChannels(List<String> channelIds) {
        return null;
    }

    @Override
    public String getUploadsPlaylistId(String channelId) {
        return null;
    }

    @Override
    public List<YouTubeClient.VideoDTO> listPlaylistVideos(String playlistId) {
        return listAllVideos(playlistId);
    }

    @Override
    public List<VideoDTO> listVideos(List<String> videoIds) {
        return null;
    }

    @Override
    public YouTubeClient.VideoDTO getVideo(String videoId) {
        return videosById.get(videoId);
    }

    @Override
    public List<String> getCaptionLanguages(String videoId) {
        return captionLanguagesByVideoId.getOrDefault(videoId, List.of());
    }
}
