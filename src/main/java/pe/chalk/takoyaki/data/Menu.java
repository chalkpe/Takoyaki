package pe.chalk.takoyaki.data;

import pe.chalk.takoyaki.Target;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-16
 */
public class Menu extends Data {
    private final int id;
    private final String name;

    public Menu(Target target, int id, String name){
        super(target);

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
        return "[" + this.getId() + "#" + this.getName() + "]";
    }

    @Override
    public boolean equals(Object another){
        return another instanceof Menu && this.getId() == ((Menu) another).getId();
    }
}