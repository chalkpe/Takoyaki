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
                filter.getFreshData(document).forEach(data -> filter.getLogger().info(data.toString()));
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
