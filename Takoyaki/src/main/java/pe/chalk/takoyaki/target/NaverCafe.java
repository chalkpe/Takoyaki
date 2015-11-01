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

package pe.chalk.takoyaki.target;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pe.chalk.takoyaki.Staff;
import pe.chalk.takoyaki.Takoyaki;
import pe.chalk.takoyaki.filter.naver.cafe.ArticleFilter;
import pe.chalk.takoyaki.filter.naver.cafe.CommentaryFilter;
import pe.chalk.takoyaki.filter.naver.cafe.VisitationFilter;
import pe.chalk.takoyaki.model.Menu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-07
 */
public class NaverCafe extends Target<Document[]> {
    private static final String STRING_CONTENT = "http://cafe.naver.com/%s.cafe";
    private static final String STRING_ARTICLE = "http://cafe.naver.com/ArticleList.nhn?search.clubid=%d&search.boardtype=L";
    private static final Pattern PATTERN_CLUB_ID = Pattern.compile("var g_sClubId = \"(\\d+)\";");

    private String contentUrl;
    private String articleUrl;

    private final String address;
    private final int clubId;
    private List<Menu> menus;

    public NaverCafe(JSONObject properties){
        super(properties.getString("prefix"), properties.getLong("interval"));
        this.getFilters().addAll(Takoyaki.<String>buildStream(properties.getJSONArray("filters")).parallel().map(filterName -> {
            switch(filterName){
                case ArticleFilter.NAME:
                    return new ArticleFilter(this);

                case CommentaryFilter.NAME:
                    return new CommentaryFilter(this);

                case VisitationFilter.NAME:
                    return new VisitationFilter(this);

                default:
                    return null;
            }
        }).filter(filter -> filter != null).collect(Collectors.toList()));

        this.staff = new Staff(this.getLogger(), properties.getInt("timeout"), "EUC-KR", properties.getJSONObject("naverAccount"));
        this.address = properties.getString("address");
        this.contentUrl = String.format(STRING_CONTENT, this.getAddress());

        try{
            Document contentDocument = Jsoup.parse(this.getStaff().parse(this.contentUrl));
            this.setName(contentDocument.select("h1.d-none").text());

            Matcher clubIdMatcher = NaverCafe.PATTERN_CLUB_ID.matcher(contentDocument.head().select("script:not([type]):not([src])").first().html());
            if(!clubIdMatcher.find()){
                throw new IllegalArgumentException("카페 ID를 찾을 수 없습니다: " + this.getName());
            }

            this.clubId = Integer.parseInt(clubIdMatcher.group(1));
            this.menus = contentDocument.select("a[id^=menuLink]").stream().map(element -> new Menu(this, Integer.parseInt(element.id().substring(8)), element.text())).collect(Collectors.toList());

            this.articleUrl = String.format(STRING_ARTICLE, this.getClubId());

            Files.write(Paths.get("Takoyaki-menus-" + this.getAddress() + ".log"), this.getMenus().stream().map(Menu::toString).collect(Collectors.toList()), StandardCharsets.UTF_8);
        }catch(IOException | JSONException e){
            String errorMessage = "모니터링이 불가합니다: " + e.getClass().getName() + ": " + e.getMessage();

            this.getLogger().error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    @Override
    public Document[] getDocument() throws Exception {
        return new Document[]{ Jsoup.parse(this.getStaff().parse(this.contentUrl)), Jsoup.parse(this.getStaff().parse(this.articleUrl)) };
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
    public String toString(){
        return this.getClubId() + " (" + super.toString() + ")";
    }
}