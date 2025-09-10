package org.bgf.youtube.api.client;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface PageIterator<T> extends Iterator<List<T>> {

    List<T> nextPageItems() throws IOException;

    String nextPageToken();

    @Override
    default boolean hasNext() {
        return nextPageToken() != null;
    }

    @Override
    default List<T> next() {
        try {
            return nextPageItems();
        } catch (IOException e) {
            throw new RuntimeException("YouTube API error", e);
        }
    }

    default Stream<T> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.ORDERED), false)
                .flatMap(List::stream);
    }
}
