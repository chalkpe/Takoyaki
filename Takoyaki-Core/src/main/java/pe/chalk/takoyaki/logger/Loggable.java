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

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-17
 */
public interface Loggable {
    enum Level implements Prefix {
        DEBUG    ("DEBUG",    new TextFormat[]{TextFormat.DARK_GRAY, TextFormat.ITALIC}),
        INFO     ("INFO",     new TextFormat[]{TextFormat.WHITE}),
        NOTICE   ("NOTICE",   new TextFormat[]{TextFormat.AQUA}),
        WARNING  ("WARNING",  new TextFormat[]{TextFormat.YELLOW}),
        ERROR    ("ERROR",    new TextFormat[]{TextFormat.DARK_RED}),
        CRITICAL ("CRITICAL", new TextFormat[]{TextFormat.RED});

        private final String prefix;
        private final String formats;

        Level(String prefix, TextFormat[] formats){
            this.prefix = prefix;
            this.formats = Stream.of(formats).map(TextFormat::toString).collect(Collectors.joining());
        }

        public String getPrefix(){
            return this.prefix;
        }

        public String getFormats(){
            return this.formats;
        }
    }

    void log(Level level, String message);

    void debug(String message);
    void info(String message);
    void notice(String message);
    void warning(String message);
    void error(String message);
    void critical(String message);
}