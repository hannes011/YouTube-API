package org.bgf.youtube.api;

import java.time.Instant;
import java.util.List;

public interface YouTubePlaylist {

	String getPlaylistId();

	String getName();

	String getDescription();

	Instant getPublishedAt();

	int getVideoCount();

	List<YouTubeVideo> fetchVideos();
}
