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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-07
 */
public class Takoyaki implements Prefix {
    public static final String VERSION = "2.1.2-SNAPSHOT";

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
            "      \"timeout\": 2500",
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
        if(Takoyaki.instance != null) Takoyaki.instance.stop();
        Takoyaki.instance = this;

        this.init();
    }

    private void init() throws JSONException, IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(Takoyaki.this::stop));

        this.logger = new Logger();
        this.logger.addStream(new LoggerStream(TextFormat.Type.ANSI, System.out));
        this.logger.addStream(new LoggerStream(TextFormat.Type.NONE, new PrintStream(new FileOutputStream("Takoyaki.log", true), true, "UTF-8")));

        this.getLogger().info("타코야키를 시작합니다: " + Takoyaki.VERSION);


        Path propertiesPath = Paths.get("Takoyaki.json");
        if(!Files.exists(propertiesPath)){
            this.getLogger().info("프로퍼티를 생성합니다: " + propertiesPath.toString());

            Files.write(propertiesPath, Takoyaki.DEFAULT_CONFIG, Charset.forName("UTF-8"));
        }


        this.getLogger().info("프로퍼티를 불러옵니다: " + propertiesPath);

        JSONObject properties = new JSONObject(Files.lines(propertiesPath, Charset.forName("UTF-8")).collect(Collectors.joining()));
        Files.write(propertiesPath, properties.toString(2).getBytes("UTF-8"));

        this.excluded = Takoyaki.<String>buildStream(properties.getJSONObject("options").getJSONArray("excluded")).collect(Collectors.toList());
        this.targets  = Takoyaki.<JSONObject>buildStream(properties.getJSONArray("targets")).map(Target::new).collect(Collectors.toList());

        this.loadPlugins();
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<T> buildStream(JSONArray array){
        Stream.Builder<T> builder = Stream.builder();

        if(array != null){
            for(int i = 0; i < array.length(); i++){
                builder.add((T) array.get(i));
            }
        }
        return builder.build();
    }

    private void loadPlugins() throws IOException {
        Path pluginsPath = Paths.get("plugins");
        if(!Files.exists(pluginsPath)){
            this.getLogger().info("플러그인을 불러옵니다: plugins 디렉토리 생성 중...");

            Files.createDirectories(pluginsPath);
        }

        this.plugins = new ArrayList<>();
        Files.list(pluginsPath).filter(path -> path.getFileName().toString().endsWith(".js") && !this.excluded.contains(path.getFileName().toString())).map(Path::toFile).forEach(pluginFile -> {
            try{
                Plugin plugin = new Plugin(pluginFile);
                plugin.call("onCreate", new Object[]{plugin.getName()});
                Object version = plugin.get("VERSION");

                this.plugins.add(plugin);
                this.logger.info("플러그인을 불러옵니다: " + plugin.getName() + (version != null ? " v" + version : ""));
            }catch(JavaScriptException | IOException e){
                this.getLogger().error(e.getMessage());
            }
        });
    }

    public void start(){
        this.isAlive = true;

        this.getTargets().forEach(Target::start);
        this.getPlugins().forEach(plugin -> plugin.call("onStart", Context.emptyArgs));
    }

    public void stop(){
        this.isAlive = false;

        if(this.getLogger() != null) this.getLogger().info("*** 타코야키를 종료합니다 ***");

        if(this.getTargets() != null) this.getTargets().forEach(Thread::interrupt);
        if(this.getPlugins() != null) this.getPlugins().forEach(plugin -> plugin.call("onDestroy", Context.emptyArgs));
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
