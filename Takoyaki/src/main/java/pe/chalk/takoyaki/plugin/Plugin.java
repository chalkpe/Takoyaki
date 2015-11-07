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

package pe.chalk.takoyaki.plugin;

import pe.chalk.takoyaki.filter.Filter;
import pe.chalk.takoyaki.logger.PrefixedLogger;
import pe.chalk.takoyaki.model.Data;
import pe.chalk.takoyaki.utils.Prefix;

import java.util.List;

/**
 * @author ChalkPE <chalkpe@gmail.com>
 * @since 2015-10-05
 */
public interface Plugin extends Prefix {
    String getName();
    @Override
    default String getPrefix(){
        return this.getName();
    }

    PrefixedLogger getLogger();
    String getVersion();

    @SuppressWarnings("unused")
    void reload();

    void onLoad();
    void onDestroy();

    void onStart();
    void onDataAdded(List<? extends Data> freshData, Filter<?, ? extends Data> filter);
}
