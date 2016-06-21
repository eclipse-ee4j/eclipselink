/*******************************************************************************
 * Copyright (c) 2012, 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial contribution for Bug 366748 - JPA 2.1 Injectable Entity Listeners
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa21.entitylistener;

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
