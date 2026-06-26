package cn.lunadeer.dominion.utils;

public class AutoTimer {
    private final String name;
    private long startTime;
    private boolean running = false;

    public AutoTimer(String name) { this.name = name; }

    public void start() { startTime = System.nanoTime(); running = true; }

    public long stop() {
        if (!running) return 0;
        running = false;
        long elapsed = System.nanoTime() - startTime;
        XLogger.debug("Timer [{0}]: {1}ms", name, elapsed / 1_000_000.0);
        return elapsed;
    }
}
