/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     07/05/2010-2.1.1 Guy Pelletier
//       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
package org.eclipse.persistence.testing.models.jpa.xml.advanced.dynamic;

import java.util.EventListener;

public class EmployeeListener implements EventListener {
    public static int PRE_PERSIST_COUNT = 0;
    public static int POST_PERSIST_COUNT = 0;
    public static int PRE_REMOVE_COUNT = 0;
    public static int POST_REMOVE_COUNT = 0;
    public static int PRE_UPDATE_COUNT = 0;
    public static int POST_UPDATE_COUNT = 0;
    public static int POST_LOAD_COUNT = 0;

    public void prePersist(Object emp) {
        PRE_PERSIST_COUNT++;
    }

    public void postPersist(Object emp) {
        POST_PERSIST_COUNT++;
    }

    public void preRemove(Object emp) {
        PRE_REMOVE_COUNT++;
    }

    public void postRemove(Object emp) {
        POST_REMOVE_COUNT++;
    }

    public void preUpdate(Object emp) {
        PRE_UPDATE_COUNT++;
    }

    public void postUpdate(Object emp) {
        POST_UPDATE_COUNT++;
    }

    public void postLoad(Object emp) {
        POST_LOAD_COUNT++;
    }
}
