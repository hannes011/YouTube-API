package org.bgf.youtube.model;

import org.bgf.youtube.api.YouTubeChannel;
import org.bgf.youtube.api.client.YouTubeClient;

public class LazyYouTubeChannel implements YouTubeChannel {
    private final String channelId;
    private final YouTubeClient client;
    // cache handled by client internally; kept only for backward compat if needed (not used)

    private volatile String title;
    private volatile String language;

    public LazyYouTubeChannel(String channelId, String title, String language, YouTubeClient client) {
        this.channelId = channelId;
        this.title = title;
        this.language = language;
        this.client = client;
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
    public String getLanguage() {
        ensureLoaded();
        return language;
    }

    private void ensureLoaded() {
        if (title != null && language != null) return;
        synchronized (this) {
            if (title == null || language == null) {
                var dto = client.getChannel(channelId);
                if (dto != null) {
                    this.title = dto.title();
                    this.language = dto.language();
                }
            }
        }
    }
}
