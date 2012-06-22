/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/13/2010-2.1 Michael O'Brien 
 *       - 312503: JPA 2.0 CacheImpl behaviour change when recurse flag=false
 *                      We only invalidate the subtree from the class parameter down when the recurse flag=false
 *                      Previously only the class itself was invalidated
 *                      The behaviour when the recurse flag is true is unaffected - the entire rooted (above) tree is invalidated
 *                      Model must be extended to have subclasses of Small/LargeProject 
 *                      to verify semi-recursive functionality surrounding the equals() to isAssignableFrom() change
 *     
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
 * Model
 * Project (Abstract)
 *    +----SmallProject
 *    +-----LargeProject
 *
 * @author Guy Pelletier
 * @version 2.0 Jan 25/05
 */
public class InvalidateClassRecurseOptionTest extends AutoVerifyTestCase {
    private Session m_session;
    private Vector m_largeProjects, m_smallProjects;
    // true(default)=invalidate entire rooted tree, false=invalidate only subtree
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
            // verify entire rooted tree was not invalidated (only from the current class down)
            if (!m_session.getIdentityMapAccessor().isValid(m_largeProjects.firstElement())) {
                throw new TestErrorException("A large project was invalidated when only small projects should have been");
            }
            // verify inheriting subclasses of the invalidated class (subtree) was also deleted
        }

        if (m_session.getIdentityMapAccessor().isValid(m_smallProjects.firstElement())) {
            throw new TestErrorException("A small project was not invalidated");
        }
    }
}
