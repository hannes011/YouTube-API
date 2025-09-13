package org.bgf.youtube.service;

import org.bgf.youtube.api.YouTubeChannel;
import org.bgf.youtube.api.client.YouTubeClient;
import org.bgf.youtube.client.MockYouTubeClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultYouTubeFetcherChannelTest {

    @Test
    void mapsChannelsFromClient() {
        var client = new MockYouTubeClient();
        client.channels = List.of(
                new YouTubeClient.ChannelDTO("UC1", "Title 1", "en", 10, 100, 1000),
                new YouTubeClient.ChannelDTO("UC2", "Title 2", "de", 5, 50, 500)
        );
        var fetcher = new DefaultYouTubeFetcher(client);
        List<YouTubeChannel> result = fetcher.getAssociatedChannels("UCX");
        assertEquals(2, result.size());
        assertEquals("UC1", result.getFirst().getChannelId());
        assertEquals("Title 1", result.getFirst().getTitle());
        assertEquals("en", result.getFirst().getLanguage());
        assertEquals(10, result.getFirst().getVideoCount());
        assertEquals(100, result.getFirst().getSubscriberCount());
        assertEquals(1000, result.getFirst().getViewCount());
    }
}
