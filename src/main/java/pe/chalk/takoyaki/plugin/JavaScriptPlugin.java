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
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import pe.chalk.takoyaki.Takoyaki;
import pe.chalk.takoyaki.event.Event;
import pe.chalk.takoyaki.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-19
 */
public class JavaScriptPlugin extends PluginBase implements EventHandler {
    private File file;
    private Scriptable scriptable;

    public JavaScriptPlugin(File file) throws IOException, RhinoException {
        super(file.getName().substring(0, file.getName().lastIndexOf(".")));

        this.file = file;
        this.initScriptable();
    }

    private void initScriptable() throws IOException, RhinoException {
        Context context = Context.enter();
        try{
            this.scriptable = new ImporterTopLevel(context);
            context.evaluateReader(this.getScriptable(), Files.newBufferedReader(this.getFile().toPath(), StandardCharsets.UTF_8), this.getName(), 0, null);

            ScriptableObject.putProperty(this.getScriptable(), "logger", this.getLogger());
            ScriptableObject.putProperty(this.getScriptable(), "takoyaki", Takoyaki.getInstance());
        }finally{
            Context.exit();
        }
    }

    public File getFile(){
        return this.file;
    }

    public Scriptable getScriptable(){
        return this.scriptable;
    }

    private Object getScriptableProperty(String name){
        Context.enter();
        try{
            return ScriptableObject.getProperty(this.getScriptable(), name);
        }catch(RhinoException e){
            this.getLogger().error(e.getMessage());
            return null;
        }finally{
            Context.exit();
        }
    }

    private Object callScriptableFunction(String functionName, Object... args){
        Context context = Context.enter();
        try{
            Object object = this.getScriptableProperty(functionName);
            if(object != null && object instanceof Function) return ((Function) object).call(context, scriptable, scriptable, args);
        }catch(RhinoException e){
            this.getLogger().error(e.getMessage());
        }finally{
            Context.exit();
        }

        return null;
    }

    @Override
    public String getVersion(){
        Object version = this.getScriptableProperty("VERSION");
        if(version == null) throw new NoSuchFieldError("VERSION");

        return version.toString();
    }

    @Override
    public void reload(){
        this.callScriptableFunction("reload");
    }

    @Override
    public void onLoad(){
        this.callScriptableFunction("onLoad");
    }

    @Override
    public void onEnable(){
        this.callScriptableFunction("onEnable");
    }

    @Override
    public void onDisable(){
        this.callScriptableFunction("onDisable");
    }

    @Override
    public void onStart(){
        this.callScriptableFunction("onStart");
    }

    @Override
    public void onStop(){
        this.callScriptableFunction("onStop");
    }

    @Override
    public boolean checkEvent(Event event){
        Object result = this.callScriptableFunction("checkEvent");
        if(result == null || !Boolean.class.isInstance(result)) return true;
        else return Boolean.class.cast(result);
    }

    @Override
    public void handleEvent(Event event){
        this.callScriptableFunction("handleEvent", event);
    }
}