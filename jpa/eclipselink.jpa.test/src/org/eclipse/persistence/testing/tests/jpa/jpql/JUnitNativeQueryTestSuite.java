/*******************************************************************************
 * Copyright (c) 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Chris Delahunt - testing for native queries 
 *       - Bug299926: Case insensitive table / column matching with native SQL queries  
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.jpql;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.JoinedAttributeTestHelper;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.GoldBuyer;
import org.eclipse.persistence.testing.models.jpa.advanced.NativeQueryTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.tests.jpa.advanced.NamedNativeQueryJUnitTest;

/**
 * @author cdelahun
 *
 */
public class JUnitNativeQueryTestSuite  extends JUnitTestCase {
    protected String PUName = "MulitPU-1";
    
    public JUnitNativeQueryTestSuite() {
        super();
    }
    
    public JUnitNativeQueryTestSuite(String name) {
        super(name);
        setPuName(PUName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("NativeQueryTestSuite");
        suite.addTest(new JUnitNativeQueryTestSuite("testSetup"));
        //Removed for 299926
        suite.addTest(new JUnitNativeQueryTestSuite("testNativeQueryWithMixedCaseFields"));
        suite.addTest(new JUnitNativeQueryTestSuite("testNativeQueryHint"));
        suite.addTest(new JUnitNativeQueryTestSuite("testCaseSensitivity_GoldBuyer1"));

        //This suite uses the same setup as this one
        TestSuite NamedNativeQuerySuite = new TestSuite();
        suite.addTest(NamedNativeQueryJUnitTest.addNamedNativeQueryTests(NamedNativeQuerySuite));

        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = JUnitTestCase.getServerSession(PUName);
        //create a new EmployeePopulator
        EmployeePopulator employeePopulator = new EmployeePopulator();
        new NativeQueryTableCreator().replaceTables(session);
        //Populate the tables
        employeePopulator.buildExamples();
        //Persist the examples in the database
        employeePopulator.persistExample(session);
        clearCache(PUName);
    }

    /* Removed for 299926 when the default changed to false.
     * force results to be returned in camel case - any case that is different from what is defined in the Entity's field definitions
     */
    public void testNativeQueryWithMixedCaseFields() {
        EntityManager em = createEntityManager(PUName);
        try {
            Address expectedAddress = (Address)em.createQuery("select a from Address a").getResultList().get(0);
            Address returnedAddress = null;
            ServerSession session = JUnitTestCase.getServerSession();
            String delimiter = session.getPlatform().getStartDelimiter();
            Query q = em.createNativeQuery(
                        "Select ADDRESS_ID as " + delimiter + "aDdReSs_iD" +delimiter +
                        ", P_CODE as "+ delimiter + "P_cOdE" +delimiter +
                        ", STREET as "+ delimiter + "StReeT" +delimiter +
                        ", PROVINCE as "+ delimiter + "PrOvInCe" +delimiter +
                        ", TYPE as " + delimiter + "TyPe" +delimiter +
                        ", CITY as " + delimiter + "CiTy" +delimiter +
                        ", COUNTRY as " + delimiter + "CoUnTrY" +delimiter +
                        ", VERSION as " + delimiter + "VeRsIoN" +delimiter +
                        " from CMP3_ADDRESS where ADDRESS_ID = "+expectedAddress.getID()
                        , Address.class
                        );

            returnedAddress = (Address)q.getSingleResult();
            this.assertNotNull("no address returned",returnedAddress);
            assertTrue("returned address does not match the expected address", session.compareObjects(returnedAddress, expectedAddress));

            //this query uses a resultsetmapping that looks for columns as camel case.  
            q = em.createNativeQuery(
                        "Select ADDRESS_ID"/* as " + delimiter + "aDrEsS_iD" +delimiter*/ +
                        ", P_CODE"/* as "+ delimiter + "P_cOdE" +delimiter*/ +
                        ", STREET"/* as "+ delimiter + "StReeT" +delimiter*/ +
                        ", PROVINCE"/* as "+ delimiter + "PrOvInCe" +delimiter*/ +
                        ", TYPE"/* as " + delimiter + "TyPe" +delimiter*/ +
                        ", CITY"/* as " + delimiter + "CiTy" +delimiter*/ +
                        ", COUNTRY"/* as " + delimiter + "CoUnTy" +delimiter*/ +
                        ", VERSION"/* as " + delimiter + "VeRsIoN" +delimiter*/ +
                        " from CMP3_ADDRESS where ADDRESS_ID = "+expectedAddress.getID()
                        , "address-case-sensitive-map"
                        );

            returnedAddress = (Address)q.getSingleResult();

            this.assertNotNull("no address returned",returnedAddress);
            assertTrue("returned address does not match the expected address", session.compareObjects(returnedAddress, expectedAddress));
        } finally {
            closeEntityManager(em);
        }
    }

    // Test that hints work for native queries.
    public void testNativeQueryHint() {
        EntityManager em = createEntityManager(PUName);
        Query query = em.createNativeQuery("Select * from CMP3_ADDRESS");
        query.setHint("somehint", "whatever");
        query.setHint(QueryHints.BIND_PARAMETERS, "false");
        query.getResultList();
        clearCache(PUName);
        closeEntityManager(em);
    }
    
    /**tests the SQL used for a JPQL query against native sql query.  
     * It tests that the "Descrip" field name defined is sent to the database in JPQL (Will fail on case sensitive database if not)
     * And tests that the "Descrip" field is found and populated into GoldBuyer when using JPQL
     * 
     * 
     */
    public void testCaseSensitivity_GoldBuyer1() {
        EntityManager em = createEntityManager(PUName);
        ServerSession session = JUnitTestCase.getServerSession(PUName);

        Query jpaquery = em.createQuery("Select b from GoldBuyer b");
        List<GoldBuyer> jpaQueryResults = jpaquery.getResultList();
        closeEntityManager(em);
        // clear cache
        clearCache(PUName);

        em = createEntityManager(PUName);
        Query query = em.createNativeQuery("Select * from CMP3_BUYER WHERE (DTYPE = 'GoldBuyer')", GoldBuyer.class);

        List<GoldBuyer> nativeQueryResults = query.getResultList();
        closeEntityManager(em);
        clearCache(PUName);

        if (nativeQueryResults.isEmpty() || 
                    nativeQueryResults.get(0).getDescription() == null || nativeQueryResults.get(0).getDescription().isEmpty()) {
            fail("testCaseSensitivity_GoldBuyer1 failed to return ");
        }
        String errorMsg = JoinedAttributeTestHelper.compareCollections(jpaQueryResults, nativeQueryResults, session.getClassDescriptor(GoldBuyer.class), session);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testCaseSensitivity2() {
        EntityManager em = createEntityManager(PUName);
        Query query = em.createNativeQuery("Select * from CMP3_ADDRESS");
        query.setHint("somehint", "whatever");
        query.setHint(QueryHints.BIND_PARAMETERS, "false");
        query.getResultList();
        closeEntityManager(em);
    }
}
