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
