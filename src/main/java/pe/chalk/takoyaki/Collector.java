package pe.chalk.takoyaki;

import org.jsoup.nodes.Document;
import pe.chalk.takoyaki.data.Article;
import pe.chalk.takoyaki.data.Data;
import pe.chalk.takoyaki.data.Member;
import pe.chalk.takoyaki.data.SimpleArticle;
import pe.chalk.takoyaki.filter.Filter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-12
 */
public class Collector<T extends Data> {
    public static enum Subscription {
        WIDGET, ARTICLE
    }

    private final Class<T> type;
    private Filter<T> filter;
    private Collector.Subscription subscription;

    private ArrayList<T> lastData;

    public Collector(Class<T> type, Filter<T> filter, Collector.Subscription subscription){
        this.type = type;
        this.filter = filter;
        this.subscription = subscription;
    }

    public Class<? extends Data> getType(){
        return this.type;
    }

    public Filter<T> getFilter(){
        return this.filter;
    }

    public Collector.Subscription getSubscription(){
        return this.subscription;
    }

    public void collect(Document document){
        try{
            ArrayList<T> rawData = this.getFilter().filter(document);
            if(rawData.size() > 0){
                ArrayList<T> freshData = this.getFreshData(rawData);
                if(freshData.size() > 0){
                    freshData.forEach(System.out::println);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private int getFreshItemCount(ArrayList<T> data){
        if(this.lastData == null){
            return 0;
        }
        T lastItem = this.lastData.get(0);

        for(int i = 0; i < data.size() && i < this.lastData.size(); i++){
            T item = data.get(i);
            if(this.getType().equals(Member.class) || this.getType().equals(SimpleArticle.class)){
                if(item.getCreationTime() <= lastItem.getCreationTime()){
                    return i;
                }
            }
            if(this.getType().equals(Article.class)){
                Article lastArticle = (Article) lastItem;
                Article article = (Article) item;

                if(article.getId() <= lastArticle.getId()){
                    return i;
                }
            }
        }
        return 0;
    }

    public ArrayList<T> getFreshData(ArrayList<T> data){
        ArrayList<T> freshData = new ArrayList<>(data.subList(0, this.getFreshItemCount(data)));
        Collections.reverse(freshData);

        this.lastData = data;
        return freshData;
    }
}
