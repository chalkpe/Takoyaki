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

package pe.chalk.takoyaki.logger;

import pe.chalk.takoyaki.utils.TextFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-14
 */
public abstract class Logger implements Loggable {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public static Map<Level, String> prefixMap = new HashMap<Level, String>(){{
        put(Level.DEBUG,    "DEBUG");
        put(Level.INFO,     "INFO");
        put(Level.WARNING,  "WARNING");
        put(Level.CRITICAL, "CRITICAL");
        put(Level.ERROR,    "ERROR");
    }};

    public static Map<Level, String> colorMap = new HashMap<Level, String>(){{
        put(Level.DEBUG,    TextFormat.RESET.toString() + TextFormat.DARK_GRAY + TextFormat.ITALIC);
        put(Level.INFO,     TextFormat.RESET.toString() + TextFormat.WHITE);
        put(Level.WARNING,  TextFormat.RESET.toString() + TextFormat.YELLOW);
        put(Level.CRITICAL, TextFormat.RESET.toString() + TextFormat.LIGHT_PURPLE);
        put(Level.ERROR,    TextFormat.RESET.toString() + TextFormat.RED);
    }};

    protected abstract void log(String message);

    protected void print(Level level, String message, Date date){
        log(String.format("%s[%s] [%s] %s", colorMap.get(level), Logger.SIMPLE_DATE_FORMAT.format(date == null ? new Date() : date), prefixMap.get(level), message));
    }

    @Override
    public void debug(String message){
        print(Level.DEBUG, message, null);
    }

    @Override
    public void info(String message){
        print(Level.INFO, message, null);
    }

    @Override
    public void warning(String message){
        print(Level.WARNING, message, null);
    }

    @Override
    public void critical(String message){
        print(Level.CRITICAL, message, null);
    }

    @Override
    public void error(String message){
        print(Level.ERROR, message, null);
    }

    public PrefixedLogger getPrefixed(Prefix prefix){
        return new PrefixedLogger(this, prefix);
    }
}