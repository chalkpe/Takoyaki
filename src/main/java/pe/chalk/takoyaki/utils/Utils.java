package pe.chalk.takoyaki.utils;

import org.json.JSONArray;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-11-08
 */
public class Utils {
    public static <T> Stream<T> buildStream(Class<T> type, JSONArray array){
        return Utils.buildStream(type, array, true);
    }

    public static <T> Stream<T> buildStream(Class<T> type, JSONArray array, boolean parallel){
        Stream.Builder<T> builder = Stream.builder();

        if(array != null) for(int i = 0; i < array.length(); i++){
            Object element = array.get(i);
            if(type.isInstance(element)) builder.add(type.cast(element));
        }

        Stream<T> stream = builder.build();
        return parallel ? stream.parallel() : stream;
    }

    public interface UnsafeConsumer<T> extends Consumer<T> {
        void acceptUnsafely(T t) throws Exception;

        default void accept(T t){
            try{
                acceptUnsafely(t);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static <T> Consumer<T> unsafe(final UnsafeConsumer<T> consumer){
        return consumer;
    }
}
