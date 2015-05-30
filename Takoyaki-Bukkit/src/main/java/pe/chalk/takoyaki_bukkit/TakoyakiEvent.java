package pe.chalk.takoyaki_bukkit;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @author 파차리로디드 <fcreloaded@outlook.kr>
 * @since 2015-05-30
 */

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import pe.chalk.takoyaki.data.Data;

@SuppressWarnings("unused")
public class TakoyakiEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Data[] data;

    public TakoyakiEvent(Data[] data){
        this.data = data;
    }

    @Override
    public HandlerList getHandlers(){
        return TakoyakiEvent.handlers;
    }

    public static HandlerList getHandlerList(){
        return TakoyakiEvent.handlers;
    }

    public Data[] getData(){
        return this.data;
    }
}