package org.bgf.youtube.service;

import org.bgf.youtube.api.YouTubeVideo;
import org.bgf.youtube.api.client.YouTubeClient;
import org.bgf.youtube.client.MockYouTubeClient;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultYouTubeFetcherVideoTest {

    @Test
    void filtersPrivateVideos() {
        var client = new MockYouTubeClient();
        client.videos = List.of(
                new YouTubeClient.VideoDTO("v1", "Public 1", "d", "en", Instant.EPOCH, 10, 1, 0, "public"),
                new YouTubeClient.VideoDTO("v2", "Private", "d", "en", Instant.EPOCH, 10, 1, 0, "private"),
                new YouTubeClient.VideoDTO("v3", "Unlisted", "d", "en", Instant.EPOCH, 10, 1, 0, "unlisted")
        );
        var fetcher = new DefaultYouTubeFetcher(client);
        List<YouTubeVideo> videos = fetcher.getVideos("UCX");
        assertEquals(2, videos.size(), "Should include public and unlisted, exclude private");
        assertTrue(videos.stream().anyMatch(v -> v.getId().equals("v1")));
        assertTrue(videos.stream().anyMatch(v -> v.getId().equals("v3")));
        assertFalse(videos.stream().anyMatch(v -> v.getId().equals("v2")));
    }

    // getVideoCount moved to YouTubeChannel; covered in channel tests
}
