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
import pe.chalk.takoyaki.model.Data;
import pe.chalk.takoyaki.model.Member;
import pe.chalk.takoyaki.target.Target;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-20
 */
public class Violation extends Data {
    @SuppressWarnings("unused")
    public enum Level {
        CAUTION("주의"),
        WARN("경고"),

        REMOVE("삭제"),
        DEMOTE("강등"),

        STOP_ACTIVITY_ONE_DAY("활동 정지 1일"),
        STOP_ACTIVITY_ONE_WEEK("활동 정지 7일"),
        STOP_ACTIVITY_ONE_MONTH("활동 정지 30일"),
        STOP_ACTIVITY_LIMITLESS("무기한 활동 정지"),

        SECEDE("강제 탈퇴"),
        REFUSE_REJOIN("재가입 불가"),
        SECEDE_AND_REFUSE_REJOIN("재가입 불가 강제 탈퇴"),

        ACCUSE("고소");

        private String name;

        Level(String name){
            this.name = name;
        }

        @Override
        public String toString(){
            return this.name;
        }
    }

    private String name;
    private Level level;

    private Member violator;
    private Data[] violations;

    public Violation(Target target, String name, Level level, Member violator, Data[] violations){
        super(target);

        this.name = name;
        this.level = level;

        this.violator = violator;
        this.violations = violations;
    }

    public String getName(){
        return this.name;
    }

    public Level getLevel(){
        return this.level;
    }

    public Member getViolator(){
        return this.violator;
    }

    public Data[] getViolations(){
        return this.violations;
    }

    @Override
    public String getPrefix(){
        return this.getLevel().toString();
    }

    @Override
    public JSONObject toJSON(){
        JSONObject jsonObject = super.toJSON();

        jsonObject.put("name", this.getName());
        jsonObject.put("level", this.getLevel());
        jsonObject.put("violator", this.getViolator());
        jsonObject.put("violations", this.getViolations());

        return jsonObject;
    }
}