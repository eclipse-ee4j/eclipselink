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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import java.util.Vector;
import java.util.Enumeration;

import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Tests the invalidate all from IdentityMapAccessor.
 *
 * @author Guy Pelletier
 * @version 1.0 Jul 14/05
 */
public class InvalidateAllTest extends AutoVerifyTestCase {
    private Session m_session;
    private Vector m_largeProjects, m_smallProjects;

    public InvalidateAllTest() {
    }

    public void reset() {
        m_session.getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }

    protected void setup() {
        m_session = getSession();
        beginTransaction();
        m_session.getIdentityMapAccessor().initializeIdentityMaps();

        m_largeProjects = m_session.readAllObjects(LargeProject.class);
        m_smallProjects = m_session.readAllObjects(SmallProject.class);
    }

    public void test() {
        ((IdentityMapAccessor)m_session.getIdentityMapAccessor()).invalidateAll();
    }

    protected void verify() {
        Enumeration e = m_largeProjects.elements();

        while (e.hasMoreElements()) {
            if (m_session.getIdentityMapAccessor().isValid(e.nextElement())) {
                throw new TestErrorException("A large project was not invalidated.");
            }
        }

        e = m_smallProjects.elements();

        while (e.hasMoreElements()) {
            if (m_session.getIdentityMapAccessor().isValid(e.nextElement())) {
                throw new TestErrorException("A small project was not invalidated");
            }
        }
    }
}
