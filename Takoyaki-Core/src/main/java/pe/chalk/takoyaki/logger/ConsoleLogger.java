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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-15
 */
public class ConsoleLogger extends Logger {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    public PrintStream out;

    public ConsoleLogger(){
        this(null);
    }

    public ConsoleLogger(PrintStream out){
        this.out = out;
    }

    @Override
    protected void send(Level level, String message){
        String log = TextFormat.AQUA + DATE_FORMAT.format(new Date()) + " " + TextFormat.RESET + message + TextFormat.RESET;
        System.out.println(log);

        //TODO: Implement this method
    }
}