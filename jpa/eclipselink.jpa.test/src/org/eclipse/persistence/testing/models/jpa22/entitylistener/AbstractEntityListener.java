/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.models.jpa22.entitylistener;

import org.eclipse.persistence.testing.models.jpa22.sessionbean.InjectedBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

/* Trigger Bug #495587 */
public class AbstractEntityListener<E extends EntityListenerHolderInterface> {
    public static boolean INJECTED_RETURN_VALUE = false;
    public static int POST_CONSTRUCT_CALLS = 0;
    public static int PRE_DESTROY_CALLS = 0;

    @Inject
    protected InjectedBean injected;

    @PrePersist
    public void prePersist(E object) {
        INJECTED_RETURN_VALUE = injected.isCalled();
    }

    @PostPersist
    public void postPersist(E object) {
    }

    @PreDestroy
    public void preDestroy() {
        PRE_DESTROY_CALLS++;
    }

    @PostConstruct
    public void postConstruct() {
        POST_CONSTRUCT_CALLS++;
    }
}
