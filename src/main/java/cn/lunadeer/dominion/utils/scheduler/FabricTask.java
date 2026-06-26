package cn.lunadeer.dominion.utils.scheduler;

import java.util.concurrent.Future;

public class FabricTask implements CancellableTask {
    private final Future<?> future;
    private volatile boolean cancelled = false;

    public FabricTask(Future<?> future) {
        this.future = future;
    }

    @Override
    public void cancel() {
        cancelled = true;
        if (future != null) {
            future.cancel(false);
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled || (future != null && future.isCancelled());
    }
}
