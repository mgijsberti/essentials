/*
 * Copyright 2014 Hippo B.V. (http://www.onehippo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onehippo.cms7.essentials.dashboard.event.listeners;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Singleton;

import org.onehippo.cms7.essentials.dashboard.event.PluginEventListener;
import org.onehippo.cms7.essentials.dashboard.event.RebuildEvent;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

/**
 * @version "$Id$"
 */
@Component
@Singleton
public class RebuildProjectEventListener implements PluginEventListener<RebuildEvent> {

    public static final int MAX_ITEMS = 25;
    private final List<RebuildEvent> events = new CopyOnWriteArrayList<>();

    @Override
    @Subscribe
    public void onPluginEvent(final RebuildEvent event) {
        if (events.size() == MAX_ITEMS) {
            events.remove(0);
        }
        events.add(event);

    }

    public List<RebuildEvent> pollEvents() {
        return new LinkedList<>(events);

    }

    public List<RebuildEvent> consumeEvents() {
        final List<RebuildEvent> pluginEvents = new LinkedList<>(events);
        events.clear();
        return pluginEvents;
    }
}
