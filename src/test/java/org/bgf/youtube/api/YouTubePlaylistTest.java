package org.bgf.youtube.api;

import org.bgf.youtube.api.client.YouTubeClient;
import org.bgf.youtube.client.MockYouTubeClient;
import org.bgf.youtube.model.LazyYouTubePlaylist;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class YouTubePlaylistTest {

    @Test
    void gettersAndFetchVideos_returnProvidedValues() {
        var client = new MockYouTubeClient();
        var preload = new YouTubeClient.PlaylistDTO("PL1", "Playlist", "desc", Instant.EPOCH, "public", false, 3);
        var pl = new LazyYouTubePlaylist("PL1", preload, client);
        assertEquals("PL1", pl.getPlaylistId());
        assertEquals("Playlist", pl.getName());
        assertEquals("desc", pl.getDescription());
        assertEquals(Instant.EPOCH, pl.getPublishedAt());
        assertEquals(3, pl.getVideoCount());
    }
}
