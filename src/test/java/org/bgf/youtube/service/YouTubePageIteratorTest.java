package org.bgf.youtube.service;

import org.bgf.youtube.api.client.PageIterator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class YouTubePageIteratorTest {

    @Test
    void iteratesPagesLazily() {
        // Build a test iterator using a functional constructor
        var tokenRef = new AtomicReference<String>(null);
        PageIterator<String> it = new PageIterator<>() {
            private String token = "p2"; // next page token after first page
            private boolean first = true;
            @Override public List<String> nextPageItems() {
                if (first) {
                    first = false;
                    return List.of("a", "b");
                } else if (token != null) {
                    token = null;
                    return List.of("c");
                } else {
                    return List.of();
                }
            }
            @Override public String nextPageToken() { return token; }
        };

        var page1 = it.next();
        assertEquals(List.of("a", "b"), page1);
        assertTrue(it.hasNext());
        var page2 = it.next();
        assertEquals(List.of("c"), page2);
        assertFalse(it.hasNext());
    }
}

