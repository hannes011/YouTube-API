# YouTube Data API Toolkit

A small Java 21 library that wraps the public [YouTube Data API v3](https://developers.google.com/youtube/v3/docs) behind a simple domain model. It ships with a production-ready client built on Google's official SDK and utilities to fetch channels, playlists, videos, and caption metadata without exposing raw HTTP details.

## Features
- `YouTubeFetcher` facade to look up channels, associated channels, playlists, and videos.
- Runnable examples for API-key and OAuth flows that demonstrate how to wire authentication.
- Lazy domain objects (`YouTubeChannel`, `YouTubePlaylist`, `YouTubeVideo`, `YouTubeThumbnail`) that defer extra API calls until data is needed.
- Iterator and batching utilities that transparently page through the YouTube API.
- In-memory response caching in the default client to minimize duplicate requests.
- Comprehensive JUnit 5 test suite; no network access is required when running tests.

## Project Layout
```
src/main/java/org/bgf/youtube        # Library code
src/main/java/org/bgf/youtube/api    # Public interfaces
src/main/java/org/bgf/youtube/client # Default Google API client implementation
src/main/java/org/bgf/youtube/model  # Lazy domain model wrappers
src/test/java                        # Unit tests (Jupiter 5)
```

## Prerequisites
- Java 21 SDK
- Apache Maven 3.9+
- A Google Cloud project with the YouTube Data API enabled (for API-key usage)
- (Optional) OAuth 2.0 client credentials for installed applications if you want to try the OAuth example

## Environment Configuration
Two example `.env` templates live in the repository root:

- `apikey.env.example` – variables for the API key flow.
- `oauth.env.example` – variables for the OAuth flow.

Copy the template that matches your scenario to `.env` (or export the variables manually) and fill in the values:

```bash
cp apikey.env.example .env
# edit .env and provide your API key and preferred channel
```

Expected variables:

| Variable                     | Description                                                                 |
|------------------------------|-----------------------------------------------------------------------------|
| `YOUTUBE_API_KEY`            | API key used by `ExampleWithApiKey`. Required when using key-based auth.    |
| `YOUTUBE_CHANNEL_ID`         | Target channel ID or handle (e.g. `@YourChannel`). Used by both examples.   |
| `GOOGLE_CLIENT_SECRETS_PATH` | Path to the OAuth client secrets JSON (only needed for `ExampleWithOAuth`). |

> **Note:** Never commit `.env` or secrets. The example values intentionally omit real credentials.

## Building the Library

```bash
mvn clean package
```

The build outputs `target/yt-api-1.0-SNAPSHOT.jar`.

## Running the Examples

### API key flow
1. Provide `YOUTUBE_API_KEY` and `YOUTUBE_CHANNEL_ID` (handle or channel ID).
2. Run the example:
   ```bash
   mvn exec:java
   ```
   The default `exec-maven-plugin` entry point runs `org.bgf.youtube.ExampleWithApiKey`.
3. Alternatively run the packaged jar:
   ```bash
   java -cp target/yt-api-1.0-SNAPSHOT.jar org.bgf.youtube.ExampleWithApiKey UCXXXXXXXXXXXX
   ```

### OAuth flow
1. Download OAuth client credentials from Google Cloud (Desktop app) and save them locally, e.g. `client_secrets.json`.
2. Point `GOOGLE_CLIENT_SECRETS_PATH` to that file and set `YOUTUBE_CHANNEL_ID`.
3. Launch the example:
   ```bash
   mvn exec:java -Dexec.mainClass=org.bgf.youtube.ExampleWithOAuth
   ```
   The first run opens a local browser window to complete the OAuth consent flow.

## Using the Fetcher in Your Code

```java
var fetcher = YouTubeFetcher.create(System.getenv("YOUTUBE_API_KEY"));
var channelId = fetcher.getChannelIdForChannelHandle("@ExampleChannel");
var playlists = fetcher.getPlayLists(channelId);
playlists.forEach(playlist -> {
	System.out.println(playlist.getName() + " has " + playlist.getVideoCount() + " videos");
});
```

You can also supply your own `YouTubeClient` implementation when you need different auth or transport mechanics.

## Testing

```bash
mvn test
```

Unit tests rely on fakes and do not call the live API, so they can run in CI without credentials.

## Contributing
- Keep new interfaces under `org.bgf.youtube.api` minimal and propose additions through a PR.
- Avoid committing secrets or generated files—`.gitignore` already covers build outputs.
- Open an issue or pull request with context when contributing new features or fixes.
