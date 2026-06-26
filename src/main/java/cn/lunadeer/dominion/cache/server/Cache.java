package cn.lunadeer.dominion.cache.server;

import cn.lunadeer.dominion.utils.XLogger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static cn.lunadeer.dominion.cache.CacheManager.UPDATE_INTERVAL;

/**
 * Abstract base class for all cache types.
 * Provides debounced reload/load/delete mechanism with scheduling.
 * <p>
 * Ported from Bukkit: Bukkit scheduler replaced with CompletableFuture-based scheduling.
 */
public abstract class Cache {

    public void load() {
        if (getLastTaskTimeStamp() + UPDATE_INTERVAL < System.currentTimeMillis()) {
            XLogger.debug("run loadExecution immediately");
            resetLastTaskTimeStamp();
            try {
                loadExecution();
            } catch (Exception e) {
                XLogger.error(e);
            }
        } else {
            scheduleLoad();
        }
    }

    public void delete(Integer idToDelete) {
        if (getLastTaskTimeStamp() + UPDATE_INTERVAL < System.currentTimeMillis()) {
            XLogger.debug("run deleteExecution immediately");
            resetLastTaskTimeStamp();
            try {
                deleteExecution(idToDelete);
            } catch (Exception e) {
                XLogger.error(e);
            }
        } else {
            scheduleLoad();
        }
    }

    private void scheduleLoad() {
        if (isTaskScheduled()) return;
        XLogger.debug("schedule loadExecution");
        setTaskScheduled();
        runTaskLaterAsync(() -> {
                    XLogger.debug("scheduled loadExecution run");
                    try {
                        resetLastTaskTimeStamp();
                        loadExecution();
                    } catch (Exception e) {
                        XLogger.error(e);
                    } finally {
                        unsetTaskScheduled();
                    }
                },
                getTaskScheduledDelayTick());
    }

    public void load(Integer idToLoad) {
        if (getLastTaskTimeStamp() + UPDATE_INTERVAL < System.currentTimeMillis()) {
            resetLastTaskTimeStamp();
            try {
                loadExecution(idToLoad);
            } catch (Exception e) {
                XLogger.error(e);
            }
        } else {
            if (isTaskScheduled()) return;
            setTaskScheduled();
            runTaskLaterAsync(() -> {
                        try {
                            resetLastTaskTimeStamp();
                            loadExecution();
                        } catch (Exception e) {
                            XLogger.error(e);
                        } finally {
                            unsetTaskScheduled();
                        }
                    },
                    getTaskScheduledDelayTick());
        }
    }

    abstract void loadExecution() throws Exception;

    abstract void loadExecution(Integer idToLoad) throws Exception;

    abstract void deleteExecution(Integer idToDelete) throws Exception;

    private final AtomicLong lastTask = new AtomicLong(0);
    private final AtomicBoolean taskScheduled = new AtomicBoolean(false);

    private Long getLastTaskTimeStamp() {
        return lastTask.get();
    }

    private void resetLastTaskTimeStamp() {
        lastTask.set(System.currentTimeMillis());
    }

    private Boolean isTaskScheduled() {
        return taskScheduled.get();
    }

    private void setTaskScheduled() {
        taskScheduled.set(true);
    }

    private void unsetTaskScheduled() {
        taskScheduled.set(false);
    }

    private long getTaskScheduledDelayTick() {
        return (UPDATE_INTERVAL - (System.currentTimeMillis() - getLastTaskTimeStamp())) / 1000 * 20L;
    }

    /**
     * Runs a task after a delay using CompletableFuture.
     *
     * @param task      the task to run
     * @param delayTicks delay in game ticks (20 ticks = 1 second)
     */
    private static void runTaskLaterAsync(Runnable task, long delayTicks) {
        long delayMs = Math.max(delayTicks * 50, 100);
        CompletableFuture.delayedExecutor(delayMs, TimeUnit.MILLISECONDS, ForkJoinPool.commonPool())
                .execute(task);
    }
}
