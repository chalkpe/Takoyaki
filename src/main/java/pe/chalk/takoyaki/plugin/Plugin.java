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

import org.mozilla.javascript.*;
import pe.chalk.takoyaki.Takoyaki;
import pe.chalk.takoyaki.logger.Prefix;
import pe.chalk.takoyaki.logger.PrefixedLogger;

import java.io.*;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-19
 */
public class Plugin implements Prefix {
    private File file;
    private String name;
    private Scriptable scriptable;
    private PrefixedLogger logger;

    public Plugin(File file) throws JavaScriptException, IOException {
        this.file = file;
        this.name = file.getName().substring(0, file.getName().lastIndexOf("."));
        this.logger = Takoyaki.getInstance().getLogger().getPrefixed(this);

        Context context = Context.enter();
        try{
            this.scriptable = new ImporterTopLevel(context);
            context.evaluateReader(this.getScriptable(), new FileReader(this.getFile()), this.getName(), 0, null);
            ScriptableObject.putProperty(this.getScriptable(), "logger", this.getLogger());
        }finally{
            Context.exit();
        }

        try(ObjectInputStream stream = new ObjectInputStream(new FileInputStream(new File(this.getFile().getParentFile(), this.getName().concat(".ser"))))){
            Object data = stream.readObject();
            ScriptableObject.putProperty(this.getScriptable(), "data", data);
        }catch(Exception ignored){}
    }

    public File getFile(){
        return this.file;
    }

    public String getName(){
        return this.name;
    }

    public Scriptable getScriptable(){
        return this.scriptable;
    }

    public PrefixedLogger getLogger(){
        return this.logger;
    }

    public Object call(String functionName, Object[] args){
        Context context = Context.enter();
        try{
            Object object = this.getScriptable().get(functionName, this.getScriptable());
            if(object != null && object instanceof Function){
                return ((Function) object).call(context, scriptable, scriptable, args);
            }
        }catch(Exception e){
            this.getLogger().error(e.getMessage());
        }finally{
            Context.exit();
        }
        return null;
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