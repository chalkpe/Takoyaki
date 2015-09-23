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

package pe.chalk.takoyaki.data;

import org.json.JSONObject;
import pe.chalk.takoyaki.Takoyaki;
import pe.chalk.takoyaki.Target;
import pe.chalk.takoyaki.utils.Prefix;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-07
 */
public abstract class Data implements Prefix {
    private final int targetId;
    private final long creationTime;

    public Data(int targetId){
        this.targetId = targetId;
        this.creationTime = System.currentTimeMillis();
    }

    public int getTargetId(){
        return this.targetId;
    }

    public Target getTarget(){
        return Takoyaki.getInstance().getTarget(this.getTargetId());
    }

    public long getCreationTime(){
        return this.creationTime;
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("class", this.getClass().getCanonicalName());
        jsonObject.put("creationTime", this.getCreationTime());

        return jsonObject;
    }
}