package pe.chalk.takoyaki.plugin;

import pe.chalk.takoyaki.Takoyaki;
import pe.chalk.takoyaki.filter.Filter;
import pe.chalk.takoyaki.logger.PrefixedLogger;
import pe.chalk.takoyaki.model.Data;
import pe.chalk.takoyaki.utils.Prefix;

import java.util.List;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-10-05
 */
public abstract class Plugin implements Prefix {
    private final String name;
    private final PrefixedLogger logger;

    public Plugin(String name){
        this.name = name;
        this.logger = new PrefixedLogger(Takoyaki.getInstance().getLogger(), this);
    }

    public final String getName(){
        return this.name;
    }

    @Override
    public final String getPrefix(){
        return this.getName();
    }

    @Override
    public final String toString(){
        return this.getName();
    }

    public final PrefixedLogger getLogger(){
        return this.logger;
    }

    public abstract String getVersion();

    public abstract void reload();

    public abstract void onLoad();
    public abstract void onDestroy();

    public abstract void onStart();
    public abstract void onDataAdded(List<? extends Data> freshData, Filter<?, ? extends Data> filter);
}
