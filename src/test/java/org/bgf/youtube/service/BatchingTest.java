package org.bgf.youtube.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class BatchingTest {

    @Test
    void partitionsListIntoBatchesOfGivenSize() {
        var items = IntStream.range(0, 123).boxed().toList();
        var batches = Batching.partition(items, 50);
        assertEquals(3, batches.size());
        assertEquals(50, batches.get(0).size());
        assertEquals(50, batches.get(1).size());
        assertEquals(23, batches.get(2).size());
        assertEquals(List.of(0, 1, 2), batches.get(0).subList(0, 3));
    }

    @Test
    void returnsWholeListIfSizeLeBatchSize() {
        var items = List.of(1, 2, 3);
        var batches = Batching.partition(items, 50);
        assertEquals(1, batches.size());
        assertEquals(items, batches.get(0));
    }

    @Test
    void handlesEmptyOrInvalid() {
        assertTrue(Batching.partition(List.of(), 50).isEmpty());
        assertEquals(1, Batching.partition(List.of(1, 2, 3), 0).size());
    }
}
