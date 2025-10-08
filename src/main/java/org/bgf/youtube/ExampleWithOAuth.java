package org.bgf.youtube;

import org.bgf.youtube.api.YouTubeFetcher;
import org.bgf.youtube.auth.YouTubeServiceInitializer;

import java.io.FileInputStream;
import java.io.InputStream;

public class ExampleWithOAuth {

    private static InputStream getClientSecrets() {
        String clientSecretsPath = System.getenv("GOOGLE_CLIENT_SECRETS_PATH");
        if (clientSecretsPath == null || clientSecretsPath.isBlank()) {
            throw new IllegalStateException("Missing GOOGLE_CLIENT_SECRETS_PATH env var pointing to OAuth client secrets JSON");
        }
        try {
            clientSecretsPath = ExampleWithOAuth.class.getResource(clientSecretsPath) == null ? clientSecretsPath
                    : ExampleWithOAuth.class.getResource(clientSecretsPath).getPath();
            return new FileInputStream(clientSecretsPath);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to open client secrets at " + clientSecretsPath, e);
        }
    }

    public static void main(String[] args) throws Exception {
        String channelId = System.getenv("YOUTUBE_CHANNEL_ID");

        if ((channelId == null || channelId.isBlank()) && args.length > 0) {
            channelId = args[0];
        }

        if (channelId == null || channelId.isBlank()) {
            System.err.println("Missing configuration. Set env var YOUTUBE_CHANNEL_ID or pass CHANNEL_ID as arg.");
            System.err.println("Example: GOOGLE_CLIENT_SECRETS_PATH=client_secret.json YOUTUBE_CHANNEL_ID=UC... mvn -q exec:java");
            return;
        }

        var fetcher = YouTubeFetcher.create(YouTubeServiceInitializer.initServiceWithOAuth("bgf-youtube-api", ExampleWithOAuth::getClientSecrets));
        ExampleWithApiKey.runFullScan(fetcher, channelId);
    }
}
