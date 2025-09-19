package org.bgf.youtube.model;

import org.bgf.youtube.api.YouTubeChannel;
import org.bgf.youtube.api.client.YouTubeClient;

public class LazyYouTubeChannel implements YouTubeChannel {
    private final String channelId;
    private final YouTubeClient client;
    // cache handled by client internally; kept only for backward compat if needed (not used)

    private volatile String title;
    private volatile String handle;
    private volatile String language;
    private volatile Integer videoCount;
    private volatile Integer subscriberCount;
    private volatile Integer viewCount;

    public LazyYouTubeChannel(String channelId, YouTubeClient.ChannelDTO preload, YouTubeClient client) {
        this.channelId = channelId;
        this.client = client;
        if (preload != null) apply(preload);
    }

    public LazyYouTubeChannel(String channelId, YouTubeClient client) {
        this(channelId, null, client);
    }

    private void apply(YouTubeClient.ChannelDTO dto) {
        this.title = dto.title();
        this.handle = dto.handle();
        this.language = dto.language();
        this.videoCount = dto.videoCount();
        this.subscriberCount = dto.subscriberCount();
        this.viewCount = dto.viewCount();
    }

    @Override
    public String getChannelId() {
        return channelId;
    }

    @Override
    public String getTitle() {
        ensureLoaded();
        return title;
    }

    @Override
    public String getChannelHandle() {
        ensureLoaded();
        return handle;
    }

    @Override
    public String getLanguage() {
        ensureLoaded();
        return language;
    }

    @Override
    public int getVideoCount() {
        ensureLoaded();
        return videoCount == null ? 0 : videoCount;
    }

    @Override
    public int getSubscriberCount() {
        ensureLoaded();
        return subscriberCount == null ? 0 : subscriberCount;
    }

    @Override
    public int getViewCount() {
        ensureLoaded();
        return viewCount == null ? 0 : viewCount;
    }

    private void ensureLoaded() {
        if (title != null && language != null && videoCount != null && subscriberCount != null && viewCount != null) return;
        synchronized (this) {
            if (title == null || language == null || videoCount == null || subscriberCount == null || viewCount == null) {
                var dto = client.getChannel(channelId);
                if (dto != null) apply(dto);
            }
        }
    }
}
