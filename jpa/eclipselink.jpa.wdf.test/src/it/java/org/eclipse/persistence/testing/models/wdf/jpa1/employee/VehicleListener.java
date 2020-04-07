/*
 * Copyright (c) 2005, 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.lang.annotation.Annotation;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

public class VehicleListener {

    private static CallbackEventListener LISTENER = null;

    public static void setListener(final CallbackEventListener listener) {
        LISTENER = listener;
    }

    @PrePersist
    public void prePersist(final Object entity) {
        fireEvent(entity, PrePersist.class, LISTENER);
    }

    @PostPersist
    public void postPersist(final Object entity) {
        fireEvent(entity, PostPersist.class, LISTENER);
    }

    @PreRemove
    public void preRemove(final Vehicle entity) {
        fireEvent(entity, PreRemove.class, LISTENER);
    }

    @PostRemove
    public void postRemove(final Object entity) {
        fireEvent(entity, PostRemove.class, LISTENER);
    }

    @PreUpdate
    public void preUpdate(final Object entity) {
        fireEvent(entity, PreUpdate.class, LISTENER);
    }

    @PostUpdate
    public void postUpdate(final Vehicle entity) {
        fireEvent(entity, PostUpdate.class, LISTENER);
    }

    @PostLoad
    public void postLoad(final Object entity) {
        fireEvent(entity, PostLoad.class, LISTENER);
    }

    void fireEvent(final Object entity, final Class<? extends Annotation> event, final CallbackEventListener listener) {
        if (listener != null) {
            listener.callbackCalled(entity, event);
        }
    }

}
