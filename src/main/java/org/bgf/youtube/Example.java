package org.bgf.youtube;

import org.bgf.youtube.api.YouTubeFetcher;

public class Example {
    public static void main(String[] args) {
        String apiKey = System.getenv("YOUTUBE_API_KEY");
        String channelId = System.getenv("YOUTUBE_CHANNEL_ID");

        if ((channelId == null || channelId.isBlank()) && args.length > 0) {
            channelId = args[0];
        }

        if (apiKey == null || apiKey.isBlank() || channelId == null || channelId.isBlank()) {
            System.err.println("Missing configuration. Set env vars YOUTUBE_API_KEY and YOUTUBE_CHANNEL_ID or pass CHANNEL_ID as arg.");
            System.err.println("Example: YOUTUBE_API_KEY=... YOUTUBE_CHANNEL_ID=UC... mvn -q exec:java");
            System.err.println("Or run: java -cp target/yt-api-1.0-SNAPSHOT.jar org.bgf.youtube.Main UC...\n");
            return;
        }

        var fetcher = YouTubeFetcher.create(apiKey);
        if (channelId.startsWith("@")) {
            var resId = fetcher.getChannelIdForChannelHandle(channelId);
            if (resId == null) {
                System.err.println("The given channel handle " + channelId + " does not exist!");
                return;
            }
            channelId = resId;
        }
        var channels = fetcher.getAssociatedChannels(channelId);
//        var channel = fetcher.getChannel(channelId);
        System.out.println("Found " + channels.size() + " channels\n");
        for (var channel : channels) {
            System.out.println("Channel: " + channel.getTitle());
            System.out.println("ID: " + channel.getChannelId());
            System.out.println("Language: " + channel.getLanguage());
            System.out.println("Playlists:");
            fetcher.getPlayLists(channel.getChannelId()).forEach(playlist -> {
                System.out.println(" - " + playlist.getName() + " (" + playlist.getPlaylistId() + ")");
                System.out.println("    - Video count: " + playlist.fetchVideos().size());
                playlist.fetchVideos().forEach(video -> {
                    System.out.println("    - " + video.getTitle() + " (" + video.getId() + ")");
                });
            });
            System.out.println("All Videos:");
            System.out.println(" - Video count: " + fetcher.getVideos(channel.getChannelId()).size());
            fetcher.getVideos(channel.getChannelId()).forEach(video -> {
                System.out.println(" - " + video.getTitle() + " (" + video.getId() + " - lang: " + video.getLanguage() + ")");
                var thumbUrl = video.getThumbnail(org.bgf.youtube.api.YouTubeThumbnailType.HIGH).getUrl();
                System.out.println("   Thumbnail URL: " + thumbUrl);
            });
            System.out.println();
        }
    }
}
