package pe.chalk.takoyaki.data;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-12
 */
public class SimpleArticle extends Data {
    private int id;
    private String title;

    public SimpleArticle(int id, String title){
        this.id = id;
        this.title = title;
    }

    public int getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }

    @Override
    public boolean equals(Object another){
        return another instanceof SimpleArticle && this.getId() == ((SimpleArticle) another).getId();
    }

    @Override
    public String toString(){
        return "[" + this.getId() + "] " + this.getTitle();
    }
}
