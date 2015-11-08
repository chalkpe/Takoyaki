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

package pe.chalk.takoyaki.target;

import org.json.JSONArray;
import org.json.JSONObject;
import pe.chalk.takoyaki.Staff;
import pe.chalk.takoyaki.filter.namu.wiki.StatusFilter;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-11-01
 */
public class NamuWiki extends Target<JSONArray> {
    public NamuWiki(JSONObject properties){
        super(properties.getString("prefix"), properties.getLong("interval"));
        this.getFilters().add(new StatusFilter(this));

        this.setName(this.getName() + " (나무위키)");
        this.staff = new Staff(this.getLogger(), properties.getInt("timeout"), "UTF-8");
    }

    public static String getType(){
        return "namu.wiki";
    }

    @Override
    public JSONArray getDocument() throws Exception{
        return new JSONArray(this.getStaff().parse("https://namu.wiki/sidebar.json"));
    }
}
