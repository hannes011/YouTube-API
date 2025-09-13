# Repository Guidelines

## Project Structure & Module Organization
- Source: `src/main/java` under `org.bgf.youtube` and `org.bgf.youtube.api`.
- Tests: `src/test/java` (JUnit 5; mirrors package paths).
- Resources: `src/main/resources` for non-code assets.
- Entry point: `org.bgf.youtube.Example` (reads `YOUTUBE_API_KEY` and `YOUTUBE_CHANNEL_ID`).

## Implementation
- Utility library for public YouTube Data API: https://developers.google.com/youtube/v3/docs
- Default client: `impl/client/GoogleApiYouTubeClient` (API key-based). Swap by injecting another `YouTubeClient`.
- OAuth2 is optional; any auth handling should live in a separate package.
- Interfaces in `org.bgf.youtube.api` define the feature scope; propose additions via PR.
- Reference: https://github.com/hannes011/youtube-data-cli (no file storage, no System.out).

## Build, Test, and Development Commands
- Build: `mvn clean package` — compiles sources and creates `target/yt-api-1.0-SNAPSHOT.jar`.
- Run (preferred): `YOUTUBE_API_KEY=... YOUTUBE_CHANNEL_ID=UC... mvn exec:java` — uses the default Google client.
- Run (jar): `java -cp target/yt-api-1.0-SNAPSHOT.jar org.bgf.youtube.Example UC...` — reads API key from `YOUTUBE_API_KEY`.
- Tests: `mvn test` — runs unit tests; no network required (tests use fakes).

## Coding Style & Naming Conventions
- Java 21; keep code simple and null-safe.
- Indentation: tabs (matches current files); consistent braces and one statement per line.
- Packages: lower case (e.g., `org.bgf.youtube.api`).
- Types: PascalCase (e.g., `YouTubeFetcher`, `YouTubePlaylist`).
- Methods/fields: camelCase; constants UPPER_SNAKE_CASE; enum values UPPER (see `YouTubeThumbnailType`).

## Testing Guidelines
- Framework: JUnit 5. Place tests in `src/test/java` mirroring source packages.
- Naming: `ClassNameTest.java` (e.g., `YouTubeFetcherTest.java`).
- Scope: fast, isolated unit tests for interfaces and any default implementations; add integration tests if HTTP is introduced.
- Run: `mvn test`. Add coverage via Surefire/JaCoCo if needed.
- Ensure that edge cases of the YouTube API (e.g. pagination, etc.) are covered.

## Commit & Pull Request Guidelines
- Commits: imperative mood, concise subject, clear scope (e.g., "Add default fetcher for playlists").
- PRs: include summary, rationale, test notes, and screenshots/logs if helpful. Link related issues.
- Keep changes focused; update `pom.xml` and docs when adding deps or tools.

## Security & Configuration Tips
- Do not hardcode API keys. Prefer environment variables (e.g., `YOUTUBE_API_KEY`) or a local, untracked config.
- Never commit secrets; `.gitignore` already excludes build outputs and IDE files. Add local secrets files to `.gitignore` if introduced.
