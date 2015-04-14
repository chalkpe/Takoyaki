package pe.chalk.takoyaki.logger;

import java.text.SimpleDateFormat;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-14
 */
public interface Logger {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    
    public void debug(String message);
    public void info(String message);
    public void warning(String message);
    public void error(String message);
}