/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation.
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import junit.framework.*;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist.CascadePersistTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist.EntityX;
import org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist.EntityY;
import org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist.EntityZ;

public class CascadePersistJUnitTestSuite extends JUnitTestCase {

    public CascadePersistJUnitTestSuite() {
        super();
    }

    public CascadePersistJUnitTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CascadePersistJUnitTestSuite");
        suite.addTest(new CascadePersistJUnitTestSuite("testSetup"));
        suite.addTest(new CascadePersistJUnitTestSuite("testCascadePersistForUnregisteredObjects"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow
     * execution in the server.
     */
    public void testSetup() {
        new CascadePersistTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    public void testCascadePersistForUnregisteredObjects(){
        List<Throwable> errorList = new ArrayList<Throwable>();
        for (int i = 0; i < 10; i++) {
            try {
                persistObjects(i);
            } catch (Throwable t) {
                errorList.add(t);
            }
        }
        
        String errorMsg = new String();
        if (!errorList.isEmpty()) {
            errorMsg = ((Throwable)errorList.get(0)).getMessage();
        }
          
        Assert.assertTrue(errorList.size() + " tests failed out of 10 tests with exception " + errorMsg, errorList.size() == 0);
    }

    private void persistObjects(int id) throws Exception {
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            EntityX x = new EntityX();
            x.setId(id);
            x.setXname("Bob" + id);
            
            EntityY y = new EntityY();
            y.setId(100 + id);
            y.setYname("Smith" + id);
            
            EntityZ z = new EntityZ();
            z.setId(1000 + id);
            z.setZname("Robin" + id);
            
            // Register just z and x objects.
            em.persist(z);
            em.persist(x);
    
            // Set values as unregistered object y to reproduce the issue.
            x.setEntityY(y);
            y.setEntityX(x);
            z.addYToList(y);
            y.setYzEntityRelation(z);
            
            em.flush();
        } finally {
            rollbackTransaction(em);
            if (em != null && em.isOpen()) {
                closeEntityManager(em);
            }
        }
    }   
}
