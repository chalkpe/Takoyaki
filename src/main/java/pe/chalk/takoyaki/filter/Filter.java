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
import pe.chalk.takoyaki.data.Data;

import java.util.ArrayList;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public abstract class Filter<T extends Data> {
    private JSONArray options;

    public Filter(JSONArray options){
        this.options = options;
    }

    public JSONArray getOptions(){
        return this.options;
    }

    public abstract ArrayList<T> filter(Document document);
}
