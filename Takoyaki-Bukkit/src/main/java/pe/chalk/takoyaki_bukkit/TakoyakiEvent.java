package pe.chalk.takoyaki_bukkit;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import pe.chalk.takoyaki.data.Data;

public class TakoyakiEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private Object[] message;
	
	public TakoyakiEvent(Object[] message) {
		this.message = message;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public Object[] getMessage() {
    	return message;
    }
    
}
