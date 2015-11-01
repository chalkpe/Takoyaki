package pe.chalk.takoyaki.plugin;

import pe.chalk.takoyaki.Takoyaki;
import pe.chalk.takoyaki.filter.Filter;
import pe.chalk.takoyaki.logger.PrefixedLogger;
import pe.chalk.takoyaki.model.Data;

import java.util.List;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-11-02
 */
public class PluginBase implements Plugin {
    private final String name;
    private final PrefixedLogger logger;

    public PluginBase(String name){
        this.name = name;
        this.logger = new PrefixedLogger(Takoyaki.getInstance().getLogger(), this);
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public final PrefixedLogger getLogger(){
        return this.logger;
    }

    @Override
    public String getVersion(){
        return null;
    }

    @Override
    public void reload(){

    }

    @Override
    public void onLoad(){

    }

    @Override
    public void onDestroy(){

    }

    @Override
    public void onStart(){

    }

    @Override
    public void onDataAdded(List<? extends Data> freshData, Filter<?, ? extends Data> filter){

    }

    @Override
    public String toString(){
        return this.getName();
    }
}
