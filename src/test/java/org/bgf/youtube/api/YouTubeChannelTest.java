package org.bgf.youtube.api;

import org.bgf.youtube.api.client.YouTubeClient;
import org.bgf.youtube.client.MockYouTubeClient;
import org.bgf.youtube.model.LazyYouTubeChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YouTubeChannelTest {

    @Test
    void getters_returnProvidedValues() {
        var client = new MockYouTubeClient();
        var preload = new YouTubeClient.ChannelDTO("UC123", "BGF Channel", "en", 7, 100, 1000);
        var ch = new LazyYouTubeChannel("UC123", preload, client);
        assertEquals("UC123", ch.getChannelId());
        assertEquals("BGF Channel", ch.getTitle());
        assertEquals("en", ch.getLanguage());
        assertEquals(7, ch.getVideoCount());
        assertEquals(100, ch.getSubscriberCount());
        assertEquals(1000, ch.getViewCount());
    }
}
