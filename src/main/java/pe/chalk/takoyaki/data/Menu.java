package pe.chalk.takoyaki.data;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-16
 */
public class Menu extends Data {
    private final int id;
    private final String name;

    public Menu(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public String toString(){
        return "[" + this.getName() + "]";
    }
}