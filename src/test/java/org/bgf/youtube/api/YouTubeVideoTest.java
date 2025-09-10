package org.bgf.youtube.api;

import org.bgf.youtube.api.client.UrlFetcher;
import org.bgf.youtube.api.client.YouTubeClient;
import org.bgf.youtube.client.MockYouTubeClient;
import org.bgf.youtube.model.LazyYouTubeVideo;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class YouTubeVideoTest {

    @Test
    void getters_returnConsistentMetadata() {
        var client = new MockYouTubeClient();
        var preload = new YouTubeClient.VideoDTO(
                "abc123", "Sample Video", "desc", "en", Instant.parse("2020-01-01T00:00:00Z"), 120, 10, 2, List.of("en"), "public");
        var v = new LazyYouTubeVideo("abc123", preload, client);
        assertEquals("abc123", v.getId());
        assertEquals("Sample Video", v.getTitle());
        assertEquals("desc", v.getDescription());
        assertEquals("en", v.getLanguage());
        assertEquals(Instant.parse("2020-01-01T00:00:00Z"), v.getPublishedAt());
        assertEquals(120, v.getDurationInSeconds());
        assertEquals(10, v.getViewCount());
        assertEquals(2, v.getCommentCount());
        assertEquals(List.of("en"), v.getSubtitleLanguages());
    }

    @Test
    void fetchThumbnail_returnsStreamPerType_usingInjectedUrlFetcher() {
        UrlFetcher fetcher = (URL url) -> new java.io.ByteArrayInputStream(new byte[]{1,2,3});
        var client = new MockYouTubeClient();
        var v = new LazyYouTubeVideo("abc123", null, client, fetcher);
        for (var t : YouTubeThumbnailType.values()) {
            try (InputStream in = v.getThumbnail(t).fetch()) {
                assertNotNull(in);
                assertEquals(3, in.readAllBytes().length);
            } catch (Exception e) {
                fail(e);
            }
        }
    }
}
