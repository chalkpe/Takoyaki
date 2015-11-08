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

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @author 파차리로디드 <fcreloaded@outlook.com>
 * @since 2015-11-08
 */
public class Main implements Prefix {
	
	private Logger logger;

    static final List<String> DEFAULT_CONFIG = Arrays.asList(
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
    
	public static void main(String[] args) {
		Main main = new Main();
		Takoyaki instance = Takoyaki.getInstance();
        try {
        	main.init(instance);
        	instance.start();
        } catch(Exception e) {
            instance.stop();

            e.printStackTrace();
            System.exit(1);
        }
	}
	
	private boolean init(Takoyaki instance) throws Exception {
        if(instance.getLogger() != null) return false;

        Runtime.getRuntime().addShutdownHook(new Thread(instance::shutdown));

        Logger logger = new Logger();
        logger.addStream(new LoggerStream(TextFormat.Type.ANSI, System.out));
        logger.addStream(new LoggerStream(TextFormat.Type.NONE, new PrintStream(new FileOutputStream("Takoyaki.log", true), true, "UTF-8")));
        instance.logger = logger;
        instance.getLogger().addStream(new LoggerStream(TextFormat.Type.ANSI, System.out));
        instance.getLogger().addStream(new LoggerStream(TextFormat.Type.NONE, new PrintStream(new FileOutputStream("Takoyaki.log", true), true, "UTF-8")));
        instance.getLogger().info("타코야키를 시작합니다: " + Takoyaki.VERSION);

        try{
            final Path propertiesPath = Paths.get("Takoyaki.json");
            if(!Files.exists(propertiesPath)){
            	this.getLogger().info("프로퍼티를 생성합니다: " + propertiesPath.toString());
                Files.write(propertiesPath, Main.DEFAULT_CONFIG, StandardCharsets.UTF_8);
            }

            this.getLogger().info("프로퍼티를 불러옵니다: " + propertiesPath);

            final JSONObject properties = new JSONObject(Files.lines(propertiesPath, StandardCharsets.UTF_8).collect(Collectors.joining()));
            //Files.write(propertiesPath, properties.toString(2).getBytes("UTF-8"));

            instance.addTargetClass(NaverCafe.class);
            instance.addTargetClass(NamuWiki.class);

            List<String> excludedPlugins = Takoyaki.buildStream(String.class, properties.getJSONObject("options").getJSONArray("excludedPlugins")).collect(Collectors.toList());

            for (Plugin plugin : this.loadPlugins(excludedPlugins)) {
            	instance.loadPlugin(plugin);
            }
            
            for (Target<?> target : this.loadTargets(properties)) {
            	instance.addTarget(target);
            }
        }catch(Exception e){
        	this.getLogger().error(e.getClass().getName() + ": " + e.getMessage());
            throw e;
        }

        return true;
	}

    private List<Plugin> loadPlugins(List<String> excludedPlugins) throws IOException {
        Path pluginsPath = Paths.get("plugins");
        if(!Files.exists(pluginsPath)){
            this.getLogger().info("플러그인을 불러옵니다: plugins 디렉토리 생성 중...");

            Files.createDirectories(pluginsPath);
        }

        Predicate<Path> filter = path -> !excludedPlugins.contains(path.getFileName().toString());
        filter = filter.and(path -> {
            String filename = path.getFileName().toString();
            return filename.endsWith(".js") || filename.endsWith(".jar");
        });

        return Files.list(pluginsPath).parallel().filter(filter).map(new PluginLoader()::load).filter(Objects::nonNull).collect(Collectors.toList());
    }
    
    private List<Target<?>> loadTargets(JSONObject properties) {
    	return Takoyaki.buildStream(JSONObject.class, properties.getJSONArray("targets")).map(Target::create).collect(Collectors.toList());
    }
    
    
	private Main() {
		this.logger = new Logger();
	}
	
	@Override
	public String getPrefix() {
		return "타코야키";
	}
	
	public Logger getLogger() {
		return this.logger;
	}

}
