package pe.chalk.takoyaki;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pe.chalk.takoyaki.logger.Prefix;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public class Target extends Thread implements Prefix {
    private static final String STRING_WIDGET = "http://cafe.naver.com/%s";
    private static final String STRING_ARTICLE = "http://cafe.naver.com/ArticleList.nhn?search.clubid=%d&search.boardtype=L";

    private URL widgetUrl, articleUrl;

    private Takoyaki takoyaki;

    private long interval;
    private int timeout;

    private List<Collector> collectors;

    public Target(Takoyaki takoyaki, JSONObject jsonObject) throws JSONException, MalformedURLException{
        this.takoyaki = takoyaki;
        this.setName(jsonObject.getString("name"));

        this.widgetUrl = new URL(String.format(STRING_WIDGET, jsonObject.getString("address")));
        this.articleUrl = new URL(String.format(STRING_ARTICLE, jsonObject.getInt("clubId")));

        this.interval = jsonObject.getLong("interval");
        this.timeout = jsonObject.getInt("timeout");
    }

    public Takoyaki getTakoyaki(){
        return this.takoyaki;
    }

    public long getInterval(){
        return this.interval;
    }

    public int getTimeout(){
        return this.timeout;
    }

    @Override
    public String getPrefix(){
        return this.getName();
    }

    @Override
    public void run(){
        while(this.getTakoyaki().isAlive()){
            try{
                Thread.sleep(this.getInterval());

                Document widgetDocument = Jsoup.parse(this.widgetUrl, this.getTimeout());
                Document articleDocument = Jsoup.parse(this.articleUrl, this.getTimeout());

                this.collectors.forEach(collector -> collector.collect(widgetDocument, articleDocument));
            }catch(IOException | InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}