/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

/**
 * This type is used to catch events thrown from session.  The constructor must have
 * when all is completed the events vector passed into the constructor will have
 * any evets that have occurred up until that point.
 */
import java.util.Vector;

public class StoredProcedureOutputListener extends org.eclipse.persistence.sessions.SessionEventAdapter {
    private Vector events;

    public StoredProcedureOutputListener(Vector events) {
        this.events = events;
    }

    /*
     * This method is called when a sesion event is thrown
     */
    public void outputParametersDetected(org.eclipse.persistence.sessions.SessionEvent event) {
        this.events.addElement(event);
    }
}
