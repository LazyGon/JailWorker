package fr.alienationgaming.jailworker.events;

import org.bukkit.event.Cancellable;

public abstract class JailWorkerCancellableEvent extends JailWorkerEvent implements Cancellable {

    protected boolean cancel = false;

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}