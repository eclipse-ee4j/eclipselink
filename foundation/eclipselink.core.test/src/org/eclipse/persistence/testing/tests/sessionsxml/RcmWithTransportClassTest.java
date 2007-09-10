/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.framework.TestErrorException;


public class RcmWithTransportClassTest extends RcmBasicTest {
    public RcmWithTransportClassTest() {
        sessionsXmlFileName = "org/eclipse/persistence/testing/models/sessionsxml/sessions_rcm_transport_class.xml";
    }

    public void verify() {
        if (!(((AbstractSession)loadedSession).getCommandManager().getTransportManager() instanceof CustomTransportManager)) {
            throw new TestErrorException("The RCM Transport is not set to use the CustomTransportManager class");
        }
    }

}
