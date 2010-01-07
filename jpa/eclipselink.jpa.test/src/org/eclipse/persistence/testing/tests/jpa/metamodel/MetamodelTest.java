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
package org.eclipse.persistence.testing.tests.jpa.metamodel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

/**
 * These tests verify the JPA 2.0 Metamodel API.
 * The framework is as follows:
 *   - initialize persistence unit
 *   - start a transaction
 *   - persist some entities to test
 *   - verify metamodel
 *   - delete test entities created above (to reset the database)
 *   - close persistence unit
 *
 */
public class MetamodelTest extends JUnitTestCase {
    
    public static final String PERSISTENCE_UNIT_NAME = "metamodel1";
    /** Cache the EMF on the test suite - for performance - to save 20 sec per test case */
    public EntityManagerFactory entityManagerFactory = null;
    /** Create tables only once - for performance */
    public boolean isDatabaseSchemaCreated = false;
    
    public MetamodelTest() {
        super();
    }

    public MetamodelTest(String name) {
        super(name);
    }

    public void setUp() {
        setUp(false);
    }

    public void setUp(boolean overrideEMFCachingForTesting) {
        super.setUp();
        initialize();
        ServerSession session = JUnitTestCase.getServerSession(PERSISTENCE_UNIT_NAME);
        //if(!isDatabaseSchemaCreated | overrideEMFCachingForTesting) {
        if(null == entityManagerFactory || (null != entityManagerFactory && !entityManagerFactory.isOpen())) {
            new MetamodelTableCreator().replaceTables(session);
            isDatabaseSchemaCreated = true;
        }
    }
    
    
    public EntityManagerFactory initialize() {
        return initialize(false);
    }
    
    public void resetEntityManagerFactory() {
        try {
            if(null != entityManagerFactory && entityManagerFactory.isOpen()) {
                entityManagerFactory.close();
            }
        } catch (Exception e) {
            e.printStackTrace();            
        } finally {
            entityManagerFactory = null;
        }
        
    }
    
    public EntityManagerFactory initialize(boolean overrideEMFCachingForTesting) {
        try {
            if(null == entityManagerFactory || overrideEMFCachingForTesting || !entityManagerFactory.isOpen()) {
                entityManagerFactory = getEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entityManagerFactory;
    }
    
    public void cleanup(EntityManager em) {
        // close JPA
        try {
            if(null != em && em.isOpen()) {
                em.close();
                //emf.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
