/*
 * Copyright 2014-2015 ChalkPE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pe.chalk.takoyaki;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pe.chalk.takoyaki.data.Data;
import pe.chalk.takoyaki.data.Menu;
import pe.chalk.takoyaki.filter.*;
import pe.chalk.takoyaki.logger.Prefix;
import pe.chalk.takoyaki.logger.PrefixedLogger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public class Target extends Thread implements Prefix {
    private static final String STRING_CONTENT = "http://cafe.naver.com/%s";
    private static final String STRING_ARTICLE = "http://cafe.naver.com/ArticleList.nhn?search.clubid=%d&search.boardtype=L";

    private static final Pattern PATTERN_CLUB_ID = Pattern.compile("var g_sClubId = \"(\\d+)\";");

    private URL contentUrl;
    private URL articleUrl;

    private Takoyaki takoyaki;
    private String prefix;
    private PrefixedLogger logger;

    private long interval;
    private int timeout;

    private Collector collector;

    private final String address;
    private final int clubId;
    private List<Menu> menus;

    public Target(Takoyaki takoyaki, JSONObject jsonObject) throws JSONException, IOException {
        this.takoyaki = takoyaki;
        this.prefix = jsonObject.getString("prefix");
        this.logger = this.getTakoyaki().getLogger().getPrefixed(this);

        this.interval = jsonObject.getLong("interval");
        this.timeout = jsonObject.getInt("timeout");

        JSONArray filtersArray = jsonObject.getJSONArray("filters");
        List<Filter<? extends Data>> filters = new ArrayList<>(filtersArray.length());

        for(int i = 0; i < filtersArray.length(); i++){
            Filter<? extends Data> filter;
            switch(filtersArray.getString(i)){
                case ArticleFilter.NAME:
                    filter = new ArticleFilter(this);
                    break;
                case CommentaryFilter.NAME:
                    filter = new CommentaryFilter(this);
                    break;
                case VisitationFilter.NAME:
                    filter = new VisitationFilter(this);
                    break;
                default:
                    continue;
            }
            filters.add(filter);
        }
        this.collector = new Collector(filters);

        this.address = jsonObject.getString("address");
        this.contentUrl = new URL(String.format(STRING_CONTENT, this.getAddress()));

        Document contentDocument = Jsoup.parse(this.contentUrl, this.getTimeout());
        this.setName(contentDocument.select("h1.d-none").text());
        Matcher clubIdMatcher = Target.PATTERN_CLUB_ID.matcher(contentDocument.head().getElementsByTag("script").first().html());
        if(!clubIdMatcher.find()){
            throw new IllegalArgumentException("Cannot find menuId of " + this.getName());
        }
        this.clubId = Integer.parseInt(clubIdMatcher.group(1));
        this.menus = contentDocument.select("a[id^=menuLink]").stream()
                .map(element -> new Menu(this.getClubId(), Integer.parseInt(element.id().substring(8)), element.text()))
                .collect(Collectors.toList());

        Files.write(Paths.get(this.getAddress() + "-menus.json"), this.getMenus().stream().map(Menu::toString).collect(Collectors.toList()), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

        this.articleUrl = new URL(String.format(STRING_ARTICLE, this.getClubId()));

        this.getLogger().debug("카페명: " + this.getName() + " (ID: " + this.getClubId() + ")");
        this.getLogger().debug("게시판 수: " + this.getMenus().size() + "개");
        this.getLogger().newLine();
    }

    public Takoyaki getTakoyaki(){
        return this.takoyaki;
    }

    public PrefixedLogger getLogger(){
        return this.logger;
    }

    public long getInterval(){
        return this.interval;
    }

    public int getTimeout(){
        return this.timeout;
    }

    public String getAddress(){
        return this.address;
    }

    public int getClubId(){
        return this.clubId;
    }

    public List<Menu> getMenus(){
        return this.menus;
    }

    public Menu getMenu(int menuId){
        for(Menu menu : this.getMenus()){
            if(menu.getId() == menuId){
                return menu;
            }
        }
        return null;
    }

    @Override
    public String getPrefix(){
        return this.prefix;
    }

    @Override
    public void run(){
        while(this.getTakoyaki().isAlive()){
            try{
                Thread.sleep(this.getInterval());

                Document contentDocument = Jsoup.parse(this.contentUrl, this.getTimeout());
                Document articleDocument = Jsoup.parse(this.articleUrl, this.getTimeout());

                this.collector.collect(contentDocument, articleDocument);
            }catch(IOException e){
                this.getLogger().error(e.getMessage());
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}