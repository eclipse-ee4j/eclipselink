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
 *     03/08/2010 Andrei Ilitchev 
 *       Bug 300512 - Add FUNCTION support to extended JPQL
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.customjpql;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.*;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.*;

public class CustomJPQLTestSuite extends JUnitTestCase {

    public CustomJPQLTestSuite() {
        super();
    }
    
    public CustomJPQLTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CustomJPQLTestSuite");
        
        suite.addTest(new CustomJPQLTestSuite("testSetup"));
        suite.addTest(new CustomJPQLTestSuite("customFunctionNVLTest"));
        
        return suite;
    }

    public void testSetup() {
        new AdvancedTableCreator().replaceTables(JUnitTestCase.getServerSession());

        // Force uppercase for Postgres.
        if (getServerSession().getPlatform().isPostgreSQL()) {
            getServerSession().getLogin().setShouldForceFieldNamesToUpperCase(true);
        }
    }

    // Bug 300512 - Add FUNCTION support to extended JPQL 
    public void customFunctionNVLTest() {
        EntityManager em = createEntityManager();
        String[] jpqlString = {
                "SELECT FUNC('NVL', e.firstName, 'NoFirstName'), func('NVL', e.lastName, 'NoLastName') FROM Employee e",
                "SELECT FUNC('NVL', e.firstName, :param0), func('NVL', :param1, e.lastName) FROM Employee e",
                "SELECT FUNC('NVL', e.firstName, SUBSTRING(e.lastName, 1)), func('NVL', e.lastName, CONCAT(e.lastName, ' no name')) FROM Employee e",
                "SELECT CONCAT(FUNC('NVL', e.firstName, 'NoFirstName'), func('NVL', e.lastName, 'NoLastName')) FROM Employee e",

                "SELECT e.id FROM Employee e WHERE FUNC('NVL', e.firstName, 'NoFirstName') = func('NVL', e.lastName, 'NoLastName')",
                "SELECT e.id FROM Employee e WHERE FUNC('NVL', e.firstName, :param0) = func('NVL', :param1, e.lastName)",
                "SELECT e.id FROM Employee e WHERE FUNC('NVL', e.firstName, SUBSTRING(e.lastName, 1)) = func('NVL', e.lastName, CONCAT(e.lastName, ' no name'))",
                "SELECT e.id FROM Employee e WHERE CONCAT(FUNC('NVL', e.firstName, 'NoFirstName'), func('NVL', e.lastName, 'NoLastName')) = 'NoFirstNameNoLastName'"
        };
        
        String errorMsg = "";
        String jpql = null;
        Query query;
        for(int i=0; i < jpqlString.length; i++) {
            try {
                jpql = jpqlString[i];
                query = em.createQuery(jpql);
                if(i == 1 || i == 5) {
                    query.setParameter("param0", "Blah0");
                    query.setParameter("param1", "Blah1");
                }
                query.getResultList();
            } catch (Exception ex) {
                ex.printStackTrace();
                errorMsg += '\t' + jpql + " - "+ex+'\n';
            }
        }
        
        closeEntityManager(em);
        
        if(errorMsg.length() > 0) {
            errorMsg = "Failed:\n" + errorMsg;
            fail(errorMsg);
        }
    }   
}
