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
import pe.chalk.takoyaki.logger.Logger;
import pe.chalk.takoyaki.logger.LoggerStream;
import pe.chalk.takoyaki.plugin.Plugin;
import pe.chalk.takoyaki.plugin.PluginLoader;
import pe.chalk.takoyaki.target.Target;
import pe.chalk.takoyaki.utils.Prefix;
import pe.chalk.takoyaki.utils.TextFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-07
 */
public class Takoyaki implements Prefix {
    public static final String VERSION = "2.2.2-SNAPSHOT";

    private static Takoyaki instance = null;
    private static final List<String> DEFAULT_CONFIG = Arrays.asList(
            "{",
            "  \"options\": {\"excludedPlugins\": [\"debug.js\"]},",
            "  \"targets\": [",
            "    {",
            "      \"type\": \"naver.cafe\",",
            "      \"address\": \"minecraftpe\",",
            "      \"prefix\": \"M.K\",",
            "      \"interval\": 2500,",
            "      \"timeout\": 5000,",
            "      \"filters\": [\"article\", \"commentary\", \"visitation\"],",
            "      \"naverAccount\": {\"username\": \"\", \"password\": \"\"}",
            "    },",
            "    {",
            "      \"type\": \"naver.cafe\",",
            "      \"address\": \"ourmcspace\",",
            "      \"prefix\": \"PMC\",",
            "      \"interval\": 5000,",
            "      \"timeout\": 5000,",
            "      \"filters\": [\"article\", \"commentary\", \"visitation\"],",
            "      \"naverAccount\": {\"username\": \"\", \"password\": \"\"}",
            "    },",
            "    {",
            "      \"type\": \"namu.wiki\",",
            "      \"prefix\": \"N.W\",",
            "      \"interval\": 2500,",
            "      \"timeout\": 5000",
            "    }",
            "  ]",
            "}");

    private List<String> excludedPlugins;
    private List<Target> targets;
    private List<Plugin> plugins;
    private Logger logger;

    private boolean isAlive = false;
    
    public static Takoyaki getInstance(){
        if(Takoyaki.instance == null){
            try{
                new Takoyaki();
            }catch(IOException | JSONException e){
                throw new RuntimeException(e);
            }
        }

        return Takoyaki.instance;
    }

    private Takoyaki() throws IOException, JSONException {
        Takoyaki.instance = this;

        this.init();
    }

    private void init() throws IOException, JSONException {
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

            this.excludedPlugins = Takoyaki.<String>buildStream(properties.getJSONObject("options").getJSONArray("excludedPlugins")).collect(Collectors.toList());
            this.targets         = Takoyaki.<JSONObject>buildStream(properties.getJSONArray("targets")).map(Target::create).collect(Collectors.toList());

            this.loadPlugins();
        }catch(Exception e){
            this.getLogger().error(e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }
    }

    public static <T> Stream<T> buildStream(JSONArray array){
        return Takoyaki.buildStream(array, true);
    }
    
    public static <T> Stream<T> buildStream(JSONArray array, boolean parallel){
        Stream.Builder<T> builder = Stream.builder();

        if(array != null){
            for(int i = 0; i < array.length(); i++){
                builder.add((T) array.get(i));
            }
        }

        Stream<T> stream = builder.build();
        return parallel ? stream.parallel() : stream;
    }

    private void loadPlugins() throws IOException {
        Path pluginsPath = Paths.get("plugins");
        if(!Files.exists(pluginsPath)){
            this.getLogger().info("플러그인을 불러옵니다: plugins 디렉토리 생성 중...");

            Files.createDirectories(pluginsPath);
        }

        Predicate<Path> filter = path -> !this.excludedPlugins.contains(path.getFileName().toString());
        filter = filter.and(path -> {
            String filename = path.getFileName().toString();
            return filename.endsWith(".js") || filename.endsWith(".jar");
        });

        this.plugins = Files.list(pluginsPath).parallel().filter(filter).map(new PluginLoader()::load).filter(Objects::nonNull).collect(Collectors.toList());
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

    public List<Target> getTargets(){
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

    public static void main(String[] args){
        try{
            Takoyaki.getInstance().start();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}
