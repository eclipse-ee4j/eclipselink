/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.inheritance.listeners;

import javax.persistence.PrePersist;
import javax.persistence.PostPersist;

/**
 * A listener for all TireInfo entities.
 */
public class TireInfoListener extends ListenerSuperclass {
    public static int PRE_PERSIST_COUNT = 0;
    public static int POST_PERSIST_COUNT = 0;

    @PrePersist
    public void prePersist(Object tireInfo) {
        PRE_PERSIST_COUNT++;
    }

    @PostPersist
    public void postPersist(Object tireInfo) {
        POST_PERSIST_COUNT++;
    }
}
