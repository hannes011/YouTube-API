package org.bgf.youtube.auth;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.function.Supplier;

public class YouTubeServiceInitializer {
    private static final String CLIENT_SECRETS= "client_secret.json";
    private static final List<String> SCOPES = List.of("https://www.googleapis.com/auth/youtube.readonly");

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     */
    private static Credential authorizeOAuthWithLocalServer(final NetHttpTransport httpTransport, Supplier<InputStream> clientSecretsSupplier, LocalServerReceiver receiver, String userId) throws IOException {
        try (InputStream in = clientSecretsSupplier.get()) {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize(userId);
        }
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     */
    public static YouTube initServiceWithOAuth(String appName, Supplier<InputStream> clientSecretsSupplier, LocalServerReceiver receiver, String userId) throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorizeOAuthWithLocalServer(httpTransport, clientSecretsSupplier, receiver, userId);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(appName)
                .build();
    }

    public static YouTube initServiceWithOAuth(String appName, Supplier<InputStream> clientSecretsSupplier, LocalServerReceiver receiver) throws GeneralSecurityException, IOException {
        return initServiceWithOAuth(appName, clientSecretsSupplier, receiver, "user");
    }

    public static YouTube initServiceWithOAuth(String appName, Supplier<InputStream> clientSecretsSupplier) throws GeneralSecurityException, IOException {
        return initServiceWithOAuth(appName, clientSecretsSupplier, new LocalServerReceiver(), "user");
    }

    public static YouTube initServiceWithOAuth(String appName) throws GeneralSecurityException, IOException {
        try (var in = YouTubeServiceInitializer.class.getResourceAsStream(CLIENT_SECRETS)) {
            if (in == null) {
                throw new FileNotFoundException(CLIENT_SECRETS);
            }
            return initServiceWithOAuth(appName, () -> in);
        }
    }

    public static YouTube initServiceWithApiKey(String appName, String apiKey) throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var builder = new YouTube.Builder(
                httpTransport,
                GsonFactory.getDefaultInstance(),
                request -> {})
                .setApplicationName(appName);
        if (apiKey != null) {
            builder.setGoogleClientRequestInitializer(new YouTubeRequestInitializer(apiKey));
        }
        return builder.build();
    }

}
