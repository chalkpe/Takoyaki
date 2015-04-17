package pe.chalk.takoyaki.logger;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-17
 */
public interface Loggable {
    enum Level {
        DEBUG, INFO, WARNING, CRITICAL, ERROR
    }

    void debug(String message);
    void info(String message);
    void warning(String message);
    void critical(String message);
    void error(String message);
}