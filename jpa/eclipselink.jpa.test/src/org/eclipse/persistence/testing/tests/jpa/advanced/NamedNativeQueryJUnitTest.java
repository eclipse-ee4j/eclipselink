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


package org.eclipse.persistence.testing.tests.jpa.advanced;

import java.util.List;
import javax.persistence.EntityManager;
import junit.framework.*;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;

public class NamedNativeQueryJUnitTest extends JUnitTestCase {
    protected String PUName = "MulitPU-1";

    public NamedNativeQueryJUnitTest() {
        super();
    }

    public NamedNativeQueryJUnitTest(String name) {
        super(name);
        setPuName(PUName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new NamedNativeQueryJUnitTest("testSetup"));
        return addNamedNativeQueryTests(suite);
    }

    /**
     * Adds test, similar to suite() but without adding a setup.  used from JUnitNativeQueryTestSuite 
     * @param suite
     * @return
     */
    public static Test addNamedNativeQueryTests(TestSuite suite){
        suite.setName("NamedNativeQueryJUnitTest");
        suite.addTest(new NamedNativeQueryJUnitTest("testNamedNativeQuery"));
        suite.addTest(new NamedNativeQueryJUnitTest("selectNamedNativeQueryWithPositionalParameterTest"));
        suite.addTest(new NamedNativeQueryJUnitTest("selectNativeQueryWithPositionalParameterTest"));
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(getServerSession(PUName));
        clearCache(PUName);
    }

    public void testNamedNativeQuery() {
        EntityManager em = createEntityManager(PUName);
        try {
            em.createNamedQuery("findAllSQLAddresses").getResultList();
            //if there is an exception, let it go through so we can see what it was
        } finally {
            if (isTransactionActive(em)) {
                closeEntityManager(em);
            }
        }
    }

    public void selectNamedNativeQueryWithPositionalParameterTest() {
        EntityManager em = createEntityManager(PUName);

        List results_QuestionMark_Number = null;
        List results_QuestionMark = null;
        javax.persistence.Query query;
        String errorMsg = "";

        boolean shouldCompareResults = true;
        try {
            query = em.createNamedQuery("findAllSQLAddressesByCity_QuestionMark_Number");
            query.setParameter(1, "Ottawa");

            results_QuestionMark_Number = query.getResultList();
        } catch (Exception e) {
            errorMsg = errorMsg + "findAllSQLAddressesByCity_QuestionMark_Number: " + e.getMessage() + "\n";
            shouldCompareResults = false;
        }
        try {
            query = em.createNamedQuery("findAllSQLAddressesByCity_QuestionMark");
            query.setParameter(1, "Ottawa");

            results_QuestionMark = query.getResultList();
        } catch (Exception e) {
            errorMsg = errorMsg + "findAllSQLAddressesByCity_QuestionMark: " + e.getMessage() + "\n";
            shouldCompareResults = false;
        }
        if (shouldCompareResults) {
            if (results_QuestionMark_Number.size() != results_QuestionMark.size()) {
                errorMsg = errorMsg + ("findAllSQLAddressesByCity_QuestionMark_Number and findAllSQLAddressesByCity_QuestionMark produced non-equal results");
            }
        }

        shouldCompareResults = true;
        try {
            query = em.createNamedQuery("findAllSQLAddressesByCityAndCountry_QuestionMark_Number");
            query.setParameter(1, "Ottawa");
            query.setParameter(2, "Canada");

            results_QuestionMark_Number = query.getResultList();
        } catch (Exception e) {
            errorMsg = errorMsg + "findAllSQLAddressesByCityAndCountry_QuestionMark_Number: " + e.getMessage() + "\n";
            shouldCompareResults = false;
        }
        try {
            query = em.createNamedQuery("findAllSQLAddressesByCityAndCountry_QuestionMark");
            query.setParameter(1, "Ottawa");
            query.setParameter(2, "Canada");

            results_QuestionMark = query.getResultList();
        } catch (Exception e) {
            errorMsg = errorMsg + "findAllSQLAddressesByCityAndCountry_QuestionMark: " + e.getMessage();
            shouldCompareResults = false;
        }
        if (shouldCompareResults) {
            if (results_QuestionMark_Number.size() != results_QuestionMark.size()) {
                errorMsg = errorMsg + ("findAllSQLAddressesByCityAndCountry_QuestionMark_Number and findAllSQLAddressesByCityAndCountry_QuestionMark produced non-equal results");
            }
        }

        if (errorMsg.length() > 0) {
            Assert.fail(errorMsg);
        }
        closeEntityManager(em);
    }

    public void selectNativeQueryWithPositionalParameterTest() {
        EntityManager em = createEntityManager(PUName);

        List results_QuestionMark_Number = null;
        List results_QuestionMark = null;
        javax.persistence.Query query;
        String errorMsg = "";

        boolean shouldCompareResults = true;
        try {
            query = em.createNativeQuery("select * from CMP3_ADDRESS where CITY=?1", Address.class);
            query.setParameter(1, "Ottawa");

            results_QuestionMark_Number = query.getResultList();
        } catch (Exception e) {
            errorMsg = errorMsg + "findAllSQLAddressesByCity_QuestionMark_Number: " + e.getMessage() + "\n";
            shouldCompareResults = false;
        }
        try {
            query = em.createNativeQuery("select * from CMP3_ADDRESS where CITY=?", Address.class);
            query.setParameter(1, "Ottawa");

            results_QuestionMark = query.getResultList();
        } catch (Exception e) {
            errorMsg = errorMsg + "findAllSQLAddressesByCity_QuestionMark: " + e.getMessage() + "\n";
            shouldCompareResults = false;
        }
        if (shouldCompareResults) {
            if (results_QuestionMark_Number.size() != results_QuestionMark.size()) {
                errorMsg = errorMsg + ("findAllSQLAddressesByCity_QuestionMark_Number and findAllSQLAddressesByCity_QuestionMark produced non-equal results");
            }
        }

        shouldCompareResults = true;
        try {
            query = em.createNativeQuery("select * from CMP3_ADDRESS where CITY=?1 and COUNTRY=?2", Address.class);
            query.setParameter(1, "Ottawa");
            query.setParameter(2, "Canada");

            results_QuestionMark_Number = query.getResultList();
        } catch (Exception e) {
            errorMsg = errorMsg + "findAllSQLAddressesByCityAndCountry_QuestionMark_Number: " + e.getMessage() + "\n";
            shouldCompareResults = false;
        }
        try {
            query = em.createNativeQuery("select * from CMP3_ADDRESS where CITY=? and COUNTRY=?", Address.class);
            query.setParameter(1, "Ottawa");
            query.setParameter(2, "Canada");

            results_QuestionMark = query.getResultList();
        } catch (Exception e) {
            errorMsg = errorMsg + "findAllSQLAddressesByCityAndCountry_QuestionMark: " + e.getMessage();
            shouldCompareResults = false;
        }
        if (shouldCompareResults) {
            if (results_QuestionMark_Number.size() != results_QuestionMark.size()) {
                errorMsg = errorMsg + ("findAllSQLAddressesByCityAndCountry_QuestionMark_Number and findAllSQLAddressesByCityAndCountry_QuestionMark produced non-equal results");
            }
        }

        if (errorMsg.length() > 0) {
            Assert.fail(errorMsg);
        }
        closeEntityManager(em);
    }
}
