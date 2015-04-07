package pe.chalk.takoyaki;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pe.chalk.takoyaki.data.Data;
import pe.chalk.takoyaki.filter.Filter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public class Monitor {
    private URL target;
    private ArrayList<Filter> filters;

    public Monitor(URL target, ArrayList<Filter> filters){
        this.target = target;
        this.filters = filters;
    }

    public void monitor() throws IOException {
        Document document = Jsoup.parse(this.target, Takoyaki.getInstance().getTimeout());
        for(Filter filter : this.filters){
            ArrayList<Data> list = filter.filter(document);
            //TODO: Implements data handling
        }
    }
}
