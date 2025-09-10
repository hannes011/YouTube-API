package org.bgf.youtube.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Batching {
    private Batching() {}

    public static <T> List<List<T>> partition(List<T> items, int batchSize) {
        if (items == null || items.isEmpty()) return List.of();
        if (batchSize <= 0) return List.of(List.copyOf(items));
        int size = items.size();
        if (size <= batchSize) return List.of(List.copyOf(items));
        List<List<T>> batches = new ArrayList<>((size + batchSize - 1) / batchSize);
        for (int i = 0; i < size; i += batchSize) {
            int end = Math.min(size, i + batchSize);
            batches.add(List.copyOf(items.subList(i, end)));
        }
        return Collections.unmodifiableList(batches);
    }
}

