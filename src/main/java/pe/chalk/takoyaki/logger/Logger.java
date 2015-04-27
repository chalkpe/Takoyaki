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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-14
 */
public class Logger implements Loggable {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    @Override
    public String println(String message){
        System.out.println(message);
        return message;
    }

    @Override
    public String printf(String message, String... args){
        return println(String.format("[%s] %s", Logger.SIMPLE_DATE_FORMAT.format(new Date()), String.format(message, args)));
    }

    @Override
    public String newLine(){
        return this.println("");
    }

    @Override
    public String debug(String message, String... args){
        return printf(String.format("%s[%s] %s", Level.DEBUG.getFormats(), Level.DEBUG.getPrefix(), message), args);
    }

    @Override
    public String info(String message, String... args){
        return printf(String.format("%s[%s] %s", Level.INFO.getFormats(), Level.INFO.getPrefix(), message), args);
    }

    @Override
    public String warning(String message, String... args){
        return printf(String.format("%s[%s] %s", Level.WARNING.getFormats(), Level.WARNING.getPrefix(), message), args);
    }

    @Override
    public String critical(String message, String... args){
        return printf(String.format("%s[%s] %s", Level.CRITICAL.getFormats(), Level.CRITICAL.getPrefix(), message), args);
    }

    @Override
    public String error(String message, String... args){
        return printf(String.format("%s[%s] %s", Level.ERROR.getFormats(), Level.ERROR.getPrefix(), message), args);
    }

    public PrefixedLogger getPrefixed(Prefix prefix){
        return new PrefixedLogger(this, prefix);
    }
}