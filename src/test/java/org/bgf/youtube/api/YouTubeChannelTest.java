package org.bgf.youtube.api;

import org.bgf.youtube.client.MockYouTubeClient;
import org.bgf.youtube.model.LazyYouTubeChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YouTubeChannelTest {

    @Test
    void getters_returnProvidedValues() {
        var client = new MockYouTubeClient();
        var ch = new LazyYouTubeChannel("UC123", "BGF Channel", "en", client);
        assertEquals("UC123", ch.getChannelId());
        assertEquals("BGF Channel", ch.getTitle());
        assertEquals("en", ch.getLanguage());
    }
}
