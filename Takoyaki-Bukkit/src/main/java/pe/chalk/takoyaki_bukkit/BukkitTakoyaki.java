package pe.chalk.takoyaki_bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import pe.chalk.takoyaki.Takoyaki;

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
            takoyaki.getLogger().removeStream(System.out);
            takoyaki.getLogger().addTransmitter((level, message) -> BukkitTakoyaki.this.getServer().broadcastMessage(level.getFormats() + "[" + level.getPrefix() + "] " + message));

            takoyaki.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}