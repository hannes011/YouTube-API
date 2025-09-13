package org.bgf.youtube.client;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.bgf.youtube.api.client.ApiKeyProvider;
import org.bgf.youtube.api.client.YouTubeClient;
import org.bgf.youtube.client.cache.YouTubeCache;
import org.bgf.youtube.service.YouTubePageIterator;
import org.bgf.youtube.service.Batching;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * HTTP-backed YouTube client using google-api-services-youtube.
 * Designed to be stateless; the underlying YouTube service is lazily created per call.
 */
public class GoogleApiYouTubeClient implements YouTubeClient {
    private final YouTubeCache cache = new YouTubeCache();
    private final ApiKeyProvider apiKeyProvider;
    private final GoogleApiResponseMapper responseMapper = new GoogleApiResponseMapper();
    private YouTube youTubeApp;

    public GoogleApiYouTubeClient(ApiKeyProvider apiKeyProvider) {
        this.apiKeyProvider = apiKeyProvider;
    }

    private YouTube newService() {
        if (youTubeApp == null) {
            try {
                youTubeApp = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), request -> {
                }).setApplicationName("bgf-youtube-api").build();
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException("Failed to init YouTube client", e);
            }
        }
        return youTubeApp;
    }

    @Override
    public String getChannelId(String channelHandle) {
        try {
            var yt = newService();
            ChannelListResponse response = yt.channels()
                    .list(List.of("id"))
                    .setForHandle(channelHandle.startsWith("@") ? channelHandle.substring(1) : channelHandle)
                    .setKey(this.apiKeyProvider.get())
                    .execute();

            if (response.getItems().isEmpty()) {
                return null;
            }
            Channel ch = response.getItems().getFirst();
            return ch.getId();
        } catch (IOException e) {
            throw new RuntimeException("YouTube API error", e);
        }
    }

    @Override
    public List<ChannelDTO> listAssociatedChannels(String channelId) {
        try {
            var yt = newService();
            var sectionsReq = yt.channelSections().list(List.of("snippet", "contentDetails"))
                    .setChannelId(channelId)
                    .setKey(apiKeyProvider.get());
            var assocChannelIds = new YouTubePageIterator<ChannelSection>(sectionsReq).stream()
                    .filter(Objects::nonNull)
                    .filter(cs -> cs.getSnippet() != null && cs.getContentDetails() != null)
                    .filter(cs -> "multiplechannels".equalsIgnoreCase(cs.getSnippet().getType()))
                    .map(cs -> cs.getContentDetails().getChannels())
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .distinct()
                    .toList();
            return listChannels(assocChannelIds);
        } catch (IOException e) {
            throw new RuntimeException("YouTube API error", e);
        }
    }

    @Override
    public ChannelDTO getChannel(String channelId) {
        try {
            var yt = newService();
            var it = new YouTubePageIterator<Channel>(yt.channels()
                    .list(List.of("snippet", "statistics"))
                    .setId(List.of(channelId))
                    .setKey(apiKeyProvider.get())
                    .setMaxResults(1L));
            var list = it.stream().map(responseMapper::channelDTO).toList();
            return list.isEmpty() ? null : list.getFirst();
        } catch (IOException e) {
            throw new RuntimeException("YouTube API error", e);
        }
    }

    @Override
    public List<ChannelDTO> listChannels(List<String> channelIds) {
        if (channelIds.isEmpty()) return List.of();
        try {
            var yt = newService();
            List<ChannelDTO> all = new ArrayList<>();
            for (var chunk : Batching.partition(channelIds, 50)) {
                var channelReq = yt.channels().list(List.of("snippet", "statistics"))
                        .setId(chunk)
                        .setKey(apiKeyProvider.get())
                        .setMaxResults(50L);
                var it = new YouTubePageIterator<Channel>(channelReq);
                all.addAll(it.stream()
                        .map(responseMapper::channelDTO)
                        .toList());
            }
            return all;
        } catch (IOException e) {
            throw new RuntimeException("YouTube API error", e);
        }
    }

    @Override
    public String getUploadsPlaylistId(String channelId) {
        try {
            var yt = newService();
            var chReq = yt.channels().list(List.of("contentDetails"))
                    .setId(List.of(channelId))
                    .setKey(apiKeyProvider.get())
                    .setMaxResults(1L);
            var chResp = chReq.execute();
            if (chResp.getItems() != null && !chResp.getItems().isEmpty()) {
                var rel = chResp.getItems().getFirst().getContentDetails();
                if (rel != null && rel.getRelatedPlaylists() != null) {
                    return rel.getRelatedPlaylists().getUploads();
                }
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException("YouTube API error", e);
        }
    }

    @Override
    public List<VideoDTO> listAllVideos(String channelId) {
        var uploadsPlId = getUploadsPlaylistId(channelId);
        if (uploadsPlId == null || uploadsPlId.isEmpty()) {
            throw new IllegalArgumentException("No Uploads Playlist found for Channel " + channelId);
        }
        return listPlaylistVideos(uploadsPlId);
    }

    @Override
    public List<VideoDTO> listPlaylistVideos(String playlistId) {
        try {
            var yt = newService();
            var itemsReq = yt.playlistItems().list(List.of("contentDetails"))
                    .setPlaylistId(playlistId)
                    .setMaxResults(50L)
                    .setKey(apiKeyProvider.get());
            var itemsIter = new YouTubePageIterator<PlaylistItem>(itemsReq);
            var vidIds = itemsIter.stream()
                    .map(PlaylistItem::getContentDetails)
                    .filter(Objects::nonNull)
                    .map(PlaylistItemContentDetails::getVideoId)
                    .filter(Objects::nonNull)
                    .toList();
            if (vidIds.isEmpty()) return List.of();
            return listVideos(vidIds);
        } catch (IOException e) {
            throw new RuntimeException("YouTube API error", e);
        }
    }

    @Override
    public List<VideoDTO> listVideos(List<String> videoIds) {
        try {
            var yt = newService();
            List<VideoDTO> all = new ArrayList<>();
            for (var chunk : Batching.partition(videoIds, 50)) {
                var it = new YouTubePageIterator<Video>(yt.videos()
                        .list(List.of("snippet", "contentDetails", "statistics", "status"))
                        .setId(chunk)
                        .setKey(apiKeyProvider.get())
                        .setMaxResults(50L));
                var videos = it.stream()
                        .filter(Objects::nonNull)
                        .map(responseMapper::videoDTO)
                        .toList();
                videos.forEach(cache::putVideo);
                all.addAll(videos);
            }
            return all;
        } catch (IOException e) {
            throw new RuntimeException("YouTube API error", e);
        }
    }

    @Override
    public VideoDTO getVideo(String videoId) {
        try {
            var cached = cache.getVideo(videoId);
            if (cached != null) return cached;
            var yt = newService();
            var it = new YouTubePageIterator<Video>(yt.videos()
                    .list(List.of("snippet", "contentDetails", "statistics", "status"))
                    .setId(List.of(videoId))
                    .setKey(apiKeyProvider.get())
                    .setMaxResults(1L));
            var videos = it.stream()
                    .filter(Objects::nonNull)
                    .map(responseMapper::videoDTO)
                    .toList();
            var vid = videos.isEmpty() ? null : videos.getFirst();
            if (vid != null) cache.putVideo(vid);
            return vid;
        } catch (IOException e) {
            throw new RuntimeException("YouTube API error", e);
        }
    }

    @Override
    public List<PlaylistDTO> listPlaylists(String channelId) {
        try {
            var yt = newService();
            var it = new YouTubePageIterator<Playlist>(yt.playlists().list(List.of("snippet", "status", "contentDetails"))
                    .setChannelId(channelId)
                    .setMaxResults(50L)
                    .setKey(apiKeyProvider.get()));
            var uploadsPlId = getUploadsPlaylistId(channelId);
            return it.stream()
                    .map(pl -> responseMapper.playlistDTO(pl, uploadsPlId))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("YouTube API error", e);
        }
    }

    /**
     * Clears the internal caches (currently only videos).
     */
    @Override
    public void flushCache() {
        cache.clear();
    }
}
