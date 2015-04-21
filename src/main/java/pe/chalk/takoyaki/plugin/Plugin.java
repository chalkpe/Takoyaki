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

package pe.chalk.takoyaki.plugin;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import pe.chalk.takoyaki.logger.Prefix;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-19
 */
public class Plugin implements Prefix {
    private String name;
    private Scriptable scriptable;

    public Plugin(String name, Scriptable scriptable){
        this.name = name;
        this.scriptable = scriptable;
    }

    public String getName(){
        return this.name;
    }

    public Scriptable getScriptable(){
        return this.scriptable;
    }

    public Object call(String functionName, Object[] args){
        Context context = Context.enter();
        try{
            Object object = this.getScriptable().get(functionName, this.getScriptable());
            if(object != null && object instanceof Function){
                return ((Function) object).call(context, scriptable, scriptable, args);
            }
            return null;
        }finally{
            Context.exit();
        }
    }

    @Override
    public String toString(){
        return this.getName();
    }

    @Override
    public String getPrefix(){
        return this.getName();
    }
}