package org.bgf.youtube.service;

import org.bgf.youtube.client.MockYouTubeClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultYouTubeFetcherTest {

	@Test
	void createAndCallMethods_returnsEmptyLists() {
        var fetcher = new DefaultYouTubeFetcher(new MockYouTubeClient());
		assertNotNull(fetcher);
		assertTrue(fetcher.getAssociatedChannels("UC123").isEmpty());
		assertTrue(fetcher.getVideos("UC123").isEmpty());
		assertTrue(fetcher.getPlayLists("UC123").isEmpty());
	}
}
