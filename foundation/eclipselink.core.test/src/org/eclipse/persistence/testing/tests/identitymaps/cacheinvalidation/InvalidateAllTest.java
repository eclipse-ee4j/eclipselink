/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
