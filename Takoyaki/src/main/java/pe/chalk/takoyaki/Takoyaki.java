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
import org.mozilla.javascript.RhinoException;
import pe.chalk.takoyaki.logger.Logger;
import pe.chalk.takoyaki.logger.LoggerStream;
import pe.chalk.takoyaki.plugin.JavaScriptPlugin;
import pe.chalk.takoyaki.plugin.Plugin;
import pe.chalk.takoyaki.utils.Prefix;
import pe.chalk.takoyaki.utils.TextFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
    public static final String VERSION = "2.2";

    private static Takoyaki instance = null;
    private static List<String> DEFAULT_CONFIG = Arrays.asList(
            "{",
            "  \"naverAccount\": {",
            "    \"username\": \"\",",
            "    \"password\": \"\"",
            "  },",
            "  \"options\": {",
            "    \"timeout\": 5000,",
            "    \"excludedPlugins\": [\"debug.js\"]",
            "  },",
            "  \"targets\": [",
            "    {",
            "      \"address\": \"minecraftpe\",",
            "      \"prefix\": \"M.K\",",
            "      \"interval\": 2500,",
            "      \"filters\": [\"article\", \"commentary\", \"visitation\"]",
            "    },",
            "    {",
            "      \"address\": \"ourmcspace\",",
            "      \"prefix\": \"PMC\",",
            "      \"interval\": 5000,",
            "      \"filters\": [\"article\", \"commentary\", \"visitation\"]",
            "    }",
            "  ]",
            "}");

    private List<String> excludedPlugins;

    private List<Target> targets;
    private List<Plugin> plugins;
    private Logger logger;
    private Staff staff;

    private boolean isAlive = false;

    public static Takoyaki getInstance(){
        return instance;
    }

    public Takoyaki() throws IOException {
        if(Takoyaki.instance != null) Takoyaki.instance.stop("새로운 인스턴스가 생성되었습니다");
        Takoyaki.instance = this;

        this.init();
    }

    private void init() throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(Takoyaki.this::shutdown));

        this.logger = new Logger();
        this.logger.addStream(new LoggerStream(TextFormat.Type.ANSI, System.out));
        this.logger.addStream(new LoggerStream(TextFormat.Type.NONE, new PrintStream(new FileOutputStream("Takoyaki.log", true), true, "UTF-8")));

        this.getLogger().info("타코야키를 시작합니다: " + Takoyaki.VERSION);

        try{
            final Path propertiesPath = Paths.get("Takoyaki.json");
            if(!Files.exists(propertiesPath)){
                this.getLogger().info("프로퍼티를 생성합니다: " + propertiesPath.toString());
                Files.write(propertiesPath, Takoyaki.DEFAULT_CONFIG, StandardCharsets.UTF_8);
            }

            this.getLogger().info("프로퍼티를 불러옵니다: " + propertiesPath);

            final JSONObject properties = new JSONObject(Files.lines(propertiesPath, StandardCharsets.UTF_8).collect(Collectors.joining()));
            //Files.write(propertiesPath, properties.toString(2).getBytes("UTF-8"));

            this.staff = new Staff(properties.getJSONObject("naverAccount"), properties.getJSONObject("options").getInt("timeout"));

            this.excludedPlugins = Takoyaki.<String>buildStream(properties.getJSONObject("options").getJSONArray("excludedPlugins")).collect(Collectors.toList());
            this.targets         = Takoyaki.<JSONObject>buildStream(properties.getJSONArray("targets")).map(Target::new).collect(Collectors.toList());

            this.loadPlugins();
        }catch(Exception e){
            this.getLogger().error(e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
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
        Files.list(pluginsPath).filter(path -> path.getFileName().toString().endsWith(".js") && !this.excludedPlugins.contains(path.getFileName().toString())).map(Path::toFile).forEach(pluginFile -> {
            try{
                JavaScriptPlugin plugin = new JavaScriptPlugin(pluginFile);
                plugin.onLoad();

                this.plugins.add(plugin);
                this.logger.info("플러그인을 불러옵니다: " + plugin.getName() + (plugin.getVersion() != null ? " v" + plugin.getVersion() : ""));
            }catch(IOException | RhinoException e){
                this.getLogger().error(e.getClass().getName() + ": " + e.getMessage());
            }
        });
    }

    public void start(){
        this.isAlive = true;

        this.getTargets().forEach(Target::start);
        this.getPlugins().forEach(Plugin::onStart);
    }

    public void shutdown(){
        this.stop("VM에 의한 종료");
    }

    @SuppressWarnings("unused")
    public void stop(){
        this.stop("알 수 없음");
    }

    public void stop(String reason){
        this.isAlive = false;

        if(this.getLogger() != null) this.getLogger().info("타코야키를 종료합니다: 사유: " + reason);

        if(this.getTargets() != null) this.getTargets().forEach(Thread::interrupt);
        if(this.getPlugins() != null) this.getPlugins().forEach(Plugin::onDestroy);
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

    public Staff getStaff(){
        return this.staff;
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
