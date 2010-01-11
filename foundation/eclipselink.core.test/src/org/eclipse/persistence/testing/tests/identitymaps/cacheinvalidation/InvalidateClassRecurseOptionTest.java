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

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Tests the recurse option on the invalidateClass from IdentityMapAccessor.
 * Two scenarios: recurse equals true or false. Edwin Tang
 *
 * @author Guy Pelletier
 * @version 2.0 Jan 25/05
 */
public class InvalidateClassRecurseOptionTest extends AutoVerifyTestCase {
    private Session m_session;
    private Vector m_largeProjects, m_smallProjects;
    private boolean recurse;

    public InvalidateClassRecurseOptionTest(boolean recurse) {
        this.recurse = recurse;
        this.setName(this.getName() + "(" + recurse + ")");
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
        m_session.getIdentityMapAccessor().invalidateClass(SmallProject.class, recurse);
    }

    protected void verify() {
        // Just check the first project of the vector
        if (recurse) {
            if (m_session.getIdentityMapAccessor().isValid(m_largeProjects.firstElement())) {
                throw new TestErrorException("A large project was not invalidated recursively");
            }
        } else {
            if (!m_session.getIdentityMapAccessor().isValid(m_largeProjects.firstElement())) {
                throw new TestErrorException("A large project was invalidated when only small projects should have been");
            }
        }

        if (m_session.getIdentityMapAccessor().isValid(m_smallProjects.firstElement())) {
            throw new TestErrorException("A small project was not invalidated");
        }
    }
}
