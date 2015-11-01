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

package pe.chalk.takoyaki.filter.naver.cafe;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pe.chalk.takoyaki.model.naver.NaverMember;
import pe.chalk.takoyaki.model.naver.cafe.Article;
import pe.chalk.takoyaki.model.Member;
import pe.chalk.takoyaki.target.NaverCafe;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-07
 */
public class ArticleFilter extends NaverCafeFilter<Article> {
    public static final String NAME = "article";

    private static final Pattern MEMBER_ID_PATTERN = Pattern.compile("ui\\(event, '([a-z0-9-_]+)',");
    private static final Pattern MENU_ID_PATTERN = Pattern.compile("'(\\d+)'\\);");

    public ArticleFilter(NaverCafe target){
        super(target);
    }
    
    @Override
    public String getPrefix(){
        return "새글";
    }

    @Override
    public List<Article> filter(Document[] documents){
        return this.filter(documents, "form[name=ArticleList] tr[align=center]");
    }

    public List<Article> filter(Document[] documents, String cssQuery){
        return documents[1].select(cssQuery).stream()
                .map(element -> {
                    int articleId = Integer.parseInt(element.select(".m-tcol-c.list-count").first().text());

                    Elements countElements = element.select(".view-count.m-tcol-c");

                    String uploadDate = countElements.get(0).text().trim();
                    int viewCount = Integer.parseInt(countElements.get(1).text());
                    int recommendedCount = Integer.parseInt(countElements.get(2).text());

                    boolean isQuestion = !element.select(".ico-q.m-tcol-p").isEmpty();

                    int commentCount = 0;
                    Elements commentElem = element.select(".aaa a[href=#] strong");
                    if(!commentElem.isEmpty()){
                        commentCount = Integer.parseInt(commentElem.first().text());
                    }

                    String head = element.select(".head").text().trim();
                    if(head.startsWith("[") && head.endsWith("]")){
                        head = head.substring(1, head.length() - 1);
                    }
                    String title = element.select(".aaa a").first().text();

                    Element nicknameElement = element.select(".p-nick a[href=#]").first();
                    String memberName = nicknameElement.child(0).text();

                    //Element memberLevelElement = nicknameElement.select("img.mem-level").first();
                    //TODO: url to valid member level value (0, 1, staff, manager etc)

                    String memberId = null;
                    Matcher memberIdMatcher = ArticleFilter.MEMBER_ID_PATTERN.matcher(nicknameElement.attr("onclick"));
                    if(memberIdMatcher.find()){
                        memberId = memberIdMatcher.group(1);
                    }

                    int menuId = -1;
                    Matcher menuIdMatcher = ArticleFilter.MENU_ID_PATTERN.matcher(nicknameElement.attr("onclick"));
                    if(menuIdMatcher.find()){
                        menuId = Integer.parseInt(menuIdMatcher.group(1));
                    }

                    Member writer = new NaverMember(this.getTarget(), memberId, memberName);
                    return new Article(this.getTarget(), articleId, title, writer, head, uploadDate, menuId, viewCount, commentCount, recommendedCount, isQuestion);
                }).collect(Collectors.toList());
    }
}