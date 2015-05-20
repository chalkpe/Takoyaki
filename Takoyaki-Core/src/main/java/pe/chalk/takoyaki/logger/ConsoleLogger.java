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

import java.util.Date;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-15
 */
public class ConsoleLogger extends Logger {
    public ConsoleLogger(LoggerTransmitter... transmitters){
        super(transmitters);
    }

    @Override
    protected void send(Level level, String message){
        message = TextFormat.AQUA + DATE_FORMAT.format(new Date()) + " " + TextFormat.RESET + level.getFormats() + "[" + level.getPrefix() + "] " + message + TextFormat.RESET;

        System.out.println(TextFormat.replaceTo(TextFormat.Type.ANSI, message));
        for(LoggerTransmitter transmitter : this.transmitters){
            transmitter.transmit(level, message);
        }
    }
}