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

import pe.chalk.takoyaki.logger.PrefixedLogger;
import pe.chalk.takoyaki.model.Article;
import pe.chalk.takoyaki.model.Data;
import pe.chalk.takoyaki.model.Member;
import pe.chalk.takoyaki.model.SimpleArticle;
import pe.chalk.takoyaki.target.Target;
import pe.chalk.takoyaki.utils.Prefix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-07
 */
public abstract class Filter<D, T extends Data> implements Prefix {
    private Target<D> target;
    private PrefixedLogger logger;

    private List<T> lastData;

    public Filter(Target<D> target){
        this.target = target;
        this.logger = new PrefixedLogger(target.getLogger(), this);
    }

    public Target<D> getTarget(){
        return this.target;
    }

    public PrefixedLogger getLogger(){
        return this.logger;
    }

    protected abstract List<T> filter(D document);

    public List<T> getFreshData(D document){
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

            //TODO: Replace it by Comparable<T>
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