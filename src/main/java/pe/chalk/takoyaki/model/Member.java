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

package pe.chalk.takoyaki.model;

import org.json.JSONObject;
import pe.chalk.takoyaki.target.Target;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-07
 */
public abstract class Member extends Data {
    private String username;
    private String nickname;

    public Member(Target<?> target, String username){
        this(target, username, null);
    }

    public Member(Target<?> target, String username, String nickname){
        super(target);

        this.username = username;
        this.nickname = nickname;
    }

    public String getUsername(){
        return this.username;
    }

    public String getNickname(){
        return this.nickname;
    }

    public InternetAddress getInternetAddress(){
        try{
            return new InternetAddress(this.getUsername() + "@" + this.getHost());
        }catch(AddressException e){
            e.printStackTrace();
            return null;
        }
    }

    public abstract String getHost();

    @Override
    public String toString(){
        if(this.getNickname() == null) return this.getUsername();
        return this.getNickname() + " (" + this.getUsername() + ")";
    }

    @Override
    public boolean equals(Object that){
        if(this == that) return true;
        if(that == null || this.getClass() != that.getClass()) return false;

        Member member = (Member) that;
        return this.getUsername() != null && this.getUsername().equals(member.getUsername());
    }

    @Override
    public int hashCode(){
        return username != null ? username.hashCode() : 0;
    }

    @Override
    public String getPrefix(){
        return this.getUsername();
    }

    @Override
    public JSONObject toJSON(){
        JSONObject jsonObject = super.toJSON();

        jsonObject.put("username", this.getUsername());
        jsonObject.put("nickname", this.getNickname());

        return jsonObject;
    }
}
