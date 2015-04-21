package pe.chalk.takoyaki.logger;

import pe.chalk.takoyaki.utils.TextFormat;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-15
 */
public class ConsoleLogger extends Logger {
    @Override
    protected void log(String message){
        System.out.println(TextFormat.replaceTo(TextFormat.Type.ANSI, message));
    }
}