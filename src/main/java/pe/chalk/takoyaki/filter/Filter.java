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
import pe.chalk.takoyaki.Target;
import pe.chalk.takoyaki.data.*;
import pe.chalk.takoyaki.logger.Prefix;
import pe.chalk.takoyaki.logger.PrefixedLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public abstract class Filter<T extends Data> implements Prefix {
    private Target target;
    private String prefix;
    private PrefixedLogger logger;

    private List<T> lastData;

    public Filter(Target target, JSONObject jsonObject){
        this.target = target;
        this.prefix = jsonObject == null ? "" : jsonObject.getString("prefix");
        this.logger = target.getLogger().sub(this);
    }

    public Target getTarget(){
        return this.target;
    }

    public String getPrefix(){
        return this.prefix;
    }

    public PrefixedLogger getLogger(){
        return this.logger;
    }

    protected abstract List<T> filter(Document document);

    public List<T> getFreshData(Document document){
        List<T> rawData = this.filter(document);
        List<T> freshData = new ArrayList<>();

        int count = this.getFreshItemCount(rawData);
        if(count > 0){
            freshData.addAll(rawData.subList(0, count));
            Collections.reverse(freshData);
        }

        this.lastData = rawData;
        return freshData;
    }

    private int getFreshItemCount(List<T> data){
        if(this.lastData == null || this.lastData.size() <= 0){
            return 0;
        }
        T lastItem = this.lastData.get(0);

        for(int i = 0; i < data.size(); i++){
            T item = data.get(i);
            if(item instanceof Article){
                Article lastArticle = (Article) lastItem;
                Article article = (Article) item;

                if(article.getId() <= lastArticle.getId()){
                    return i;
                }
            }else if(item instanceof SimpleArticle || item instanceof Member){
                if(item.equals(lastItem)){
                    return i;
                }
            }
        }
        return 0;
    }
}