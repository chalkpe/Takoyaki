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
 * @since 2015-04-17
 */
public class PrefixedLogger implements Loggable {
    private Loggable parent;
    private Prefix prefix;

    public PrefixedLogger(Loggable parent, Prefix prefix){
        this.parent = parent;
        this.prefix = prefix;
    }

    @Override
    public String println(String message){
        return this.parent.println(message);
    }

    @Override
    public String newLine(){
        return this.parent.newLine();
    }

    @Override
    public String log(Level level, String message){
        return this.parent.log(level, message);
    }

    
    @Override
    public String debug(String message){
        return this.parent.debug(String.format("[%s] %s", prefix.getPrefix(), message));
    }

    @Override
    public String info(String message){
        return this.parent.info(String.format("[%s] %s", prefix.getPrefix(), message));
    }

    @Override
    public String warning(String message){
        return this.parent.warning(String.format("[%s] %s", prefix.getPrefix(), message));
    }

    @Override
    public String critical(String message){
        return this.parent.critical(String.format("[%s] %s", prefix.getPrefix(), message));
    }

    @Override
    public String error(String message){
        return this.parent.error(String.format("[%s] %s", prefix.getPrefix(), message));
    }

    public PrefixedLogger sub(Prefix prefix){
        return new PrefixedLogger(this, prefix);
    }
}