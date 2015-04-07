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

package pe.chalk.takoyaki;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pe.chalk.takoyaki.filter.ArticleFilter;
import pe.chalk.takoyaki.filter.CommentaryFilter;
import pe.chalk.takoyaki.filter.Filter;
import pe.chalk.takoyaki.filter.VisitationFilter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public class Takoyaki {
    private static Takoyaki instance = null;

    private JSONObject properties;
    private Monitor monitor;

    public static Takoyaki getInstance(){
        return instance;
    }

    private long interval;
    private int timeout;

    private boolean isAlive;

    public Takoyaki(JSONObject properties){
        this.properties = properties;
    }

    public void init() throws JSONException, MalformedURLException {
        URL target = new URL(properties.getString("url"));
        this.interval = properties.getLong("interval");
        this.timeout = properties.getInt("timeout");

        JSONArray filtersArray = properties.getJSONArray("filters");
        ArrayList<Filter> filters = new ArrayList<>(filtersArray.length());

        for(int i = 0; i < filtersArray.length(); i++){
            JSONObject filterObject = filtersArray.getJSONObject(i);
            JSONArray filterOptions = filterObject.getJSONArray("options");

            switch(filterObject.getString("name").toLowerCase()){
                case ArticleFilter.NAME:
                    filters.add(new ArticleFilter(filterOptions));
                    break;
                case VisitationFilter.NAME:
                    filters.add(new VisitationFilter(filterOptions));
                    break;
                case CommentaryFilter.NAME:
                    filters.add(new CommentaryFilter(filterOptions));
                    break;
            }
        }

        this.monitor = new Monitor(target, filters);
        this.isAlive = true;
    }

    public long getInterval(){
        return interval;
    }

    public int getTimeout(){
        return timeout;
    }

    public boolean isAlive(){
        return this.isAlive;
    }

    public void tick() throws Exception {
        this.monitor.monitor();
    }

    public static void main(String[] args){
        if(args.length < 1){
            System.out.println("Usage: java -jar Takoyaki.jar <properties.json>");
            System.exit(1);
        }

        try{
            Path path = Paths.get(args[0]);
            String raw = Files.lines(path).collect(Collectors.joining());
            JSONObject properties = new JSONObject(raw);

            Takoyaki.instance = new Takoyaki(properties);
            Takoyaki.instance.init();
        }catch(IOException | JSONException e){
            e.printStackTrace();
            System.exit(1);
        }

        new Thread(){
            @Override
            public void run(){
                while(Takoyaki.instance.isAlive()){
                    try{
                        Takoyaki.instance.tick();
                        Thread.sleep(Takoyaki.instance.getInterval());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}