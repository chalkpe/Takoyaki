package pe.chalk.takoyaki.event;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-11-09
 */
public interface EventHandler {
    default boolean checkEvent(Event event){
        return true;
    }

    void handleEvent(Event event);
}
