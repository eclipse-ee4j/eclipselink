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
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.internal.queries.*;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionEventAdapter;
import org.eclipse.persistence.sessions.SessionEvent;

/**
 *  @version $Header: XMLMarshallerEventListener.java 07-oct-2005.21:46:13 pkrogh Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class XMLMarshallerEventListener extends SessionEventAdapter
{
    public static boolean eventTriggered = false;
      /**
     * PUBLIC:
     * This Event is raised after the session logs in.
     */
    public void postLogin(SessionEvent event) {
      eventTriggered = true;
    }
}
