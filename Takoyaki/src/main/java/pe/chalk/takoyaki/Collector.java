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

import org.jsoup.nodes.Document;
import pe.chalk.takoyaki.model.Data;
import pe.chalk.takoyaki.filter.ContentFilter;
import pe.chalk.takoyaki.filter.Filter;

import java.util.List;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-12
 */
public class Collector {
    private List<Filter<? extends Data>> filters;

    public Collector(List<Filter<? extends Data>> filters){
        this.filters = filters;
    }

    public List<Filter<? extends Data>> getFilters(){
        return this.filters;
    }

    public void collect(Document contentDocument, Document articleDocument){
        try{
            this.getFilters().forEach(filter -> {
                List<? extends Data> list = filter.getFreshData(filter instanceof ContentFilter ? contentDocument : articleDocument);

                list.forEach(data -> filter.getLogger().info(data.toString()));
                Takoyaki.getInstance().getPlugins().forEach(plugin -> plugin.onDataAdded(list, filter));
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
