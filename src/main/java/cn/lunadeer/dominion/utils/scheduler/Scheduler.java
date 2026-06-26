package cn.lunadeer.dominion.utils.scheduler;

import java.util.concurrent.*;

public class Scheduler {
    public static Scheduler instance;
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private static MinecraftServer server;

    public Scheduler() {
        instance = this;
    }

    public static void setServer(MinecraftServer srv) {
        server = srv;
    }

    public static void cancelAll() {
        instance.executor.shutdownNow();
    }

    public static CancellableTask runTaskLater(Runnable task, long delay) {
        if (delay <= 0) return runTask(task);
        return new FabricTask(instance.executor.schedule(task, delay * 50, TimeUnit.MILLISECONDS));
    }

    public static CancellableTask runTask(Runnable task) {
        if (server != null && server.isOnThread()) {
            task.run();
            return new FabricTask(CompletableFuture.completedFuture(null));
        }
        return new FabricTask(instance.executor.submit(task));
    }

    public static CancellableTask runTaskRepeat(Runnable task, long delay, long period) {
        return new FabricTask(instance.executor.scheduleAtFixedRate(task, delay * 50, period * 50, TimeUnit.MILLISECONDS));
    }

    public static CancellableTask runTaskLaterAsync(Runnable task, long delay) {
        if (delay <= 0) return runTaskAsync(task);
        return new FabricTask(instance.executor.schedule(task, delay * 50, TimeUnit.MILLISECONDS));
    }

    public static CancellableTask runTaskAsync(Runnable task) {
        return new FabricTask(instance.executor.submit(task));
    }

    public static CancellableTask runTaskRepeatAsync(Runnable task, long delay, long period) {
        return new FabricTask(instance.executor.scheduleAtFixedRate(task, delay * 50, period * 50, TimeUnit.MILLISECONDS));
    }

    public static CancellableTask runEntityTask(Runnable task, Object entity) {
        return runTask(task);
    }

    public static CancellableTask runLocationTask(Runnable task, Object location) {
        return runTask(task);
    }

    // MinecraftServer stub - will be replaced with actual Fabric class
    public interface MinecraftServer {
        boolean isOnThread();
    }
}
