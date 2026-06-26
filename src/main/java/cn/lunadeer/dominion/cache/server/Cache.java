package cn.lunadeer.dominion.cache.server;

public class Cache {
    public static class Scheduler {
        public static void runAsync(Runnable task) { new Thread(task).start(); }
        public static void runLater(Runnable task, long ticks) { new Thread(() -> { try { Thread.sleep(ticks * 50); task.run(); } catch (Exception e) {} }).start(); }
    }
}
