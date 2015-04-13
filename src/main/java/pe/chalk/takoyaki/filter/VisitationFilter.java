/*
 * Copyright 2015 ChalkPE
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

import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pe.chalk.takoyaki.data.Data;
import pe.chalk.takoyaki.data.Member;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public class VisitationFilter extends Filter<Member> {
    public static final String NAME = "visitation";

    public static final Pattern ID_PATTERN = Pattern.compile("'([0-9a-z_]+)'");

    public VisitationFilter(JSONObject options){
        super(options);
    }

    @Override
    public ArrayList<Member> filter(Document document){
        Elements elements = document.select("#first-visit-page [href=#]");
        ArrayList<Member> list = new ArrayList<>(5);

        for(Element element : elements){
            String id = null;
            String name = element.child(0).child(0).text();

            Matcher idMatcher = VisitationFilter.ID_PATTERN.matcher(element.attr("onclick"));
            if(idMatcher.find()){
                id = idMatcher.group(1);
            }

            list.add(new Member(id, name));
        }

        return list;
    }
}