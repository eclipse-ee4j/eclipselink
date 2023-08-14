/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sequencing;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.broker.SessionBroker;

import java.util.Iterator;

class SessionBrokerSequencing implements Sequencing {
    protected SessionBroker broker;
    protected int whenShouldAcquireValueForAll;

    public static boolean atLeastOneSessionHasSequencing(SessionBroker br) {
        boolean hasSequencing = false;
        Iterator<AbstractSession> sessionEnum = br.getSessionsByName().values().iterator();
        while (sessionEnum.hasNext() && !hasSequencing) {
            AbstractSession session = sessionEnum.next();
            hasSequencing = session.getSequencing() != null;
        }
        return hasSequencing;
    }

    public SessionBrokerSequencing(SessionBroker broker) {
        this.broker = broker;
        initialize();
    }

    protected void initialize() {
        whenShouldAcquireValueForAll = UNDEFINED;
        boolean first = true;
        Iterator<AbstractSession> sessionEnum = broker.getSessionsByName().values().iterator();
        while ((first || (whenShouldAcquireValueForAll != UNDEFINED)) && sessionEnum.hasNext()) {
            AbstractSession session = sessionEnum.next();
            Sequencing sequencing = session.getSequencing();
            if (sequencing != null) {
                if (first) {
                    whenShouldAcquireValueForAll = sequencing.whenShouldAcquireValueForAll();
                    first = false;
                } else {
                    if (whenShouldAcquireValueForAll != sequencing.whenShouldAcquireValueForAll()) {
                        whenShouldAcquireValueForAll = UNDEFINED;
                    }
                }
            }
        }
    }

    // internal
    protected Sequencing get(Class<?> cls) {
        return broker.getSessionForClass(cls).getSequencing();
    }

    @Override
    public int whenShouldAcquireValueForAll() {
        return whenShouldAcquireValueForAll;
    }

    @Override
    public Object getNextValue(Class<?> cls) {
        return get(cls).getNextValue(cls);
    }
}
