/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - initial contribution for Bug 366748 - JPA 2.1 Injectable Entity Listeners
package org.eclipse.persistence.testing.models.jpa22.entitylistener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

public class EntityListener extends AbstractEntityListener<EntityListenerHolder> {
    @Override
    @PrePersist
    public void prePersist(EntityListenerHolder object) {
        INJECTED_RETURN_VALUE = injected.isCalled();
    }

    @Override
    @PostPersist
    public void postPersist(EntityListenerHolder object) {
    }

    @Override
    @PreDestroy
    public void preDestroy() {
        PRE_DESTROY_CALLS++;
    }

    @Override
    @PostConstruct
    public void postConstruct() {
        POST_CONSTRUCT_CALLS++;
    }
}
