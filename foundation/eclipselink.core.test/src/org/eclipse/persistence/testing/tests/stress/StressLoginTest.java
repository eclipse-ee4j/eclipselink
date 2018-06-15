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
package org.eclipse.persistence.testing.tests.stress;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;

/**
 * Test login many times.
 */

public class StressLoginTest extends AutoVerifyTestCase {
    public int stressLevel;
public StressLoginTest(int stressLevel)
{
    this.stressLevel = stressLevel;
}
public void reset( )
{
    ((DatabaseSession) getSession()).logout();
    ((DatabaseSession) getSession()).login();
}
public void test( )
{
    Vector sessions =  new Vector();

    try {
        for (int i = 0; i < stressLevel; i++) {
            Session session = new Project(getSession().getDatasourceLogin().clone()).createDatabaseSession();
            ((DatabaseSession) session).login();
            sessions.addElement(session);
        }
        getSession().readObject(Address.class);
    } finally {
        for (Enumeration sessionEnum = sessions.elements(); sessionEnum.hasMoreElements(); ) {
            ((DatabaseSession) sessionEnum.nextElement()).logout();
        }
    }
    getSession().readObject(Address.class);
}
}
