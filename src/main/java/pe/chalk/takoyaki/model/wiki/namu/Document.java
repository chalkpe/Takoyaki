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

package pe.chalk.takoyaki.model.wiki.namu;

import org.json.JSONObject;
import pe.chalk.takoyaki.model.Data;
import pe.chalk.takoyaki.target.Target;
import pe.chalk.takoyaki.utils.TextFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-10-31
 */
public class Document extends Data {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");

    private String title;
    private String status;
    private long date;

    public Document(Target<?> target, String title, String status, long date){
        super(target);

        this.title = title;
        this.status = status;
        this.date = date;
    }

    public static Document create(Target<?> target, JSONObject json){
        return new Document(target, json.getString("document"), json.getString("status"), json.getLong("date"));
    }

    public boolean hasParent(){
        return this.getTitle().contains("/");
    }

    public Document getParent(){
        if(!this.hasParent()) return null;
        return new Document(this.getTarget(), this.getTitle().substring(0, this.getTitle().lastIndexOf("/")), "normal", this.getDate());
    }

    public String getTitle(){
        return this.title;
    }

    public String getStatus(){
        return this.status;
    }

    public long getDate(){
        return this.date;
    }

    @Override
    public String getPrefix(){
        return Document.DATE_FORMAT.format(new Date(this.getDate() * 1000L));
    }

    @Override
    public boolean equals(Object that){
        if(this == that) return true;
        if(that == null || this.getClass() != that.getClass()) return false;

        Document doc = (Document) that;
        return this.getTitle().equals(doc.getTitle()) && this.getStatus().equals(doc.getStatus()) && this.getDate() == doc.getDate();
    }

    @Override
    public String toString(){
        return "[" + this.getPrefix() + "] " + (this.getStatus().equals("delete") ? TextFormat.DARK_GRAY : "") + this.getTitle();
    }
}
