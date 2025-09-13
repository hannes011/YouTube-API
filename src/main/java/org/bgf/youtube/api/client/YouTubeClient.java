package org.bgf.youtube.api.client;

import java.time.Instant;
import java.util.List;

/**
 * Abstraction over the transport/API layer. Implementations may call
 * google-api-services-youtube or any other backend. Stateless by design.
 * API keys are supplied via an {@link ApiKeyProvider} at construction time.
 */
public interface YouTubeClient {

    void flushCache();

    record ChannelDTO(String channelId, String title, String language,
                      int videoCount, int subscriberCount, int viewCount) {}

    record VideoDTO(String id, String title, String description, String language,
                    Instant publishedAt, int durationSeconds,
                    int viewCount, int commentCount, List<String> subtitleLangs,
                    String visibility) {} // visibility: public|unlisted|private

    record PlaylistDTO(String playlistId, String name, String description,
                       Instant publishedAt, String visibility, boolean isUploadsList,
                       int itemCount) {}

    String getChannelId(String channelHandle);

    List<ChannelDTO> listAssociatedChannels(String channelId);

    ChannelDTO getChannel(String channelId);

    List<ChannelDTO> listChannels(List<String> channelIds);

    String getUploadsPlaylistId(String channelId);

    List<VideoDTO> listAllVideos(String channelId);

    List<VideoDTO> listPlaylistVideos(String playlistId);

    List<VideoDTO> listVideos(List<String> videoIds);

    VideoDTO getVideo(String videoId);

    List<PlaylistDTO> listPlaylists(String channelId);
}
