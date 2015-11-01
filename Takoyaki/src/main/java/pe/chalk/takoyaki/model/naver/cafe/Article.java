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

package pe.chalk.takoyaki.model.naver.cafe;

import org.json.JSONObject;
import pe.chalk.takoyaki.model.Member;
import pe.chalk.takoyaki.target.NaverCafe;
import pe.chalk.takoyaki.target.Target;
import pe.chalk.takoyaki.utils.Mailer;
import pe.chalk.takoyaki.utils.Taggable;
import pe.chalk.takoyaki.utils.TextFormat;

import java.util.Date;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-07
 */
public class Article extends SimpleArticle implements Taggable {
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
        return this.writer;
    }

    public String getHead(){
        return this.head;
    }

    public String getUploadDate(){
        return this.uploadDate;
    }

    public int getMenuId(){
        return this.menuId;
    }

    public int getViewCount(){
        return this.viewCount;
    }

    public int getRecommendedCount(){
        return this.recommendedCount;
    }

    public boolean isQuestion(){
        return this.isQuestion;
    }

    public boolean hasHead(){
        return this.getHead() != null && this.getHead().length() > 0;
    }

    @Override
    public String toString(){
        //TODO: Independence from Naver Cafe

        return TextFormat.GREEN + "[" + this.getId() + "] " + TextFormat.RESET
                + TextFormat.LIGHT_PURPLE + "[" + ((NaverCafe) this.getTarget()).getMenu(this.getMenuId()).getName() + "] " + TextFormat.RESET
                + (this.isQuestion() ? TextFormat.LIGHT_PURPLE + "Q. " + TextFormat.RESET : "")
                + (this.hasHead() ? TextFormat.LIGHT_PURPLE + "[" + this.getHead() + "] " + TextFormat.RESET : "")
                + TextFormat.encode(this.getTitle())
                + TextFormat.DARK_AQUA + " by " + this.getWriter().toString() + TextFormat.RESET
                + TextFormat.GOLD + " at " + TextFormat.SIMPLE_DATE_FORMAT.format(new Date(this.getCreationTime())) + " (" + this.getUploadDate() + ")" + TextFormat.RESET;
    }

    @Override
    public String toTag(Target target){
        return TextFormat.decode(this.toString(), TextFormat.Type.HTML)
                + "  <a href=\"http://cafe.naver.com/" + ((NaverCafe) target).getAddress() + "/" + this.getId()
                + "\"><img src=\"" + Mailer.HOOK_URL + "/ArticleDoctor.php?clubid=" + ((NaverCafe) target).getClubId() + "&articleid=" + this.getId()
                + "\" style=\"vertical-align: middle\" width=\"15px\" height=\"15px\"></a>";
    }

    @Override
    public JSONObject toJSON(){
        JSONObject jsonObject = super.toJSON();

        jsonObject.put("writer", this.getWriter());
        jsonObject.put("head", this.getHead());
        jsonObject.put("uploadDate", this.getUploadDate());
        jsonObject.put("menuId", this.getMenuId());
        jsonObject.put("viewCount", this.getViewCount());
        jsonObject.put("recommendedCount", this.getRecommendedCount());
        jsonObject.put("isQuestion", this.isQuestion());

        return jsonObject;
    }
}