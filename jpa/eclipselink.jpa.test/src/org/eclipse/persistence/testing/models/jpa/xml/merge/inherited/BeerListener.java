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
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import java.util.EventListener;

public class BeerListener implements EventListener {
    // Listener class used to test the mapped-superclass entity-listener XML
    // element.

    public static int POST_PERSIST_COUNT = 0;

    public BeerListener() {
        super();
    }

    public void postPersist(Object obj) {
        POST_PERSIST_COUNT++;
    }
}
