package pe.chalk.takoyaki.logger;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-14
 */
public abstract class Logger {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    protected abstract void log(String message);

    protected void print(List<ChatColor> colors, Date date, String prefix, String message){
        log(String.format("%s[%s] [%s] %s", String.join("", colors.stream().map(ChatColor::toString).collect(Collectors.toList())), Logger.SIMPLE_DATE_FORMAT.format(date), prefix, message));
    }

    public void debug(String message){
        print(Arrays.asList(ChatColor.RESET, ChatColor.DARK_GRAY, ChatColor.ITALIC), new Date(), "DEBUG", message);
    }
    public void debug(String prefix, String message){
        debug(prefix == null || prefix.length() == 0 ? message : String.format("[%s] %s", prefix, message));
    }
    public void debug(Prefix prefix, String message){
        debug(prefix == null ? message : prefix.getPrefix(), message);
    }

    public void info(String message){
        print(Arrays.asList(ChatColor.RESET, ChatColor.WHITE), new Date(), "INFO", message);
    }
    public void info(String prefix, String message){
        info(prefix == null || prefix.length() == 0 ? message : String.format("[%s] %s", prefix, message));
    }
    public void info(Prefix prefix, String message){
        info(prefix == null ? message : prefix.getPrefix(), message);
    }

    public void warning(String message){
        print(Arrays.asList(ChatColor.RESET, ChatColor.YELLOW), new Date(), "WARNING", message);
    }
    public void warning(String prefix, String message){
        warning(prefix == null || prefix.length() == 0 ? message : String.format("[%s] %s", prefix, message));
    }
    public void warning(Prefix prefix, String message){
        warning(prefix == null ? message : prefix.getPrefix(), message);
    }

    public void error(String message){
        print(Arrays.asList(ChatColor.RESET, ChatColor.RED, ChatColor.UNDERLINE), new Date(), "ERROR", message);
    }
    public void error(String prefix, String message){
        error(prefix == null || prefix.length() == 0 ? message : String.format("[%s] %s", prefix, message));
    }
    public void error(Prefix prefix, String message){
        error(prefix == null ? message : prefix.getPrefix(), message);
    }
}