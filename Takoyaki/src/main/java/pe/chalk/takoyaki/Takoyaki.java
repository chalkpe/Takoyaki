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

import org.json.JSONArray;
import pe.chalk.takoyaki.logger.Logger;
import pe.chalk.takoyaki.plugin.Plugin;
import pe.chalk.takoyaki.target.Target;
import pe.chalk.takoyaki.utils.Prefix;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-07
 */
public class Takoyaki implements Prefix {
    public static final String VERSION = "2.2.2-SNAPSHOT";

    private static Takoyaki instance = null;

    private List<Target<?>> targets;
    private List<Plugin> plugins;
    Logger logger;

    private boolean isAlive = false;
    
    private Map<String, Class<? extends Target<?>>> targetClasses = new HashMap<>();
    
    public static Takoyaki getInstance(){
        if(Takoyaki.instance == null) new Takoyaki();
        return Takoyaki.instance;
    }

    private Takoyaki(){
        Takoyaki.instance = this;
    }

    public static <T> Stream<T> buildStream(Class<T> type, JSONArray array){
        return Takoyaki.buildStream(type, array, true);
    }

    public static <T> Stream<T> buildStream(Class<T> type, JSONArray array, boolean parallel){
        Stream.Builder<T> builder = Stream.builder();

        if(array != null) for(int i = 0; i < array.length(); i++){
            Object element = array.get(i);
            if(type.isInstance(element)) builder.add(type.cast(element));
        }

        Stream<T> stream = builder.build();
        return parallel ? stream.parallel() : stream;
    }


    
    public void loadPlugin(Plugin plugin) {
    	if (this.plugins == null) {
    		this.plugins = new ArrayList<Plugin>();
    	}
    	
    	this.plugins.add(plugin);
    	
    }
    
    public boolean unloadPlugin(Plugin plugin) {
    	if (this.plugins == null) {
    		this.plugins = new ArrayList<Plugin>();
    		return false;
    	}
    	
    	return this.plugins.remove(plugin);
    }

    public void addTargetClass(Class<? extends Target<?>> targetClass) throws ReflectiveOperationException {
    	this.getTargetClasses().put(targetClass.getMethod("getType").invoke(null).toString(), targetClass);
    }
    
    public void removeTargetClass(Class<? extends Target<?>> targetClass) throws ReflectiveOperationException {
    	this.getTargetClasses().remove(targetClass.getMethod("getType").invoke(null).toString());
    }

    public void remoteTargetClass(String type){
        this.getTargetClasses().remove(type);
    }
    
    public Map<String, Class<? extends Target<?>>> getTargetClasses(){
    	return this.targetClasses;
    }
    
    public void addTarget(Target<?> target){
    	if (this.isAlive())	target.start();
    	this.getTargets().add(target);
    }
    
    public void removeTarget(Target<?> target) {
    	if (this.isAlive()) target.interrupt();
       	this.getTargets().remove(target);
    }
    
    public void start(){
        if(this.isAlive) return;
        this.isAlive = true;
        
        this.getTargets().parallelStream().forEach(Target::start);
        this.getPlugins().parallelStream().forEach(Plugin::onStart);
    }

    public void shutdown(){
        this.stop("VM에 의한 종료");
    }
    
    public void stop(){
        this.stop("알 수 없음");
    }

    public void stop(String reason){
        if(!this.isAlive) return; this.isAlive = false;

        if(this.getLogger() != null)  this.getLogger().info("타코야키를 종료합니다: 사유: " + reason);
        if(this.getTargets() != null) this.getTargets().parallelStream().forEach(Thread::interrupt);
        if(this.getPlugins() != null) this.getPlugins().parallelStream().forEach(Plugin::onDestroy);
    }

    public List<Target<?>> getTargets(){
    	if (this.targets == null) this.targets = new ArrayList<Target<?>>();
        return this.targets;
    }

    public List<Plugin> getPlugins(){
        return this.plugins;
    }
    
    public Logger getLogger(){
        return this.logger;
    }

    public boolean isAlive(){
        return this.isAlive;
    }

    @Override
    public String getPrefix(){
        return "타코야키";
    }
    
}
