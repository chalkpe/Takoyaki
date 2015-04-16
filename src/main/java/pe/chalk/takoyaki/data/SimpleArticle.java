package pe.chalk.takoyaki.data;

import pe.chalk.takoyaki.logger.ChatColor;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-12
 */
public class SimpleArticle extends Data {
    private int id;
    private String title;
    private int commentCount;

    public SimpleArticle(int id, String title, int commentCount){
        this.id = id;
        this.title = title;
        this.commentCount = commentCount;
    }

    public int getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }

    public int getCommentCount(){
        return this.commentCount;
    }

    @Override
    public boolean equals(Object another){
        return another instanceof SimpleArticle && this.getId() == ((SimpleArticle) another).getId();
    }

    @Override
    public String toString(){
        return ChatColor.GREEN + "[" + this.getId() + "] " + ChatColor.RESET + this.getTitle() + ChatColor.DARK_YELLOW + " [" + this.getCommentCount() + "]" + ChatColor.RESET;
    }
}
