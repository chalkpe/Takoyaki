/*
 * Copyright 2015 ChalkPE
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

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-07
 */
public class Member implements Data {
    public static final String DEFAULT_ID = "********";

    private String id;
    private String name;

    public Member(String id, String name){
        this.id = id == null ? "" : id;
        this.name = name;
    }

    public String getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public String toString(){
        return this.getName() + " (" + (this.getId().length() > 0 ? this.getId() : DEFAULT_ID) + ")";
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof Member && this.getId().equalsIgnoreCase(((Member) obj).getId());
    }
}
