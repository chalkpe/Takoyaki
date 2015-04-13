package pe.chalk.takoyaki.data;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-12
 */
public class SimpleArticle extends Data implements Comparable<Article> {
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
    public int compareTo(Article another){
        return this.getId() - another.getId();
    }

    @Override
    public String toString(){
        return "[" + this.getId() + "] " + this.getTitle();
    }
}
