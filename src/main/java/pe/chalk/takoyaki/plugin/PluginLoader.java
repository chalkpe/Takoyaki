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

package pe.chalk.takoyaki.plugin;

import org.mozilla.javascript.RhinoException;
import pe.chalk.takoyaki.Takoyaki;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-11-05
 */
public class PluginLoader {
    public static final Function<List<String>, Predicate<Path>> PLUGIN_FILTER = excludedPlugins -> path -> {
        String filename = path.getFileName().toString().toLowerCase();
        return !excludedPlugins.contains(filename) && (filename.endsWith(".js") || filename.endsWith(".jar"));
    };

    public List<PluginBase> load(List<Path> paths){
        return paths.parallelStream().map(this::load).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public PluginBase load(Path path){
        return this.load(path.toFile());
    }

    public PluginBase load(File file){
        try{
            PluginBase plugin;

            if(file.getName().endsWith(".jar")) plugin = this.loadJar(file);
            else if(file.getName().endsWith(".js")) plugin = new JavaScriptPlugin(file);
            else return null;

            Takoyaki.getInstance().getLogger().info("플러그인을 불러옵니다: " + plugin.getName() + (plugin.getVersion() != null ? " v" + plugin.getVersion() : ""));
            plugin.onLoad();

            return plugin;
        }catch(IOException | RhinoException | ReflectiveOperationException e){
            Takoyaki.getInstance().getLogger().error(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }

    public PluginBase loadJar(File file) throws IOException, ReflectiveOperationException {
        JarFile jarFile = new JarFile(file);

        String mainClassName = jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
        List<URL> urls = this.getURLsFromJarFile(file, jarFile);

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if(!urls.isEmpty()) loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());

        Class<?> mainClass = Class.forName(mainClassName, true, loader).asSubclass(PluginBase.class);
        return (PluginBase) mainClass.newInstance();
    }

    public List<URL> getURLsFromJarFile(File file) throws IOException {
        return this.getURLsFromJarFile(file, new JarFile(file));
    }

    public List<URL> getURLsFromJarFile(File file, JarFile jarFile) throws IOException {
        List<URL> urls = new ArrayList<>();
        urls.add(file.toURI().toURL());

        Manifest manifest = jarFile.getManifest();
        if(manifest != null){
            String classpath = manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH);

            if(classpath != null){
                urls.addAll(Arrays.stream(classpath.split("\\s+")).parallel().flatMap(path -> {
                    try{
                        return this.getURLsFromJarFile(new File(file.getParentFile(), path)).stream();
                    }catch(IOException e){
                        e.printStackTrace();
                    }

                    return Stream.empty();
                }).collect(Collectors.toList()));
            }
        }

        return urls;
    }
}
