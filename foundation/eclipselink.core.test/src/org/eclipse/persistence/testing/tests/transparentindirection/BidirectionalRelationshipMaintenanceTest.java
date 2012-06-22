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
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.transparentindirection.Player;
import org.eclipse.persistence.testing.models.transparentindirection.Team;

/**
 * BUG - 4082205 Bi-directional relationship maintenance does not work with
 * transparent maps.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date March 21, 2005
 */
public class BidirectionalRelationshipMaintenanceTest extends AutoVerifyTestCase {
    boolean m_exceptionCaught;

    public BidirectionalRelationshipMaintenanceTest() {
        setDescription("Test bidrectional relationship maintenance on an indirect transparent map.");
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void setup() throws Exception {
        m_exceptionCaught = false;

        beginTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Team team = (Team)uow.registerObject(new Team());
        Player player = (Player)uow.registerObject(new Player());
        player.setTeam(team);

        try {
            uow.commit();
        } catch (Exception e) {
            m_exceptionCaught = true;
        }
    }

    public void verify() throws Exception {
        if (m_exceptionCaught) {
            throw new TestErrorException("Relationship maintenance failed.");
        }
    }
}
