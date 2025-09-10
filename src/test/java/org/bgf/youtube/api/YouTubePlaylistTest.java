package org.bgf.youtube.api;

import org.bgf.youtube.client.MockYouTubeClient;
import org.bgf.youtube.model.LazyYouTubePlaylist;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class YouTubePlaylistTest {

    @Test
    void gettersAndFetchVideos_returnProvidedValues() {
        var client = new MockYouTubeClient();
        var pl = new LazyYouTubePlaylist("PL1", "Playlist", "desc", Instant.EPOCH, client);
        assertEquals("PL1", pl.getPlaylistId());
        assertEquals("Playlist", pl.getName());
        assertEquals("desc", pl.getDescription());
        assertEquals(Instant.EPOCH, pl.getPublishedAt());
    }
}
