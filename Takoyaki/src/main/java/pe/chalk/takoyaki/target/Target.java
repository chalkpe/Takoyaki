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

import org.json.JSONObject;
import pe.chalk.takoyaki.Staff;
import pe.chalk.takoyaki.Takoyaki;
import pe.chalk.takoyaki.filter.Filter;
import pe.chalk.takoyaki.logger.PrefixedLogger;
import pe.chalk.takoyaki.model.Data;
import pe.chalk.takoyaki.utils.Prefix;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-11-01
 */
public abstract class Target<D> extends Thread implements Prefix {
    private String prefix;
    private PrefixedLogger logger;

    private long interval;
    private List<Filter<D, ? extends Data>> filters;
    protected Staff staff;

    public Target(String prefix, long interval){
        this(prefix, interval, new ArrayList<>(), null);
    }

    public Target(String prefix, long interval, List<Filter<D, ? extends Data>> filters){
        this(prefix, interval, filters, null);
    }

    public Target(String prefix, long interval, List<Filter<D, ? extends Data>> filters, Staff staff){
        this.prefix = prefix;
        this.logger = new PrefixedLogger(Takoyaki.getInstance().getLogger(), this);

        this.interval = interval;
        this.filters = filters;
        this.staff = staff;
    }

    public static Target<?> create(JSONObject properties){
    	Class<? extends Target> targetClass = Takoyaki.getInstance().getTargetClasses().get(properties.getString("type").toLowerCase());
    	if (targetClass == null) {
    		throw new IllegalArgumentException("Unknown type");
    	} else {
    		try {
    			return (Target<?>) targetClass.getConstructor(JSONObject.class).newInstance(properties);
    		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
    				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
    			e.printStackTrace();
    			return null;
    		}
    	}
    }

    @Override
    public final String getPrefix(){
        return this.prefix;
    }

    public PrefixedLogger getLogger(){
        return this.logger;
    }

    public long getInterval(){
        return this.interval;
    }

    public List<Filter<D, ?>> getFilters(){
        return this.filters;
    }

    public Staff getStaff(){
        return this.staff;
    }

    @Override
    public void interrupt(){
        super.interrupt();
        if(this.getStaff() != null) this.getStaff().close();
    }

    @Override
    public void run(){
        this.getLogger().getParent().info("모니터링을 시작합니다: " + this);
        while(Takoyaki.getInstance().isAlive() && !this.isInterrupted()){
            try{
                Thread.sleep(this.getInterval());
                this.collect(this.getDocument());
            }catch(InterruptedException e){
                return;
            }catch(Exception e){
                this.getLogger().error(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    public abstract D getDocument() throws Exception;

    public void collect(D document){
        try{
            this.getFilters().forEach(filter -> {
                List<? extends Data> list = filter.getFreshData(document);
                if(list.isEmpty()) return;

                list.forEach(data -> filter.getLogger().info(data.toString()));
                Takoyaki.getInstance().getPlugins().forEach(plugin -> plugin.onDataAdded(list, filter));
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return this.getName();
    }
}
