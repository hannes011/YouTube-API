package org.bgf.youtube.api;

import com.google.api.services.youtube.YouTube;
import org.bgf.youtube.auth.YouTubeServiceInitializer;
import org.bgf.youtube.client.GoogleApiYouTubeClient;
import org.bgf.youtube.service.DefaultYouTubeFetcher;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface YouTubeFetcher {

    /**
     * Initialize the fetcher with the given API key.
     * @param apiKey the API key to use for requests
     * @return the fetcher instance
     */
    static YouTubeFetcher create(String apiKey) {
        try {
            return new DefaultYouTubeFetcher(new GoogleApiYouTubeClient(YouTubeServiceInitializer.initServiceWithApiKey("bgf-youtube-api", apiKey)));
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to init YouTube client", e);
        }
    }

    /**
     * Initialize the fetcher.
     * @param app e.g. use org.bgf.youtube.auth.YouTubeServiceInitializer for initialization
     * @return the fetcher instance
     */
    static YouTubeFetcher create(YouTube app) {
        return new DefaultYouTubeFetcher(new GoogleApiYouTubeClient(app));
    }

    /**
     * Get all channels associated with the given channel (e.g. brand channels).
     * @param channelId the channel to get the associated channels for
     * @return a list of channels
     */
    List<YouTubeChannel> getAssociatedChannels(String channelId);

    /**
     * Get a channel by the given channel ID.
     * @param channelId ID of the channel to get
     * @return a channel
     */
    YouTubeChannel getChannel(String channelId);

    /**
     * Get all videos from the channel (not including private ones).
     * @param channelId the channel to get the videos for
     * @return a list of videos, or an empty list if the channel does not exist
     */
    List<YouTubeVideo> getVideos(String channelId);

    /**
     * Get all playlists from the channel (excluding private ones and the uploads playlist).
     * @param channelId the channel to get the playlists for
     * @return a list of playlists, or an empty list if the channel does not exist
     */
    List<YouTubePlaylist> getPlayLists(String channelId);

    /**
     * Get the channel ID for the given handle (channel name starting with "@").
     * @param handle channel handle starting with "@"
     * @return the channel ID or null
     */
    String getChannelIdForChannelHandle(String handle);
}
