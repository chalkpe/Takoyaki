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

package pe.chalk.takoyaki.filter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pe.chalk.takoyaki.Target;
import pe.chalk.takoyaki.data.Article;
import pe.chalk.takoyaki.data.Member;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-07
 */
public class ArticleFilter extends Filter<Article> {
    public static final String NAME = "article";

    private static final Pattern MEMBER_ID_PATTERN = Pattern.compile("ui\\(event, '([a-z0-9_]+)',");
    private static final Pattern MENU_ID_PATTERN = Pattern.compile("'(\\d+)'\\);");

    public ArticleFilter(Target target){
        super(target);
    }
    
    @Override
    public String getPrefix(){
        return "새글";
    }

    @Override
    public List<Article> filter(Document document){
        return document.select("form[name=ArticleList] tr[align=center]").stream()
                .map(element -> {
                    int articleId = Integer.parseInt(element.select(".m-tcol-c.list-count").first().text());
                    int viewCount = Integer.parseInt(element.select(".view-count.m-tcol-c._rosReadcount").first().text());

                    Elements countElements = element.select(".view-count.m-tcol-c");
                    String uploadDate = countElements.get(0).text().trim();

                    int recommendedCount = Integer.parseInt(countElements.get(1).text());

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

                    Element memberLevelElement = nicknameElement.select("img.mem-level").first();
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

                    Member writer = new Member(this.getTarget().getClubId(), memberName, memberId);
                    return new Article(this.getTarget().getClubId(), articleId, title, writer, head, uploadDate, menuId, viewCount, commentCount, recommendedCount, isQuestion);
                }).collect(Collectors.toList());
    }
}