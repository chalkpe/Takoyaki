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
 * @since 2015-04-12
 */
public class SimpleArticle extends Data {
    private static final long serialVersionUID = 4569340242232623025L;

    private int id;
    private String title;
    private int commentCount;

    public SimpleArticle(Target target, int id, String title, int commentCount){
        super(target);

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
        return TextFormat.GREEN + "[" + this.getId() + "] " + TextFormat.RESET + this.getTitle() + TextFormat.GOLD + " [" + this.getCommentCount() + "]" + TextFormat.RESET;
    }

    @Override
    public String getPrefix(){
        return String.valueOf(this.getId());
    }
}