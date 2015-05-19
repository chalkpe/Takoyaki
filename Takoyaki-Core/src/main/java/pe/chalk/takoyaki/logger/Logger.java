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

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-14
 */
public abstract class Logger implements Loggable {
    @Override
    public void log(Level level, String message){
        switch(level){
            case DEBUG:
                debug(message);
                break;

            case INFO:
                info(message);
                break;

            case WARNING:
                warning(message);
                break;

            case CRITICAL:
        }
    }

    @Override
    public void debug(String message){
        send(Level.DEBUG, Level.DEBUG.getFormats() + message);
    }

    @Override
    public void info(String message){
        send(Level.INFO, Level.INFO.getFormats() + message);
    }

    @Override
    public void notice(String message){
        send(Level.NOTICE, Level.NOTICE.getFormats() + message);
    }

    @Override
    public void warning(String message){
        send(Level.WARNING, Level.WARNING.getFormats() + message);
    }

    @Override
    public void error(String message){
        send(Level.ERROR, Level.ERROR.getFormats() + message);
    }

    @Override
    public void critical(String message){
        send(Level.CRITICAL, Level.CRITICAL.getFormats() + message);
    }

    protected abstract void send(Level level, String message);
}