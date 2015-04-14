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
import pe.chalk.takoyaki.data.Article;
import pe.chalk.takoyaki.data.Data;
import pe.chalk.takoyaki.data.Member;
import pe.chalk.takoyaki.data.SimpleArticle;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public abstract class Filter<T extends Data> {
    private JSONObject options;

    private ArrayList<T> lastData;

    public Filter(JSONObject options){
        this.options = options;
    }

    public JSONObject getOptions(){
        return this.options;
    }

    protected abstract ArrayList<T> filter(Document document);

    private int getFreshItemCount(ArrayList<T> data){
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

    public ArrayList<T> getFreshData(Document document){
        ArrayList<T> rawData = this.filter(document);
        ArrayList<T> freshData = new ArrayList<>();

        int count = this.getFreshItemCount(rawData);
        if(count > 0){
            freshData.addAll(rawData.subList(0, count));
            Collections.reverse(freshData);
        }

        this.lastData = rawData;
        return freshData;
    }
}