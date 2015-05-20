package pe.chalk.takoyaki_bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import pe.chalk.takoyaki.Takoyaki;
import pe.chalk.takoyaki.logger.Loggable;

import java.io.IOException;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-05-19
 */
public class BukkitTakoyaki extends JavaPlugin {
    @Override
    public void onEnable(){
        try{
            Takoyaki takoyaki = new Takoyaki();
            takoyaki.start();

            takoyaki.getLogger().addTransmitter((Loggable.Level level, String message) -> {
                if(level == Loggable.Level.INFO){
                    BukkitTakoyaki.this.getServer().broadcastMessage(message);
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}