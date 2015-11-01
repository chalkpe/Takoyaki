package pe.chalk.takoyaki.target;

import org.json.JSONObject;
import pe.chalk.takoyaki.Takoyaki;
import pe.chalk.takoyaki.filter.Filter;
import pe.chalk.takoyaki.logger.PrefixedLogger;
import pe.chalk.takoyaki.model.Data;
import pe.chalk.takoyaki.utils.Prefix;
import pe.chalk.takoyaki.utils.TextFormat;

import java.util.List;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-11-01
 */
public abstract class Target<D> extends Thread implements Prefix {
    private String prefix;
    private PrefixedLogger logger;

    private long interval;
    private List<Filter<D, ?>> filters;

    public Target(String prefix, long interval, List<Filter<D, ?>> filters){
        this.prefix = prefix;
        this.logger = new PrefixedLogger(Takoyaki.getInstance().getLogger(), this);

        this.interval = interval;
        this.filters = filters;
    }

    public static Target create(JSONObject properties){
        switch(properties.getString("type").toLowerCase()){
            case "naver.cafe":
                return new NaverCafe(properties);

            default:
                throw new IllegalArgumentException("Unknown type");
        }
    }

    @Override
    public final String getPrefix(){
        return this.prefix;
    }

    public PrefixedLogger getLogger(){
        return this.logger;
    }

    public long getInterval(){
        return this.interval;
    }

    public List<Filter<D, ?>> getFilters(){
        return this.filters;
    }

    @Override
    public void run(){
        this.getLogger().getParent().info("모니터링을 시작합니다: " + this);
        while(Takoyaki.getInstance().isAlive() && !this.isInterrupted()){
            try{
                Thread.sleep(this.getInterval());
                this.collect(this.getDocument());
            }catch(InterruptedException e){
                return;
            }catch(Exception e){
                this.getLogger().error(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    public abstract D getDocument() throws Exception;

    public void collect(D document){
        try{
            this.getFilters().forEach(filter -> {
                List<? extends Data> list = filter.getFreshData(document);
                if(list.isEmpty()) return;

                list.forEach(data -> filter.getLogger().info(data.toString()));
                Takoyaki.getInstance().getPlugins().forEach(plugin -> plugin.onDataAdded(list, filter));
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
