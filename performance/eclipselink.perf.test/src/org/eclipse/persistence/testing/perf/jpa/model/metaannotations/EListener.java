/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//              ljungmann - initial implementation
package org.eclipse.persistence.testing.perf.jpa.model.metaannotations;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class EListener {

    @PrePersist
    public void prePersist(Object entity) {
        System.out.println("Entity '" + entity + "' is going to be persisted");
    }

    @PostPersist
    public void postPersist(Object entity) {
        System.out.println("Entity '" + entity + "' has been persisted");
    }

    @PreUpdate
    public void preUpdade(Object entity) {
        System.out.println("Entity '" + entity + "' is going to be updated");
    }

    @PostUpdate
    public void postUpdade(Object entity) {
        System.out.println("Entity '" + entity + "' has been updated");
    }
}
