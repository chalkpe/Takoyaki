package pe.chalk.takoyaki;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import pe.chalk.takoyaki.logger.Prefix;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-19
 */
public class Plugin implements Prefix {
    private String name;
    private Scriptable scriptable;

    public Plugin(String name, Scriptable scriptable){
        this.name = name;
        this.scriptable = scriptable;
    }

    public String getName(){
        return this.name;
    }

    public Scriptable getScriptable(){
        return this.scriptable;
    }

    public Object call(String functionName, Object[] args){
        Context context = Context.enter();
        try{
            Object object = this.getScriptable().get(functionName, this.getScriptable());
            if(object != null && object instanceof Function){
                return ((Function) object).call(context, scriptable, scriptable, args);
            }
            return null;
        }finally{
            Context.exit();
        }
    }

    @Override
    public String toString(){
        return this.getName();
    }

    @Override
    public String getPrefix(){
        return "#" + this.getName();
    }
}