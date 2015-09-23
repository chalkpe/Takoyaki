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
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import pe.chalk.takoyaki.logger.Logger;
import pe.chalk.takoyaki.logger.LoggerStream;
import pe.chalk.takoyaki.plugin.Plugin;
import pe.chalk.takoyaki.utils.Prefix;
import pe.chalk.takoyaki.utils.TextFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-07
 */
public class Takoyaki implements Prefix {
    public static final String VERSION = "2.1.1";

    private static Takoyaki instance = null;
    private static List<String> DEFAULT_CONFIG = Arrays.asList(
            "{",
            "  \"options\": {\"excluded\": [\"debug.js\"]},",
            "  \"targets\": [",
            "    {",
            "      \"address\": \"minecraftpe\",",
            "      \"prefix\": \"M.K\",",
            "      \"interval\": 2500,",
            "      \"filters\": [",
            "        \"article\",",
            "        \"commentary\",",
            "        \"visitation\"",
            "      ],",
            "      \"timeout\": 1000",
            "    },",
            "    {",
            "      \"address\": \"ourmcspace\",",
            "      \"prefix\": \"PMC\",",
            "      \"interval\": 5000,",
            "      \"filters\": [",
            "        \"article\",",
            "        \"commentary\",",
            "        \"visitation\"",
            "      ],",
            "      \"timeout\": 2000",
            "    }",
            "  ],",
            "}");

    private List<String> excluded;

    private List<Target> targets;
    private List<Plugin> plugins;
    private Logger logger;

    private boolean isAlive = false;

    public static Takoyaki getInstance(){
        return instance;
    }

    public Takoyaki() throws JSONException, IOException {
        Takoyaki.instance = this;
        this.init();
    }

    private void init() throws JSONException, IOException {
        this.logger = new Logger();
        this.logger.addStream(new LoggerStream(TextFormat.Type.ANSI, System.out));
        this.logger.addStream(new LoggerStream(TextFormat.Type.NONE, new PrintStream(new FileOutputStream("Takoyaki.log", true), true, "UTF-8")));

        Path propertiesPath = Paths.get("properties.json");
        if(!Files.exists(propertiesPath)){
            Files.write(propertiesPath, DEFAULT_CONFIG, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        }
        JSONObject properties = new JSONObject(Files.lines(propertiesPath, Charset.forName("UTF-8")).collect(Collectors.joining()));
        Files.write(propertiesPath, properties.toString(2).getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

        JSONArray excludedArray = properties.getJSONObject("options").getJSONArray("excluded");
        this.excluded = new ArrayList<>();
        for(int i = 0; i < excludedArray.length(); i++){
            this.excluded.add(excludedArray.getString(i));
        }

        JSONArray targetsArray = properties.getJSONArray("targets");
        this.targets = new ArrayList<>(targetsArray.length());
        for(int i = 0; i < targetsArray.length(); i++){
            this.targets.add(new Target(this, targetsArray.getJSONObject(i)));
        }

        this.loadPlugins();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Takoyaki.this.getLogger().critical("*** FINALIZATION RUNNING ***");
            Takoyaki.this.getPlugins().forEach(plugin -> plugin.call("onDestroy", Context.emptyArgs));
        }));
    }

    private void loadPlugins() throws IOException {
        Path pluginsPath = Paths.get("plugins");
        if(!Files.exists(pluginsPath)){
            Files.createDirectories(pluginsPath);
        }

        this.plugins = new ArrayList<>();
        Files.list(pluginsPath).filter(path -> path.getFileName().toString().endsWith(".js") && !this.excluded.contains(path.getFileName().toString())).map(Path::toFile).forEach(pluginFile -> {
            try{
                Plugin plugin = new Plugin(pluginFile);
                plugin.call("onCreate", new Object[]{plugin.getName()});

                Object version = plugin.get("VERSION");

                this.plugins.add(plugin);
                this.logger.info("플러그인을 불러옵니다: " + plugin.getName() + (version != null ? " v" + plugin.get("VERSION") : ""));
            }catch(JavaScriptException | IOException e){
                this.getLogger().error(e.getMessage());
            }
        });
    }

    public void start(){
        this.isAlive = true;

        this.getTargets().forEach(target -> {target.start(); this.getLogger().info("모니터링을 시작합니다: " + target.getName() + " (ID: " + target.getClubId() + ")");});
        this.getPlugins().forEach(plugin -> plugin.call("onStart", Context.emptyArgs));
    }

    public List<Target> getTargets(){
        return this.targets;
    }

    public Target getTarget(int clubId){
        for(Target target : this.getTargets()){
            if(target.getClubId() == clubId){
                return target;
            }
        }
        return null;
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

    public static void main(String[] args){
        try{
            new Takoyaki().start();
        }catch(JSONException | IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}
