package org.bgf.youtube.api;

import java.io.File;
import java.time.Instant;
import java.util.List;

public interface YouTubeVideo {

	String getId();

	String getTitle();

	String getDescription();

	String getLanguage();

	Instant getPublishedAt();

	int getDurationInSeconds();

	int getViewCount();

	int getCommentCount();

	List<String> getSubtitleLanguages();

	File fetchThumbnail(YouTubeThumbnailType thumbnailType);

}
