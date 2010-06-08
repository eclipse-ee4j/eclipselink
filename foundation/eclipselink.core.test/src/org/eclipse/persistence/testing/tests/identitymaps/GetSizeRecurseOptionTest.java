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
package org.eclipse.persistence.testing.tests.identitymaps;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.internal.identitymaps.FullIdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * Tests the recurse option on the getSize from IdentityMapAccessor.
 *
 * @author Guy Pelletier
 * @version 1.0 May 20/04
 */
public class GetSizeRecurseOptionTest extends TestCase {
    private Session m_session;
    private int m_fullSize;
    private int m_smallSize;

    public GetSizeRecurseOptionTest() {
    }

    public void reset() {
    }

    protected void setup() {
        m_session = getSession();

        // Get the projects into the identity map
        m_session.readAllObjects(LargeProject.class);
        m_session.readAllObjects(SmallProject.class);
    }

    public void test() {
        FullIdentityMap identityMap = (FullIdentityMap)((AbstractSession)m_session).getIdentityMapAccessorInstance().getIdentityMap(Project.class);
        m_fullSize = identityMap.getSize();
        m_smallSize = identityMap.getSize(SmallProject.class, false);
    }

    protected void verify() {
        if (m_smallSize >= m_fullSize) {
            throw new TestErrorException("The number of small projects exceeded or equalled the number of projects as a whole");
        }
    }
}
