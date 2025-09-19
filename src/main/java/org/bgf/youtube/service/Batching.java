package org.bgf.youtube.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Batching {
    private Batching() {}

    /**
     * @param items data which needs to be split into pieces
     * @param batchSize if <= 0 then return all items in one chunk, otherwise each chunk is max as big as batchSize
     * @return a list of all chunks
     * @param <T> type of the input list data / output chunk list data
     */
    public static <T> List<List<T>> partition(List<T> items, int batchSize) {
        if (items == null || items.isEmpty()) return List.of();
        int size = items.size();
        if (batchSize <= 0 || size <= batchSize) return List.of(List.copyOf(items));
        List<List<T>> batches = new ArrayList<>((size + batchSize - 1) / batchSize);
        for (int i = 0; i < size; i += batchSize) {
            int end = Math.min(size, i + batchSize);
            batches.add(List.copyOf(items.subList(i, end)));
        }
        return Collections.unmodifiableList(batches);
    }
}

