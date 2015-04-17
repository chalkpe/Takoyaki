package pe.chalk.takoyaki.filter;

import org.json.JSONObject;
import pe.chalk.takoyaki.data.Data;
import pe.chalk.takoyaki.logger.PrefixedLogger;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-16
 */
public abstract class ContentFilter<T extends Data> extends Filter<T> {
    public ContentFilter(JSONObject options, PrefixedLogger logger){
        super(options, logger);
    }
}