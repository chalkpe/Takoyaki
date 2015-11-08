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

import pe.chalk.takoyaki.utils.Prefix;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-04-17
 */
public class PrefixedLogger implements Loggable {
    private Loggable parent;
    private String prefix;

    public PrefixedLogger(Loggable parent, Prefix prefix){
        this(parent, prefix.getPrefix());
    }

    public PrefixedLogger(Loggable parent, String prefix){
        this.parent = parent;
        this.prefix = "[" + prefix + "]";
    }

    public Loggable getParent(){
        return this.parent;
    }

    @Override
    public void log(Level level, String message){
        this.parent.log(level, message);
    }

    @Override
    public void debug(String message){
        this.parent.debug(this.prefix + " " + message);
    }

    @Override
    public void info(String message){
        this.parent.info(this.prefix + " " + message);
    }

    @Override
    public void warning(String message){
        this.parent.warning(this.prefix + " " + message);
    }

    @Override
    public void notice(String message){
        this.parent.notice(this.prefix + " " + message);
    }

    @Override
    public void error(String message){
        this.parent.error(this.prefix + " " + message);
    }

    @Override
    public void critical(String message){
        this.parent.critical(this.prefix + " " + message);
    }
}