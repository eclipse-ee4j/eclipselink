/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sequencing;

import java.util.Iterator;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.broker.SessionBroker;

class SessionBrokerSequencing implements Sequencing {
    protected SessionBroker broker;
    protected int whenShouldAcquireValueForAll;

    public static boolean atLeastOneSessionHasSequencing(SessionBroker br) {
        boolean hasSequencing = false;
        Iterator sessionEnum = br.getSessionsByName().values().iterator();
        while (sessionEnum.hasNext() && !hasSequencing) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
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
        Iterator sessionEnum = broker.getSessionsByName().values().iterator();
        while ((first || (whenShouldAcquireValueForAll != UNDEFINED)) && sessionEnum.hasNext()) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
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
    protected Sequencing get(Class cls) {
        return broker.getSessionForClass(cls).getSequencing();
    }

    @Override
    public int whenShouldAcquireValueForAll() {
        return whenShouldAcquireValueForAll;
    }

    @Override
    public Object getNextValue(Class cls) {
        return get(cls).getNextValue(cls);
    }
}
