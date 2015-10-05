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

package pe.chalk.takoyaki.model;

import org.json.JSONObject;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-16
 */
public class Menu extends Data {
    private final int id;
    private final String name;

    public Menu(int targetId, int id, String name){
        super(targetId);

        this.id = id;
        this.name = name;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public String toString(){
        return "[" + this.getId() + "#" + this.getName() + "]";
    }

    @Override
    public boolean equals(Object another){
        return another instanceof Menu && this.getId() == ((Menu) another).getId();
    }

    @Override
    public String getPrefix(){
        return this.getName();
    }

    @Override
    public JSONObject toJSON(){
        JSONObject jsonObject = super.toJSON();

        jsonObject.put("id", this.getId());
        jsonObject.put("name", this.getName());

        return jsonObject;
    }
}