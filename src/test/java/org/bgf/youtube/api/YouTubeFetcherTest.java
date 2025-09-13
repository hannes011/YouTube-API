package org.bgf.youtube.api;

import org.bgf.youtube.api.client.YouTubeClient;
import org.bgf.youtube.service.DefaultYouTubeFetcher;
import org.bgf.youtube.client.MockYouTubeClient;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class YouTubeFetcherTest {

    @Test
    void create_initializesFetcherWithApiKey() {
        String key = "test-api-key";
        YouTubeFetcher fetcher = YouTubeFetcher.create(key);
        assertNotNull(fetcher, "create(...) must return a fetcher instance");
        assertTrue(fetcher instanceof DefaultYouTubeFetcher, "Factory returns default implementation");
    }

    @Test
    void getAssociatedChannels_returnsList() {
        var fetcher = new DefaultYouTubeFetcher(new MockYouTubeClient());
        var channels = fetcher.getAssociatedChannels("UC_does_not_exist");
        assertNotNull(channels, "Must return a list, not null");
    }

    @Test
    void getVideos_excludesInvalidChannel_returnsEmptyListForNonexistentChannel() {
        var fetcher = new DefaultYouTubeFetcher(new MockYouTubeClient());
        var videos = fetcher.getVideos("UC_does_not_exist");
        assertNotNull(videos, "Must return a list, not null");
        assertTrue(videos.isEmpty(), "For non-existent channel, contract allows empty list");
    }

    @Test
    void getPlayLists_excludesUploads_returnsEmptyListForNonexistentChannel() {
        var fetcher = new DefaultYouTubeFetcher(new MockYouTubeClient());
        var playlists = fetcher.getPlayLists("UC_does_not_exist");
        assertNotNull(playlists, "Must return a list, not null");
        assertTrue(playlists.isEmpty(), "For non-existent channel, contract allows empty list");
    }

    @Test
    void getVideos_excludesPrivateVideos() {
        var mock = new MockYouTubeClient();
        mock.videos = List.of(
                new YouTubeClient.VideoDTO("v1", "Public", "d", "en", Instant.EPOCH, 10, 1, 0, List.of(), "public"),
                new YouTubeClient.VideoDTO("v2", "Private", "d", "en", Instant.EPOCH, 10, 1, 0, List.of(), "private")
        );
        var fetcher = new DefaultYouTubeFetcher(mock);
        var videos = fetcher.getVideos("UC_example");
        assertEquals(1, videos.size());
        assertEquals("v1", videos.getFirst().getId());
    }

    @Test
    void getPlayLists_excludesPrivateAndUploads() {
        var mock = new MockYouTubeClient();
        mock.playlists = List.of(
                new YouTubeClient.PlaylistDTO("PL_UPLOADS", "Uploads", "d", Instant.EPOCH, "public", true, 100),
                new YouTubeClient.PlaylistDTO("PL_PRIVATE", "Private", "d", Instant.EPOCH, "private", false, 5),
                new YouTubeClient.PlaylistDTO("PL_GOOD", "Public", "d", Instant.EPOCH, "public", false, 42)
        );
        var fetcher = new DefaultYouTubeFetcher(mock);
        var playlists = fetcher.getPlayLists("UC_example");
        assertEquals(1, playlists.size());
        assertEquals("PL_GOOD", playlists.getFirst().getPlaylistId());
    }

    @Test
    void getVideos_includesAllFromClient() {
        var mock = new MockYouTubeClient();
        mock.videos = List.of(
                new YouTubeClient.VideoDTO("v1", "T1", "d", "en", Instant.EPOCH, 10, 1, 0, List.of(), "public"),
                new YouTubeClient.VideoDTO("v2", "T2", "d", "en", Instant.EPOCH, 10, 1, 0, List.of(), "public")
        );
        var fetcher = new DefaultYouTubeFetcher(mock);
        var videos = fetcher.getVideos("UC_example");
        assertEquals(2, videos.size());
        assertTrue(videos.stream().anyMatch(v -> v.getId().equals("v1")));
        assertTrue(videos.stream().anyMatch(v -> v.getId().equals("v2")));
    }
}
