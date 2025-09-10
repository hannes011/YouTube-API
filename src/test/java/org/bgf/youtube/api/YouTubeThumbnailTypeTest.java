package org.bgf.youtube.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YouTubeThumbnailTypeTest {

    @Test
    void enumContainsExpectedValues() {
        var values = YouTubeThumbnailType.values();
        assertEquals(5, values.length);
        assertNotNull(YouTubeThumbnailType.MIN);
        assertNotNull(YouTubeThumbnailType.DEFAULT);
        assertNotNull(YouTubeThumbnailType.MEDIUM);
        assertNotNull(YouTubeThumbnailType.HIGH);
        assertNotNull(YouTubeThumbnailType.MAXRES);
    }
}

