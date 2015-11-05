package pe.chalk.takoyaki.plugin;

import org.mozilla.javascript.RhinoException;
import pe.chalk.takoyaki.Takoyaki;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.jar.JarFile;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-11-05
 */
public class PluginLoader {
    public PluginBase load(Path path){
        return this.load(path.toFile());
    }

    public PluginBase load(File file){
        try{
            PluginBase plugin;

            if(file.getName().endsWith(".jar"))     plugin = this.loadJar(file);
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

    //TODO: Load jar's classpath
    public PluginBase loadJar(File file) throws IOException, ReflectiveOperationException {
        JarFile jarFile = new JarFile(file);
        String mainClassName = jarFile.getManifest().getMainAttributes().getValue("Main-Class");

        URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader());
        //addURL(file.toURI().toURL());

        //Class<?> mainClass = loader.loadClass(mainClassName);
        //Class<?> mainClass = Class.forName(mainClassName, true, ClassLoader.getSystemClassLoader());
        Class<?> mainClass = Class.forName(mainClassName, true, loader);

        System.out.println(mainClass);
        if(!mainClass.isAssignableFrom(PluginBase.class)){
            throw new ClassCastException("Main class must be assignable to pe.chalk.takoyaki.plugin.PluginBase");
        }

        return (PluginBase) mainClass.newInstance();
    }

    public static void addURL(URL url) throws IOException {
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        for(URL u : classLoader.getURLs()){
            if(u.toString().equalsIgnoreCase(url.toString())) return;
        }

        try{
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, url);
        }catch(Throwable t){
            t.printStackTrace();
        }
    }
}
