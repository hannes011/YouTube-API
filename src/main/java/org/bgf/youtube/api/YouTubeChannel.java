package org.bgf.youtube.api;

public interface YouTubeChannel {

	String getChannelId();

	String getTitle();

	String getLanguage();

	int getVideoCount();

	int getSubscriberCount();

	int getViewCount();

}
