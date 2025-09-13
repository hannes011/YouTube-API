package org.bgf.youtube.service;

import org.bgf.youtube.api.YouTubePlaylist;
import org.bgf.youtube.api.client.YouTubeClient;
import org.bgf.youtube.client.MockYouTubeClient;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultYouTubeFetcherPlaylistTest {

    @Test
    void excludesPrivateAndUploads() {
        var client = new MockYouTubeClient();
        client.playlists = List.of(
                new YouTubeClient.PlaylistDTO("PL1", "Uploads", "d", Instant.EPOCH, "public", true, 100),
                new YouTubeClient.PlaylistDTO("PL2", "Private PL", "d", Instant.EPOCH, "private", false, 10),
                new YouTubeClient.PlaylistDTO("PL3", "Public PL", "d", Instant.EPOCH, "public", false, 3)
        );
        var fetcher = new DefaultYouTubeFetcher(client);
        List<YouTubePlaylist> playlists = fetcher.getPlayLists("UCX");
        assertEquals(1, playlists.size());
        assertEquals("PL3", playlists.getFirst().getPlaylistId());
        assertEquals(3, playlists.getFirst().getVideoCount());
    }
}
