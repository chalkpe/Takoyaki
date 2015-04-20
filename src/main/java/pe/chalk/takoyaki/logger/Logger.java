package pe.chalk.takoyaki.logger;

import pe.chalk.takoyaki.utils.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-14
 */
public abstract class Logger implements Loggable {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public static Map<Level, String> prefixMap = new HashMap<Level, String>(){{
        put(Level.DEBUG,    "DEBUG");
        put(Level.INFO,     "INFO");
        put(Level.WARNING,  "WARNING");
        put(Level.CRITICAL, "CRITICAL");
        put(Level.ERROR,    "ERROR");
    }};

    public static Map<Level, String> colorMap = new HashMap<Level, String>(){{
        put(Level.DEBUG,    ChatColor.RESET.toString() + ChatColor.DARK_GRAY + ChatColor.ITALIC);
        put(Level.INFO,     ChatColor.RESET.toString() + ChatColor.WHITE);
        put(Level.WARNING,  ChatColor.RESET.toString() + ChatColor.YELLOW);
        put(Level.CRITICAL, ChatColor.RESET.toString() + ChatColor.LIGHT_PURPLE);
        put(Level.ERROR,    ChatColor.RESET.toString() + ChatColor.RED);
    }};

    protected abstract void log(String message);

    protected void print(Level level, String message, Date date){
        log(String.format("%s[%s] [%s] %s", colorMap.get(level), Logger.SIMPLE_DATE_FORMAT.format(date == null ? new Date() : date), prefixMap.get(level), message));
    }

    @Override
    public void debug(String message){
        print(Level.DEBUG, message, null);
    }

    @Override
    public void info(String message){
        print(Level.INFO, message, null);
    }

    @Override
    public void warning(String message){
        print(Level.WARNING, message, null);
    }

    @Override
    public void critical(String message){
        print(Level.CRITICAL, message, null);
    }

    @Override
    public void error(String message){
        print(Level.ERROR, message, null);
    }

    public PrefixedLogger getPrefixed(Prefix prefix){
        return new PrefixedLogger(this, prefix);
    }
}