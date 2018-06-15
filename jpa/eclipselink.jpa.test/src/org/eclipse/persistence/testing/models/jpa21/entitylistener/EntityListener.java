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
package org.eclipse.persistence.testing.models.jpa21.entitylistener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

import org.eclipse.persistence.testing.models.jpa21.sessionbean.InjectedBean;

public class EntityListener {

    public static boolean INJECTED_RETURN_VALUE = false;
    public static int POST_CONSTRUCT_CALLS = 0;
    public static int PRE_DESTROY_CALLS = 0;

    @EJB
    protected InjectedBean injected;

    @PrePersist
    public void prePersist(Object object) {
        INJECTED_RETURN_VALUE = injected.isCalled();
    }

    @PostPersist
    public void postPersist(Object object){
    }

    @PreDestroy
    public void preDestroy(){
        PRE_DESTROY_CALLS++;
    }

    @PostConstruct
    public void postConstruct(){
        POST_CONSTRUCT_CALLS++;
    }
}
