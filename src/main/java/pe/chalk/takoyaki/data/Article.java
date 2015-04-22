/*
 * Copyright 2014-2015 ChalkPE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pe.chalk.takoyaki.data;

import pe.chalk.takoyaki.Target;
import pe.chalk.takoyaki.utils.TextFormat;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public class Article extends SimpleArticle {
    private static final long serialVersionUID = -1055443610669214310L;
    
    private final Member writer;

    private String head;
    private String uploadDate;
    private int menuId;

    private int viewCount;
    private int recommendedCount;

    private final boolean isQuestion;

    public Article(Target target, int id, String title, Member writer, String head, String uploadDate, int menuId, int viewCount, int commentCount, int recommendedCount, boolean isQuestion){
        super(target, id, title, commentCount);

        this.writer = writer;

        this.head = head;
        this.uploadDate = uploadDate;
        this.menuId = menuId;

        this.viewCount = viewCount;
        this.recommendedCount = recommendedCount;

        this.isQuestion = isQuestion;
    }

    public Member getWriter(){
        return writer;
    }

    public String getHead(){
        return head;
    }

    public String getUploadDate(){
        return uploadDate;
    }

    public int getMenuId(){
        return menuId;
    }

    public int getViewCount(){
        return viewCount;
    }

    public int getRecommendedCount(){
        return recommendedCount;
    }

    public boolean isQuestion(){
        return isQuestion;
    }

    public boolean hasHead(){
        return this.getHead() != null && this.getHead().length() > 0;
    }

    @Override
    public String toString(){
        return TextFormat.GREEN + "[" + this.getId() + "] " + TextFormat.RESET
                + TextFormat.LIGHT_PURPLE + "[" + this.getTarget().getMenu(this.getMenuId()).getName() + "] " + TextFormat.RESET
                + (this.isQuestion() ? TextFormat.LIGHT_PURPLE + "Q. " + TextFormat.RESET : "")
                + (this.hasHead() ? TextFormat.LIGHT_PURPLE + "[" + this.getHead() + "] " + TextFormat.RESET : "")
                + this.getTitle()
                + TextFormat.DARK_AQUA + " by " + this.getWriter().toString() + TextFormat.GOLD + " at " + this.getUploadDate() + TextFormat.RESET;
    }
}