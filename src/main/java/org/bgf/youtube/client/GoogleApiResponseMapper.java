package org.bgf.youtube.client;

import com.google.api.services.youtube.model.*;
import org.bgf.youtube.api.client.YouTubeClient;
import org.bgf.youtube.service.DurationParser;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Maps YouTube API v3 responses to internal DTOs and centralizes pagination handling.
 */
public class GoogleApiResponseMapper {

    public YouTubeClient.ChannelDTO channelDTO(Channel channel) {
        return new YouTubeClient.ChannelDTO(
                channel.getId(),
                opt(channel.getSnippet()).map(ChannelSnippet::getTitle).orElse(""),
                opt(channel.getSnippet()).map(ChannelSnippet::getDefaultLanguage).orElse(null),
                opt(channel.getStatistics()).map(ChannelStatistics::getVideoCount).map(Number::intValue).orElse(0),
                opt(channel.getStatistics()).map(ChannelStatistics::getSubscriberCount).map(Number::intValue).orElse(0),
                opt(channel.getStatistics()).map(ChannelStatistics::getViewCount).map(Number::intValue).orElse(0)
        );
    }

    public YouTubeClient.VideoDTO videoDTO(Video v) {
        return new YouTubeClient.VideoDTO(
                v.getId(),
                opt(v.getSnippet()).map(VideoSnippet::getTitle).orElse(""),
                opt(v.getSnippet()).map(VideoSnippet::getDescription).orElse(""),
                opt(v.getSnippet()).map(VideoSnippet::getDefaultAudioLanguage).orElse(null),
                opt(v.getSnippet()).map(VideoSnippet::getPublishedAt).map(dt -> Instant.ofEpochMilli(dt.getValue())).orElse(null),
                DurationParser.parseDurationSeconds(opt(v.getContentDetails()).map(VideoContentDetails::getDuration).orElse(null)),
                opt(v.getStatistics()).map(VideoStatistics::getViewCount).map(Number::intValue).orElse(0),
                opt(v.getStatistics()).map(VideoStatistics::getCommentCount).map(Number::intValue).orElse(0),
                List.of(),
                opt(v.getStatus()).map(VideoStatus::getPrivacyStatus).orElse("public")
        );
    }

    public YouTubeClient.PlaylistDTO playlistDTO(Playlist pl, String uploadsPlId) {
        return new YouTubeClient.PlaylistDTO(
                pl.getId(),
                opt(pl.getSnippet()).map(PlaylistSnippet::getTitle).orElse(""),
                opt(pl.getSnippet()).map(PlaylistSnippet::getDescription).orElse(""),
                opt(pl.getSnippet()).map(PlaylistSnippet::getPublishedAt).map(dt -> Instant.ofEpochMilli(dt.getValue())).orElse(null),
                opt(pl.getStatus()).map(PlaylistStatus::getPrivacyStatus).orElse("public"),
                uploadsPlId != null && uploadsPlId.equals(pl.getId()),
                opt(pl.getContentDetails()).map(PlaylistContentDetails::getItemCount).map(Number::intValue).orElse(0)
        );
    }

    private static <T> Optional<T> opt(T v) { return Optional.ofNullable(v); }
}
