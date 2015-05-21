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

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-14
 */
public class Logger implements Loggable {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    protected List<LoggerStream> streams;
    protected List<LoggerTransmitter> transmitters;

    public Logger(){
        this(new ArrayList<>(), new ArrayList<>());
    }

    public Logger(List<LoggerStream> streams, List<LoggerTransmitter> transmitters){
        this.streams = streams;
        this.transmitters = transmitters;
    }

    public boolean addStream(LoggerStream stream){
        return this.streams.add(stream);
    }

    public boolean removeStream(PrintStream stream){
        for(LoggerStream loggerStream : this.streams){
            if(stream.equals(loggerStream.getStream())){
                return this.streams.remove(loggerStream);
            }
        }
        return false;
    }

    public boolean removeStream(LoggerStream stream){
        return this.streams.remove(stream);
    }

    public boolean addTransmitter(LoggerTransmitter transmitter){
        return this.transmitters.add(transmitter);
    }

    public boolean removeTransmitter(LoggerTransmitter transmitter){
        return this.transmitters.remove(transmitter);
    }

    @Override
    public void log(Level level, String message){
        switch(level){
            case DEBUG:
                debug(message);
                break;

            case INFO:
                info(message);
                break;

            case NOTICE:
                notice(message);
                break;

            case WARNING:
                warning(message);
                break;

            case ERROR:
                error(message);
                break;

            case CRITICAL:
                critical(message);
                break;
        }
    }

    @Override
    public void debug(String message){
        send(Level.DEBUG, message);
    }

    @Override
    public void info(String message){
        send(Level.INFO, message);
    }

    @Override
    public void notice(String message){
        send(Level.NOTICE, message);
    }

    @Override
    public void warning(String message){
        send(Level.WARNING, message);
    }

    @Override
    public void error(String message){
        send(Level.ERROR, message);
    }

    @Override
    public void critical(String message){
        send(Level.CRITICAL, message);
    }

    protected void send(Level level, String message){
        this.streams.forEach(stream -> stream.println(level, message));
        this.transmitters.forEach(transmitter -> transmitter.transmit(level, message));
    }
}