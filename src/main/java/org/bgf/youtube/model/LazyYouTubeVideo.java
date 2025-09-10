package org.bgf.youtube.model;

import org.bgf.youtube.api.YouTubeThumbnail;
import org.bgf.youtube.api.YouTubeThumbnailType;
import org.bgf.youtube.api.YouTubeVideo;
import org.bgf.youtube.api.client.YouTubeClient;
import org.bgf.youtube.api.client.UrlFetcher;
import org.bgf.youtube.client.HttpUrlFetcher;

import java.time.Instant;
import java.util.List;

public class LazyYouTubeVideo implements YouTubeVideo {
    private final String id;
    private final YouTubeClient client;
    private final UrlFetcher urlFetcher;

    private volatile String title;
    private volatile String description;
    private volatile String language;
    private volatile Instant publishedAt;
    private volatile Integer durationInSeconds;
    private volatile Integer viewCount;
    private volatile Integer commentCount;
    private volatile List<String> subtitleLanguages;

    public LazyYouTubeVideo(String id, YouTubeClient.VideoDTO preload, YouTubeClient client) {
        this.id = id;
        this.client = client;
        this.urlFetcher = new HttpUrlFetcher();
        if (preload != null) apply(preload);
    }

    public LazyYouTubeVideo(String id, YouTubeClient.VideoDTO preload, YouTubeClient client, UrlFetcher urlFetcher) {
        this.id = id;
        this.client = client;
        this.urlFetcher = urlFetcher == null ? new HttpUrlFetcher() : urlFetcher;
        if (preload != null) apply(preload);
    }

    private void apply(YouTubeClient.VideoDTO dto) {
        this.title = dto.title();
        this.description = dto.description();
        this.language = dto.language();
        this.publishedAt = dto.publishedAt();
        this.durationInSeconds = dto.durationSeconds();
        this.viewCount = dto.viewCount();
        this.commentCount = dto.commentCount();
        this.subtitleLanguages = dto.subtitleLangs();
    }

    private void ensureLoaded() {
        if (title != null && description != null && language != null && publishedAt != null && durationInSeconds != null && viewCount != null && commentCount != null && subtitleLanguages != null)
            return;
        synchronized (this) {
            if (title == null || description == null || language == null || publishedAt == null || durationInSeconds == null || viewCount == null || commentCount == null || subtitleLanguages == null) {
                var dto = client.getVideo(id);
                if (dto != null) apply(dto);
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitle() {
        ensureLoaded();
        return title;
    }

    @Override
    public String getDescription() {
        ensureLoaded();
        return description;
    }

    @Override
    public String getLanguage() {
        ensureLoaded();
        return language;
    }

    @Override
    public Instant getPublishedAt() {
        ensureLoaded();
        return publishedAt;
    }

    @Override
    public int getDurationInSeconds() {
        ensureLoaded();
        return durationInSeconds == null ? 0 : durationInSeconds;
    }

    @Override
    public int getViewCount() {
        ensureLoaded();
        return viewCount == null ? 0 : viewCount;
    }

    @Override
    public int getCommentCount() {
        ensureLoaded();
        return commentCount == null ? 0 : commentCount;
    }

    @Override
    public List<String> getSubtitleLanguages() {
        ensureLoaded();
        return subtitleLanguages == null ? List.of() : subtitleLanguages;
    }

    @Override
    public YouTubeThumbnail getThumbnail(YouTubeThumbnailType thumbnailType) {
        return new LazyYouTubeThumbnail(this.id, thumbnailType, urlFetcher);
    }
}
