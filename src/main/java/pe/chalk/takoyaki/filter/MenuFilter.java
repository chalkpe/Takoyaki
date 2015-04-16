package pe.chalk.takoyaki.filter;

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import pe.chalk.takoyaki.data.Menu;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-16
 */
public class MenuFilter extends ContentFilter<Menu> {
    public static final String NAME = "menu";

    public MenuFilter(JSONObject options){
        super(options);
    }

    @Override
    protected List<Menu> filter(Document document){
        return document.select("a[id^=menuLink]").stream()
                .map(element -> new Menu(Integer.parseInt(element.id().substring(8)), element.text()))
                .collect(Collectors.toList());
    }
}