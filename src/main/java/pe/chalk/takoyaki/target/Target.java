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
import pe.chalk.takoyaki.event.Event;
import pe.chalk.takoyaki.event.EventHandler;
import pe.chalk.takoyaki.filter.Filter;
import pe.chalk.takoyaki.logger.PrefixedLogger;
import pe.chalk.takoyaki.model.Data;
import pe.chalk.takoyaki.utils.Prefix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    protected Target(String prefix, long interval){
        this(prefix, interval, new ArrayList<>(), null);
    }

    protected Target(String prefix, long interval, List<Filter<D, ? extends Data>> filters){
        this(prefix, interval, filters, null);
    }

    protected Target(String prefix, long interval, List<Filter<D, ? extends Data>> filters, Staff staff){
        this.prefix = prefix;
        this.logger = new PrefixedLogger(Takoyaki.getInstance().getLogger(), this);

        this.interval = interval;
        this.filters = filters;
        this.staff = staff;
    }

    public static Target<?> create(JSONObject properties){
        final String type = properties.getString("type").toLowerCase();
    	if(!Takoyaki.getInstance().getTargetClasses().containsKey(type)) throw new IllegalArgumentException("Unknown type: " + type);

        try {
            return (Target<?>) Takoyaki.getInstance().getTargetClasses().get(type).getConstructor(JSONObject.class).newInstance(properties);
        }catch(ReflectiveOperationException e){
            e.printStackTrace();
            return null;
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

                final Event event = new Event(filter, list);

                list.forEach(data -> filter.getLogger().info(data.toString()));

                List<EventHandler> handlers = Takoyaki.getInstance().getPlugins().parallelStream().filter(EventHandler.class::isInstance).map(EventHandler.class::cast).collect(Collectors.toList());
                if(handlers.parallelStream().allMatch(handler -> handler.checkEvent(event))) handlers.parallelStream().forEach(handler -> handler.handleEvent(event));
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
