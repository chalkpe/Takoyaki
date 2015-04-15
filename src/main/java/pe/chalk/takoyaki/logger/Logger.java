package pe.chalk.takoyaki.logger;

import pe.chalk.takoyaki.data.Prefix;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-14
 */
public abstract class Logger {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    protected abstract void log(String message);

    protected void print(ChatColor mainColor, Date date, String prefix, String message){
        log(String.format("%s[%s] [%s] %s", mainColor.toString(), Logger.SIMPLE_DATE_FORMAT.format(date), prefix, message));
    }

    public void debug(String message){
        print(ChatColor.DARK_GRAY, new Date(), "DEBUG", message);
    }
    public void debug(String prefix, String message){
        debug(String.format("[%s] %s", prefix, message));
    }
    public void debug(Prefix prefix, String message){
        debug(prefix.getPrefix(), message);
    }

    public void info(String message){
        print(ChatColor.RESET, new Date(), "INFO", message);
    }
    public void info(String prefix, String message){
        info(String.format("[%s] %s", prefix, message));
    }
    public void info(Prefix prefix, String message){
        info(prefix.getPrefix(), message);
    }

    public void warning(String message){
        print(ChatColor.YELLOW, new Date(), "WARNING", message);
    }
    public void warning(String prefix, String message){
        warning(String.format("[%s] %s", prefix, message));
    }
    public void warning(Prefix prefix, String message){
        warning(prefix.getPrefix(), message);
    }

    public void error(String message){
        print(ChatColor.RED, new Date(), "ERROR", message);
    }
    public void error(String prefix, String message){
        error(String.format("[%s] %s", prefix, message));
    }
    public void error(Prefix prefix, String message){
        error(prefix.getPrefix(), message);
    }
}