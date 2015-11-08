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
import pe.chalk.takoyaki.model.naver.NaverMember;
import pe.chalk.takoyaki.target.NaverCafe;
import pe.chalk.takoyaki.model.Member;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-07
 */
public class VisitationFilter extends NaverCafeFilter<Member> {
    public static final String NAME = "visitation";

    private static final Pattern ID_PATTERN = Pattern.compile("'([0-9a-z-_]+)'");

    public VisitationFilter(NaverCafe target){
        super(target);
    }
    
    @Override
    public String getPrefix(){
        return "방문";
    }

    @Override
    public List<Member> filter(Document[] documents){
        return documents[0].select("div#member-news ul.group-list li span.id a.tcol-c").stream()
                .map(element -> {
                    String id = null;
                    String name = element.text();

                    Matcher idMatcher = VisitationFilter.ID_PATTERN.matcher(element.attr("onclick"));
                    if(idMatcher.find()){
                        id = idMatcher.group(1);
                    }

                    return new NaverMember(this.getTarget(), id, name);
                }).collect(Collectors.toList());
    }
}