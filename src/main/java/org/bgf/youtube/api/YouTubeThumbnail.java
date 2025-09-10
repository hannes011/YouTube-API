package org.bgf.youtube.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public interface YouTubeThumbnail {

	URL getUrl();

	InputStream fetch() throws IOException;

	File fetchFile(Path target) throws IOException;
}

