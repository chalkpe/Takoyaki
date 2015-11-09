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

package pe.chalk.takoyaki.event;

import pe.chalk.takoyaki.filter.Filter;
import pe.chalk.takoyaki.model.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-11-09
 */
public class Event {
    private final Filter<?, ? extends Data> filter;
    private final List<? extends Data> dataList;

    public Event(Filter<?, ? extends Data> filter, List<? extends Data> dataList){
        this.filter = filter;
        this.dataList = Collections.unmodifiableList(dataList);
    }

    public Filter<?, ? extends Data> getFilter(){
        return this.filter;
    }

    public List<? extends Data> getDataList(){
        return this.dataList;
    }
}
