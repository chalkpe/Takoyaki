package pe.chalk.takoyaki;

import org.jsoup.nodes.Document;
import pe.chalk.takoyaki.data.Data;
import pe.chalk.takoyaki.filter.ContentFilter;
import pe.chalk.takoyaki.filter.Filter;

import java.util.List;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-12
 */
public class Collector {
    private List<Filter<? extends Data>> filters;

    public Collector(List<Filter<? extends Data>> filters){
        this.filters = filters;
    }

    public List<Filter<? extends Data>> getFilters(){
        return this.filters;
    }

    public void collect(Document contentDocument, Document articleDocument){
        try{
            this.getFilters().forEach(filter -> {
                Document document = filter instanceof ContentFilter ? contentDocument : articleDocument;
                List<? extends Data> freshData = filter.getFreshData(document);
                freshData.forEach(data -> filter.getLogger().info(data.toString()));

                Takoyaki.getInstance().getPlugins().forEach(plugin -> plugin.call("onDataUpdated", new Object[]{filter, freshData.toArray()}));
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
