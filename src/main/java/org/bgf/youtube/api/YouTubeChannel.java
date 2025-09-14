package org.bgf.youtube.api;

public interface YouTubeChannel {

    String getChannelId();

    String getChannelHandle();

    String getTitle();

    String getLanguage();

    int getVideoCount();

    int getSubscriberCount();

    int getViewCount();

}
