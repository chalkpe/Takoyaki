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

import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pe.chalk.takoyaki.data.SimpleArticle;

import java.util.ArrayList;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public class CommentaryFilter extends Filter<SimpleArticle> {
    public static final String NAME = "commentary";

    public CommentaryFilter(JSONArray options){
        super(options);
    }

    @Override
    public ArrayList<SimpleArticle> filter(Document document){
        Elements elements = document.select("#recent-reply .ellipsis.tcol-c");
        ArrayList<SimpleArticle> list = new ArrayList<>(10);

        for(Element element : elements){
            String articleIdAttr = element.parent().attr("href");

            int id = Integer.parseInt(articleIdAttr.substring(articleIdAttr.lastIndexOf('=') + 1));
            String title = element.text();

            list.add(new SimpleArticle(id, title));
        }

        return list;
    }
}
