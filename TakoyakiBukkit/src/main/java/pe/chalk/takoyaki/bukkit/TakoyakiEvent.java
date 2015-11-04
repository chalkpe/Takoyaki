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

package pe.chalk.takoyaki.bukkit;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pe.chalk.takoyaki.model.Data;

@SuppressWarnings("unused")

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @author 파차리로디드 <fcreloaded@outlook.kr>
 * @since 2015-05-30
 */
public class TakoyakiEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Data[] data;

    public TakoyakiEvent(Data[] data){
        this.data = data;
    }

    @Override
    public HandlerList getHandlers(){
        return TakoyakiEvent.handlers;
    }

    public static HandlerList getHandlerList(){
        return TakoyakiEvent.handlers;
    }

    public Data[] getData(){
        return this.data;
    }
}