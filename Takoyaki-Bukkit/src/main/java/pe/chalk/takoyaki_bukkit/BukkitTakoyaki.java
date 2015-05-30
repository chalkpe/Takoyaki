package pe.chalk.takoyaki_bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import pe.chalk.takoyaki.Takoyaki;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-05-19
 */
public class BukkitTakoyaki extends JavaPlugin {
    private static List<String> DEFAULT_PLUGIN = Arrays.asList(
            "/*",
            " * Copyright 2014-2015 ChalkPE",
            " *",
            " * Licensed under the Apache License, Version 2.0 (the \"License\");",
            " * you may not use this file except in compliance with the License.",
            " * You may obtain a copy of the License at",
            " *",
            " *     http://www.apache.org/licenses/LICENSE-2.0",
            " *",
            " * Unless required by applicable law or agreed to in writing, software",
            " * distributed under the License is distributed on an \"AS IS\" BASIS,",
            " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.",
            " * See the License for the specific language governing permissions and",
            " * limitations under the License.",
            " */",
            "",
            "/**",
            " * @author ChalkPE <amato0617@gmail.com>",
            " * @since 2015-05-30",
            " */",
            "const VERSION = \"1.0\";",
            "",
            "function onDataAdded(filter, data){",
            "    Packages.org.bukkit.Bukkit.getServer().getPluginManager().callEvent(new Packages.pe.chalk.takoyaki_bukkit.TakoyakiEvent(data));",
            "}");

    @Override
    public void onEnable(){
        try{
            Path defaultPluginPath = Paths.get("plugins/BukkitTakoyaki.js");
            if(!Files.exists(defaultPluginPath)){
                Files.write(defaultPluginPath, DEFAULT_PLUGIN, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            }

            Takoyaki takoyaki = new Takoyaki();
            takoyaki.getLogger().removeStream(System.out);
            takoyaki.getLogger().addTransmitter((level, message) -> BukkitTakoyaki.this.getServer().broadcastMessage(level.getFormats() + "[" + level.getPrefix() + "] " + message));

            takoyaki.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}