package cn.lunadeer.dominion.nms;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Allocates unique entity IDs for fake entities.
 */
public class EntityIdAllocator {
    private static final AtomicInteger counter = new AtomicInteger(Integer.MAX_VALUE / 2);

    public static int nextId() {
        return counter.decrementAndGet();
    }
}
