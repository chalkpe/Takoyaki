package pe.chalk.takoyaki;

import org.jsoup.nodes.Document;
import pe.chalk.takoyaki.data.Data;
import pe.chalk.takoyaki.filter.Filter;

import java.util.ArrayList;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-12
 */
public class Collector {
    public static enum Subscription {
        WIDGET, ARTICLE
    }

    private ArrayList<Filter<? extends Data>> filters;
    private Collector.Subscription subscription;

    public Collector(Collector.Subscription subscription, ArrayList<Filter<? extends Data>> filters){
        this.subscription = subscription;
        this.filters = filters;
    }

    public ArrayList<Filter<? extends Data>> getFilters(){
        return this.filters;
    }

    public Collector.Subscription getSubscription(){
        return this.subscription;
    }

    public void collect(Document document){
        try{
            this.getFilters().forEach(filter ->
                    filter.getFreshData(document).forEach(data ->
                            Takoyaki.getInstance().getLogger().info(
                                    filter, data.toString())));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
