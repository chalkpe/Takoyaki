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
import pe.chalk.takoyaki.data.Data;
import pe.chalk.takoyaki.filter.ArticleFilter;
import pe.chalk.takoyaki.filter.CommentaryFilter;
import pe.chalk.takoyaki.filter.Filter;
import pe.chalk.takoyaki.filter.VisitationFilter;

import java.io.IOException;
import java.net.MalformedURLException;
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
    private Provider provider;

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
        this.interval = properties.getLong("interval");
        this.timeout = properties.getInt("timeout");

        JSONArray collectorsArray = properties.getJSONArray("collectors");
        ArrayList<Collector> collectors = new ArrayList<>(collectorsArray.length());

        for(int ci = 0; ci < collectorsArray.length(); ci++){
            JSONObject collectorObject = collectorsArray.getJSONObject(ci);
            Collector collector;

            JSONArray filtersArray = collectorObject.getJSONArray("filters");
            ArrayList<Filter<? extends Data>> filters = new ArrayList<>(filtersArray.length());
            for(int fi = 0; fi < filtersArray.length(); fi++){
                JSONObject filterObject = filtersArray.getJSONObject(fi);
                JSONObject filterOptions = filterObject.getJSONObject("options");

                switch(filterObject.getString("type").toLowerCase()){
                    case VisitationFilter.NAME:
                        filters.add(new VisitationFilter(filterOptions));
                        break;
                    case CommentaryFilter.NAME:
                        filters.add(new CommentaryFilter(filterOptions));
                        break;
                    case ArticleFilter.NAME:
                        filters.add(new ArticleFilter(filterOptions));
                        break;

                    default:
                        throw new IllegalArgumentException();
                }
            }

            switch(collectorObject.getString("subscription").toLowerCase()){
                case "article":
                    collector = new Collector(Collector.Subscription.ARTICLE, filters);
                    break;

                case "widget":
                    collector = new Collector(Collector.Subscription.WIDGET, filters);
                    break;

                default:
                    throw new IllegalArgumentException();
            }
            collectors.add(collector);
        }

        this.provider = new Provider(properties.getJSONObject("target"), collectors);
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
        this.provider.monitor();
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