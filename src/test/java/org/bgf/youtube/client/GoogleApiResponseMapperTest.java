package org.bgf.youtube.client;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GoogleApiResponseMapperTest {

    private final GoogleApiResponseMapper mapper = new GoogleApiResponseMapper();


    @Test
    void mapsChannelDTOFromSnippet() {
        var snippet = new ChannelSnippet();
        snippet.setTitle("Title");
        snippet.setDefaultLanguage("en");
        var c = new Channel();
        c.setId("UCX");
        c.setSnippet(snippet);
        var dto = mapper.channelDTO(c);
        assertEquals("UCX", dto.channelId());
        assertEquals("Title", dto.title());
        assertEquals("en", dto.language());
    }

    @Test
    void mapsVideoDTO() {
        var v = new Video();
        v.setId("v1");
        var vs = new VideoSnippet();
        vs.setTitle("T");
        vs.setDescription("D");
        vs.setDefaultAudioLanguage("en");
        vs.setPublishedAt(new DateTime(Instant.EPOCH.toEpochMilli()));
        v.setSnippet(vs);
        var cd = new VideoContentDetails();
        cd.setDuration("PT1M5S");
        v.setContentDetails(cd);
        var st = new VideoStatistics();
        st.setViewCount(new BigInteger("100"));
        st.setCommentCount(new BigInteger("2"));
        v.setStatistics(st);
        var status = new VideoStatus();
        status.setPrivacyStatus("unlisted");
        v.setStatus(status);
        var dto = mapper.videoDTO(v);
        assertEquals("v1", dto.id());
        assertEquals("T", dto.title());
        assertEquals("D", dto.description());
        assertEquals("en", dto.language());
        assertEquals(Instant.EPOCH, dto.publishedAt());
        assertEquals(65, dto.durationSeconds());
        assertEquals(100, dto.viewCount());
        assertEquals(2, dto.commentCount());
        assertEquals("unlisted", dto.visibility());
    }

    @Test
    void mapsPlaylistDTOAndFlagsUploads() {
        var ps = new PlaylistSnippet();
        ps.setTitle("PL");
        ps.setDescription("D");
        ps.setPublishedAt(new DateTime(Instant.EPOCH.toEpochMilli()));
        var status = new PlaylistStatus();
        status.setPrivacyStatus("public");
        var pl = new Playlist();
        pl.setId("PL_UPLOADS");
        pl.setSnippet(ps);
        pl.setStatus(status);
        var dto = mapper.playlistDTO(pl, "PL_UPLOADS");
        assertEquals("PL_UPLOADS", dto.playlistId());
        assertTrue(dto.isUploadsList());
    }
}
