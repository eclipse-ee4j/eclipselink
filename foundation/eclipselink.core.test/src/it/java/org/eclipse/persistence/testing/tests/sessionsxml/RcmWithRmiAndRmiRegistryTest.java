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
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.TransportManager;
import org.eclipse.persistence.testing.framework.TestErrorException;


public class RcmWithRmiAndRmiRegistryTest extends RcmBasicTest {
    public RcmWithRmiAndRmiRegistryTest() {
        sessionsXmlFileName = "org/eclipse/persistence/testing/models/sessionsxml/sessions_rcm_rmi_registry.xml";
    }

    public void verify() {
        if ((((AbstractSession)loadedSession).getCommandManager().getTransportManager()).getNamingServiceType() != TransportManager.REGISTRY_NAMING_SERVICE) {
            throw new TestErrorException("The RCM transport does not use rmi registry naming service");
        }

        if (!((AbstractSession)loadedSession).getCommandManager().getUrl().equals("new_rmi_registry_url")) {
            throw new TestErrorException("The url of rmi registry naming does not match 'new_rmi_registry_url'");
        }
    }
}
