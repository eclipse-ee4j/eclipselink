/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     12/15/2010-2.2 Guy Pelletier 
 *       - 330755: Nested embeddables can't be used as embedded ids
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.complexaggregate;

import javax.persistence.EntityManager;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.models.jpa.advanced.Man;
import org.eclipse.persistence.testing.models.jpa.advanced.PartnerLink;
import org.eclipse.persistence.testing.models.jpa.advanced.PartnerLinkPK;
import org.eclipse.persistence.testing.models.jpa.advanced.Woman;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * BUG 4401389 - EJB30: EMBEDDABLES CAN NOT BE NESTED
 *
 * @author Guy Pelletier
 * @date June 9, 2005
 * @version 1.0
 */
public class NestedAggregatePrimaryKeyTest extends EntityContainerTestBase {
    protected Session m_session;
    protected boolean m_reset = false;    // reset gets called twice on error
    
    protected Body m_refreshedBody;
    protected Exception m_testException;
        
    public NestedAggregatePrimaryKeyTest() {
        setDescription("Tests nested aggregates used as the primary key class.");
    }
    
    public void setup () {
        super.setup();
        m_reset = true;
        m_refreshedBody = null;
        m_testException = null;
        m_session = ((EntityManagerImpl) getEntityManager()).getActiveSession();
        m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test() throws Exception {
        Torso torso;
        
        try {
            Body body = new Body();
            torso = new Torso();
            Heart heart = new Heart();
            heart.setSize(8);
            torso.setHeart(heart);
            body.setTorso(torso);
            
            beginTransaction();
            getEntityManager().persist(body);
            commitTransaction();
        } catch (Exception e) {
            throw new TestErrorException("Exception caught when persisting the new body: " + e.getMessage());
        } finally {
            getEntityManager().close();
        }
        
        // Try to read the body back, clear the cache first.
        try {
            getEntityManager().clear();
            m_session = ((EntityManagerImpl) getEntityManager()).getServerSession();
            m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
            m_refreshedBody = getEntityManager().find(Body.class, torso);
        } catch (Exception e) {
            m_testException = e;
        }
    }
    
    public void reset () {
        if (m_reset) {
            m_reset = false;
        }
    }
    
    public void verify() {
        if (m_testException != null) {
            throw new TestErrorException("Exception caught reading back the persisted body: " + m_testException);
        }

        if (m_refreshedBody == null) {
            throw new TestErrorException("Unable to read back the persisted body");
        }

        ClassDescriptor descriptor = ((EntityManagerImpl) getEntityManager()).getServerSession().getClassDescriptor(Body.class);
        Object pks = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(m_refreshedBody, (AbstractSession) m_session);
        Torso createdTorso = (Torso) descriptor.getCMPPolicy().createPrimaryKeyInstanceFromId(pks, (AbstractSession) m_session);        
        assertTrue("PK's do not match.", m_refreshedBody.getTorso().equals(createdTorso));

    }
}

