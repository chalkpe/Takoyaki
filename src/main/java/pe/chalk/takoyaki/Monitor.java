package pe.chalk.takoyaki;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public class Monitor {
    public static final String URL_WIDGET = "http://cafe.naver.com/%s";
    public static final String URL_ARTICLE = "http://cafe.naver.com/ArticleList.nhn?search.clubid=%d&search.boardtype=L";

    private URL widgetTarget, articleTarget;
    private List<Collector> collectors;

    public Monitor(JSONObject targetObject, List<Collector> collectors) throws JSONException, MalformedURLException{
        this.widgetTarget = new URL(String.format(URL_WIDGET, targetObject.getString("address")));
        this.articleTarget = new URL(String.format(URL_ARTICLE, targetObject.getInt("clubId")));

        this.collectors = collectors;
    }

    public void monitor() throws IOException {
        Document widgetDocument = Jsoup.parse(this.widgetTarget, Takoyaki.getInstance().getTimeout());
        Document articleDocument = Jsoup.parse(this.articleTarget, Takoyaki.getInstance().getTimeout());

        for(Collector collector : this.collectors){
            switch(collector.getSubscription()){
                case WIDGET:
                    collector.collect(widgetDocument);
                    break;
                case ARTICLE:
                    collector.collect(articleDocument);
                    break;
            }
        }
    }
}
