package org.bgf.youtube.api;

import org.bgf.youtube.service.DefaultYouTubeFetcher;

import java.util.List;

public interface YouTubeFetcher {

	/**
	 * Initialize the fetcher with the given API key.
	 * @param apiKey the API key to use for requests
	 * @return the fetcher instance
	 */
	static YouTubeFetcher create(String apiKey) {
		return new DefaultYouTubeFetcher(apiKey);
	}

	/**
	 * Get all channels associated with the given channel (e.g. brand channels).
	 * @param channelId the channel to get the associated channels for
	 * @return a list of channels
	 */
	List<YouTubeChannel> getAssociatedChannels(String channelId);

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
