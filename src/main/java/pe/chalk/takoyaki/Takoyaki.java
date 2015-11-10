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

import org.json.JSONException;
import org.json.JSONObject;
import pe.chalk.takoyaki.logger.Logger;
import pe.chalk.takoyaki.logger.LoggerStream;
import pe.chalk.takoyaki.plugin.Plugin;
import pe.chalk.takoyaki.plugin.PluginLoader;
import pe.chalk.takoyaki.target.NamuWiki;
import pe.chalk.takoyaki.target.NaverCafe;
import pe.chalk.takoyaki.target.Target;
import pe.chalk.takoyaki.utils.Prefix;
import pe.chalk.takoyaki.utils.TextFormat;
import pe.chalk.takoyaki.utils.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
            "      \"prefix\": \"§9M.K§{LEVEL_FORMAT}\",",
            "      \"interval\": 2500,",
            "      \"timeout\": 5000,",
            "      \"filters\": [\"article\", \"commentary\", \"visitation\"],",
            "      \"naverAccount\": {\"username\": \"\", \"password\": \"\"}",
            "    },",
            "    {",
            "      \"type\": \"naver.cafe\",",
            "      \"address\": \"ourmcspace\",",
            "      \"prefix\": \"§aPMC§{LEVEL_FORMAT}\",",
            "      \"interval\": 5000,",
            "      \"timeout\": 5000,",
            "      \"filters\": [\"article\", \"commentary\", \"visitation\"],",
            "      \"naverAccount\": {\"username\": \"\", \"password\": \"\"}",
            "    },",
            "    {",
            "      \"type\": \"wiki.namu\",",
            "      \"prefix\": \"N.W\",",
            "      \"interval\": 2500,",
            "      \"timeout\": 5000",
            "    }",
            "  ]",
            "}");

    private Logger logger;

    private List<Target<?>> targets = new ArrayList<>();
    private Map<String, Class<? extends Target<?>>> targetClasses = new HashMap<>();
    private List<Plugin> plugins = new ArrayList<>();

    private boolean isAlive = false;

    public static Takoyaki getInstance(){
        if(Takoyaki.instance == null) new Takoyaki();
        return Takoyaki.instance;
    }

    private Takoyaki(){
        Takoyaki.instance = this;
    }

    public synchronized Takoyaki init() throws IOException, JSONException, ReflectiveOperationException {
        if(this.getLogger() != null) return this;

        this.addTargetClasses(NaverCafe.class, NamuWiki.class);
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        this.logger = new Logger();
        this.getLogger().addStream(new LoggerStream(TextFormat.Type.ANSI, System.out));
        this.getLogger().addStream(new LoggerStream(TextFormat.Type.NONE, new PrintStream(new FileOutputStream("Takoyaki.log", true), true, "UTF-8")));
        this.getLogger().info("타코야키를 시작합니다: " + Takoyaki.VERSION);

        try{
            final Path propertiesPath = Paths.get("Takoyaki.json");
            if(!Files.exists(propertiesPath)){
                this.getLogger().info("프로퍼티를 생성합니다: " + propertiesPath.toString());
                Files.write(propertiesPath, Takoyaki.DEFAULT_CONFIG, StandardCharsets.UTF_8);
            }

            this.getLogger().info("프로퍼티를 불러옵니다: " + propertiesPath);
            final JSONObject properties = new JSONObject(Files.lines(propertiesPath, StandardCharsets.UTF_8).collect(Collectors.joining()));

            Utils.buildStream(JSONObject.class, properties.getJSONArray("targets")).map(Target::create).forEach(this::addTarget);

            final Path pluginsPath = Paths.get("TakoyakiPlugins");
            if(!Files.exists(pluginsPath)) Files.createDirectories(pluginsPath);

            List<String> excludedPlugins = Utils.buildStream(String.class, properties.getJSONObject("options").getJSONArray("excludedPlugins")).collect(Collectors.toList());
            new PluginLoader().load(Files.list(pluginsPath).filter(PluginLoader.PLUGIN_FILTER.apply(excludedPlugins)).collect(Collectors.toList())).forEach(this::addPlugin);
        }catch(Exception e){
            this.getLogger().error(e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }

        return this;
    }

    public Logger getLogger(){
        return this.logger;
    }

    public List<Target<?>> getTargets(){
        return this.targets;
    }

    public void addTarget(Target<?> target){
        Objects.requireNonNull(target);

        if(this.isAlive()) target.start();
        this.getTargets().add(target);
    }

    public void removeTarget(Target<?> target){
        Objects.requireNonNull(target);

        if(this.isAlive()) target.interrupt();
        this.getTargets().remove(target);
    }

    public Map<String, Class<? extends Target<?>>> getTargetClasses(){
        return this.targetClasses;
    }

    @SafeVarargs
    public final void addTargetClasses(Class<? extends Target<?>>... targetClasses) throws ReflectiveOperationException {
        Objects.requireNonNull(targetClasses);

        Stream.of(targetClasses).forEach(Utils.unsafe(this::addTargetClass));
    }

    public void addTargetClass(Class<? extends Target<?>> targetClass) throws ReflectiveOperationException {
        Objects.requireNonNull(targetClass);

    	this.getTargetClasses().put(targetClass.getMethod("getType").invoke(null).toString(), targetClass);
    }

    public void removeTargetClass(Class<? extends Target<?>> targetClass) throws ReflectiveOperationException {
        Objects.requireNonNull(targetClass);

    	this.remoteTargetClass(targetClass.getMethod("getType").invoke(null).toString());
    }

    public void remoteTargetClass(String type){
        Objects.requireNonNull(type);

        this.getTargetClasses().remove(type);
    }

    public List<Plugin> getPlugins(){
        return this.plugins;
    }

    public void addPlugin(Plugin plugin){
        Objects.requireNonNull(plugin);

        this.getPlugins().add(plugin);
        plugin.onEnable();
    }

    public void removePlugin(Plugin plugin){
        Objects.requireNonNull(plugin);

        plugin.onDisable();
        this.getPlugins().remove(plugin);
    }

    public boolean isAlive(){
        return this.isAlive;
    }

    public synchronized void start(){
        if(this.isAlive()) return;
        this.isAlive = true;

        this.getTargets().parallelStream().forEach(Target::start);
        this.getPlugins().parallelStream().forEach(Plugin::onStart);
    }

    public synchronized void shutdown(){
        this.stop("VM에 의한 종료");
    }

    public synchronized void stop(){
        this.stop("알 수 없음");
    }

    public synchronized void stop(String reason){
        if(!this.isAlive()) return; this.isAlive = false;

        if(this.getLogger() != null) this.getLogger().info("타코야키를 종료합니다: 사유: " + reason);
        if(this.getTargets() != null) this.getTargets().parallelStream().forEach(Target::interrupt);
        if(this.getPlugins() != null) this.getPlugins().parallelStream().forEach(Plugin::onStop);
        if(this.getLogger() != null) Utils.unsafe(Logger::close).accept(this.getLogger());
    }

    @Override
    public String getPrefix(){
        return "타코야키";
    }

    public static void main(String[] args){
        try{
        	Takoyaki.getInstance().init().start();
        }catch(Exception e){
            Takoyaki.getInstance().stop();

            e.printStackTrace();
            System.exit(1);
        }
    }
}
