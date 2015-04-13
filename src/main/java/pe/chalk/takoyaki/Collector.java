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
            this.getFilters().forEach(filter -> {
                ArrayList<? extends Data> freshData = filter.getFreshData(document);
                if(freshData.size() > 0){
                    freshData.forEach(data -> System.out.printf("[%s] %s%n", filter.getOptions().getString("prefix"), data));
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
