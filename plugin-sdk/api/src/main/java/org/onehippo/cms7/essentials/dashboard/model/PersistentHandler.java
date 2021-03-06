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

package org.onehippo.cms7.essentials.dashboard.model;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Session;

import org.onehippo.cms7.essentials.dashboard.config.Document;

/**
 * Persists JcrModel objects
 *
 * @version "$Id$"
 * @see org.onehippo.cms7.essentials.dashboard.config.Document
 */
public interface PersistentHandler<T, E extends Item> {

    E execute(Session session, Document model, T annotation);

    /**
     * NOTE: session is left open
     * @param session
     * @param parent
     * @param path
     * @param annotation
     * @return
     */
    E read(Session session, Node parent, String path, T annotation);
}
