package org.bgf.youtube.service;

import org.bgf.youtube.service.DurationParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DurationParserTest {

    @Test
    void parsesHoursMinutesSeconds() {
        assertEquals(3661, DurationParser.parseDurationSeconds("PT1H1M1S"));
    }

    @Test
    void parsesMinutesSeconds() {
        assertEquals(125, DurationParser.parseDurationSeconds("PT2M5S"));
    }

    @Test
    void handlesEmptyOrInvalid() {
        assertEquals(0, DurationParser.parseDurationSeconds(null));
        assertEquals(0, DurationParser.parseDurationSeconds(""));
        assertEquals(0, DurationParser.parseDurationSeconds("1H1M1S"));
    }
}

