package fr.alienationgaming.jailworker.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class JailWorkerEvent extends Event {

    protected static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}