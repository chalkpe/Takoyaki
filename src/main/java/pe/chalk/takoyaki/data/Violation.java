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

import java.util.HashMap;
import java.util.Map;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-20
 */
public class Violation extends Data {
    private static final long serialVersionUID = 808421183210237888L;

    public enum Level {
        DEMOTE,

        REMOVE,
        WARN,
        ACTIVITY_STOP_ONE_DAY,
        ACTIVITY_STOP_ONE_WEEK,
        ACTIVITY_STOP_ONE_MONTH,
        ACTIVITY_STOP_LIMITLESS,
        SECEDE,
        SECEDE_AND_REJECT_REJOIN,
        ACCUSE
    }

    @SuppressWarnings("serial")
    public static Map<Level, String> prefixMap = new HashMap<Level, String>(){{
        put(Level.DEMOTE,                   "강등");

        put(Level.REMOVE,                   "삭제");
        put(Level.WARN,                     "주의");
        put(Level.ACTIVITY_STOP_ONE_DAY,    "활동 정지 1일");
        put(Level.ACTIVITY_STOP_ONE_WEEK,   "활동 정지 7일");
        put(Level.ACTIVITY_STOP_ONE_MONTH,  "활동 정지 30일");
        put(Level.ACTIVITY_STOP_LIMITLESS,  "무기한 활동 정지");
        put(Level.SECEDE,                   "강제 탈퇴");
        put(Level.SECEDE_AND_REJECT_REJOIN, "재가입 불가 강제 탈퇴");
        put(Level.ACCUSE,                   "고소");
    }};

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
        return Violation.prefixMap.get(this.getLevel());
    }
}