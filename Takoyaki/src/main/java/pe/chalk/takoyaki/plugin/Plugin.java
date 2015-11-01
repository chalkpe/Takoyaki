package pe.chalk.takoyaki.plugin;

import pe.chalk.takoyaki.filter.Filter;
import pe.chalk.takoyaki.logger.PrefixedLogger;
import pe.chalk.takoyaki.model.Data;
import pe.chalk.takoyaki.utils.Prefix;

import java.util.List;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-10-05
 */
public interface Plugin extends Prefix {
    String getName();
    @Override
    default String getPrefix(){
        return this.getName();
    }

    PrefixedLogger getLogger();
    String getVersion();

    @SuppressWarnings("unused")
    void reload();

    void onLoad();
    void onDestroy();

    void onStart();
    void onDataAdded(List<? extends Data> freshData, Filter<?, ? extends Data> filter);
}
