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
import org.mozilla.javascript.*;
import pe.chalk.takoyaki.logger.Prefix;
import pe.chalk.takoyaki.logger.ConsoleLogger;
import pe.chalk.takoyaki.plugin.Plugin;

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
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public class Takoyaki implements Prefix {
    public static final String VERSION = "2.0.1-SNAPSHOT";

    private static Takoyaki instance = null;
    private static List<String> DEFAULT_CONFIG = Arrays.asList(
            "{",
            "  \"targets\": [",
            "    {",
            "      \"address\": \"minecraftpe\",",
            "      \"prefix\": \"M.K\",",
            "      \"interval\": 2500,",
            "      \"timeout\": 1000,",
            "      \"filters\": [\"article\", \"commentary\", \"visitation\"]",
            "    }",
            "  ],",
            "  \"options\": {",
            "    \"output\": \"Takoyaki.log\",",
            "    \"excluded\": [\"debug.js\"]",
            "  }",
            "}");

    private List<Target> targets;
    private List<Plugin> plugins;
    private ConsoleLogger logger;

    private boolean isAlive = false;

    public static Takoyaki getInstance(){
        return instance;
    }

    public Takoyaki() throws JSONException, IOException {
        Takoyaki.instance = this;

        Path propertiesPath = Paths.get("properties.json");
        if(!Files.exists(propertiesPath)){
            Files.write(propertiesPath, DEFAULT_CONFIG, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        }
        JSONObject properties = new JSONObject(Files.lines(propertiesPath, Charset.forName("UTF-8")).collect(Collectors.joining()));

        JSONObject optionsObject = properties.getJSONObject("options");
        this.logger = new ConsoleLogger(new PrintStream(new FileOutputStream(optionsObject.getString("output"), true)));

        JSONArray excludedArray = optionsObject.getJSONArray("excluded");
        ArrayList<String> excluded = new ArrayList<>();
        for(int i = 0; i < excludedArray.length(); i++){
            excluded.add(excludedArray.getString(i));
        }

        JSONArray targetsArray = properties.getJSONArray("targets");
        this.targets = new ArrayList<>(targetsArray.length());
        for(int i = 0; i < targetsArray.length(); i++){
            this.targets.add(new Target(this, targetsArray.getJSONObject(i)));
        }

        Path pluginsPath = Paths.get("plugins");
        if(!Files.exists(pluginsPath)){
            Files.createDirectories(pluginsPath);
            this.getLogger().debug("plugins 디렉토리를 생성했습니다");
        }

        this.plugins = new ArrayList<>();
        Files.list(pluginsPath).filter(path -> path.endsWith(".js") && !excluded.contains(path.getFileName().toString())).map(Path::toFile).forEach(pluginFile -> {
            try{
                Plugin plugin = new Plugin(pluginFile);

                this.getLogger().debug("플러그인 " + plugin.getName() + "을(를) 불러왔습니다");
                this.plugins.add(plugin);
            }catch(JavaScriptException | IOException e){
                this.getLogger().error(e.getMessage());
            }
        });

        this.plugins.forEach(plugin -> plugin.call("onCreate", new Object[]{plugin.getName()}));
        this.getLogger().newLine();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run(){
                Takoyaki.this.getLogger().newLine();
                Takoyaki.this.getLogger().debug("*** FINALIZATION RUNNING ***");
                Takoyaki.this.getLogger().newLine();

                Takoyaki.this.getPlugins().forEach(plugin -> plugin.call("onDestroy", Context.emptyArgs));
            }
        });

        Files.write(propertiesPath, properties.toString().getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
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

    public ConsoleLogger getLogger(){
        return this.logger;
    }

    public boolean isAlive(){
        return this.isAlive;
    }

    @Override
    public String getPrefix(){
        return "타코야키";
    }

    public void start(){
        this.isAlive = true;
        this.getTargets().forEach(Target::start);
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