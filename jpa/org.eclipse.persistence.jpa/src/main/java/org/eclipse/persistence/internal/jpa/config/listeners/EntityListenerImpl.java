/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.listeners;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;
import org.eclipse.persistence.jpa.config.EntityListener;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class EntityListenerImpl extends MetadataImpl<EntityListenerMetadata> implements EntityListener {

    public EntityListenerImpl() {
        super(new EntityListenerMetadata());
    }

    @Override
    public EntityListener setClass(String className) {
        getMetadata().setClassName(className);
        return this;
    }

    @Override
    public EntityListener setPostLoad(String methodName) {
        getMetadata().setPostLoad(methodName);
        return this;
    }

    @Override
    public EntityListener setPostPersist(String methodName) {
        getMetadata().setPostPersist(methodName);
        return this;
    }

    @Override
    public EntityListener setPostRemove(String methodName) {
        getMetadata().setPostRemove(methodName);
        return this;
    }

    @Override
    public EntityListener setPostUpdate(String methodName) {
        getMetadata().setPostUpdate(methodName);
        return this;
    }

    @Override
    public EntityListener setPrePersist(String methodName) {
        getMetadata().setPrePersist(methodName);
        return this;
    }

    @Override
    public EntityListener setPreRemove(String methodName) {
        getMetadata().setPreRemove(methodName);
        return this;
    }

    @Override
    public EntityListener setPreUpdate(String methodName) {
        getMetadata().setPreUpdate(methodName);
        return this;
    }

}
