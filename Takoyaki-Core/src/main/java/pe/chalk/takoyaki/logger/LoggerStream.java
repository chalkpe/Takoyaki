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

import java.io.PrintStream;
import java.util.Date;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-15
 */
public class LoggerStream extends PrintStream {
    private TextFormat.Type type;

    public LoggerStream(TextFormat.Type type, PrintStream stream){
        super(stream);
        this.type = type;
    }

    public TextFormat.Type getType(){
        return this.type;
    }

    public void setType(TextFormat.Type type){
        this.type = type;
    }

    public void println(Loggable.Level level, String message){
        super.println(TextFormat.replaceTo(this.getType(), TextFormat.AQUA + Logger.DATE_FORMAT.format(new Date()) + " " + TextFormat.RESET + level.getFormats() + "[" + level.getPrefix() + "] " + message + TextFormat.RESET));
    }
}
