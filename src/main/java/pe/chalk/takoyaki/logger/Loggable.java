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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-17
 */
public interface Loggable {
    enum Level implements Prefix {
        DEBUG    ("DEBUG",    Arrays.asList(TextFormat.RESET, TextFormat.ITALIC, TextFormat.DARK_GRAY)),
        INFO     ("INFO",     Arrays.asList(TextFormat.RESET, TextFormat.WHITE)),
        WARNING  ("WARNING",  Arrays.asList(TextFormat.RESET, TextFormat.YELLOW)),
        CRITICAL ("CRITICAL", Arrays.asList(TextFormat.RESET, TextFormat.LIGHT_PURPLE)),
        ERROR    ("ERROR",    Arrays.asList(TextFormat.RESET, TextFormat.RED));

        private final String prefix;
        private final String formats;

        Level(String prefix, List<TextFormat> formats){
            this.prefix = prefix;
            this.formats = formats.stream().map(TextFormat::toString).collect(Collectors.joining());
        }

        public String getPrefix(){
            return this.prefix;
        }

        public String getFormats(){
            return this.formats;
        }
    }

    String println(String message);
    String printf(String message, String... args);

    String newLine();

    String debug(String message);
    String info(String message);
    String warning(String message);
    String critical(String message);
    String error(String message);
}