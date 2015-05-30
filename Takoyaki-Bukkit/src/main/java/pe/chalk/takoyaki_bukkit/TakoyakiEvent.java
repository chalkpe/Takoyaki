package pe.chalk.takoyaki_bukkit;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TakoyakiEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private String message;
	
	public TakoyakiEvent(String message) {
		this.message = message;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
