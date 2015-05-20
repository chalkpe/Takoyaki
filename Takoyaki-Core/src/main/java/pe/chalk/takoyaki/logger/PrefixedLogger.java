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
 * @author ChalkPE <amato0617@gmail.com>
 * @since 2015-04-17
 */
public class PrefixedLogger implements Loggable {
    private Logger logger;
    private String prefix;

    public PrefixedLogger(Logger logger, Prefix prefix){
        this(logger, prefix.getPrefix());
    }

    public PrefixedLogger(Logger logger, String prefix){
        this.logger = logger;
        this.prefix = "[" + prefix + "]";
    }

    @Override
    public void log(Level level, String message){
        this.logger.log(level, message);
    }

    @Override
    public void debug(String message){
        this.logger.debug(this.prefix + " " + message);
    }

    @Override
    public void info(String message){
        this.logger.info(this.prefix + " " + message);
    }

    @Override
    public void warning(String message){
        this.logger.warning(this.prefix + " " + message);
    }

    @Override
    public void notice(String message){
        this.logger.notice(this.prefix + " " + message);
    }

    @Override
    public void error(String message){
        this.logger.error(this.prefix + " " + message);
    }

    @Override
    public void critical(String message){
        this.logger.critical(this.prefix + " " + message);
    }
}