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
//     07/15/2011-2.2.1 Guy Pelletier
//       - 349424: persists during an preCalculateUnitOfWorkChangeSet event are lost
package org.eclipse.persistence.testing.models.jpa.inheritance.listeners;

import org.eclipse.persistence.internal.sessions.RepeatableWriteUnitOfWork;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.testing.models.jpa.inheritance.Bus;

public class SessionListener extends SessionEventAdapter {

    /**
     * PUBLIC:
     * This event is raised after the commit has begun on the UnitOfWork but before
     * the changes are calculated.
     */
    public void preCalculateUnitOfWorkChangeSet(SessionEvent event) {
        Object source = event.getSource();

        if (source instanceof RepeatableWriteUnitOfWork) {
            RepeatableWriteUnitOfWork uow = (RepeatableWriteUnitOfWork) source;
            if (uow.getNewObjectsCloneToOriginal().size() == 1) {
                Object cloneObject = uow.getNewObjectsCloneToOriginal().keySet().iterator().next();
                if (cloneObject instanceof Bus) {
                    String busDescription = ((Bus) cloneObject).getDescription();

                    if (busDescription.contains("Listener test Bus1")) {
                        Bus newBus = new Bus();
                        newBus.setDescription("Listener test Bus2");
                        // register a new bus to this uow.
                        uow.registerObject(newBus);
                    }
                }
            }
        }
    }
}
