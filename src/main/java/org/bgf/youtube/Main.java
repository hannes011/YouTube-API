package org.bgf.youtube;

import org.bgf.youtube.api.YouTubeFetcher;

public class Main {
    public static void main(String[] args) {
        var fetcher = YouTubeFetcher.create("API_KEY");
        var channels = fetcher.getAssociatedChannels("CHANNEL_ID");
        System.out.println("Found " + channels.size() + " channels\n");
        for (var channel : channels) {
            System.out.println("Channel: " + channel.getTitle());
            System.out.println("ID: " + channel.getChannelId());
            System.out.println("Language: " + channel.getLanguage());
            System.out.println("Playlists:");
            fetcher.getPlayLists(channel.getChannelId()).forEach(playlist -> {
                System.out.println(" - " + playlist.getName() + " (" + playlist.getPlaylistId() + ")");
                playlist.fetchVideos().forEach(video -> {
                    System.out.println("    - " + video.getTitle() + " (" + video.getId() + ")");
                });
            });
            System.out.println("All Videos:");
            fetcher.getVideos(channel.getChannelId()).forEach(video -> {
                System.out.println(" - " + video.getTitle() + " (" + video.getId() + ")");
            });
            System.out.println();
        }
    }
}