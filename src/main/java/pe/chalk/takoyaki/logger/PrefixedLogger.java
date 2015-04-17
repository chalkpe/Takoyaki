package pe.chalk.takoyaki.logger;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-17
 */
public class PrefixedLogger implements Loggable {
    private Loggable parent;
    private Prefix prefix;

    public PrefixedLogger(Loggable parent, Prefix prefix){
        this.parent = parent;
        this.prefix = prefix;
    }

    @Override
    public void debug(String message){
        this.parent.debug(String.format("[%s] %s", prefix.getPrefix(), message));
    }

    @Override
    public void info(String message){
        this.parent.info(String.format("[%s] %s", prefix.getPrefix(), message));
    }

    @Override
    public void warning(String message){
        this.parent.warning(String.format("[%s] %s", prefix.getPrefix(), message));
    }

    @Override
    public void critical(String message){
        this.parent.critical(String.format("[%s] %s", prefix.getPrefix(), message));
    }

    @Override
    public void error(String message){
        this.parent.error(String.format("[%s] %s", prefix.getPrefix(), message));
    }

    public PrefixedLogger sub(Prefix prefix){
        return new PrefixedLogger(this, prefix);
    }
}