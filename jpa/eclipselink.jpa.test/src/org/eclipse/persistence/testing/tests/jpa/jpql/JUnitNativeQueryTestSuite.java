/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.tests.jpa.advanced.NamedNativeQueryJUnitTest;

/**
 * @author cdelahun
 *
 */
public class JUnitNativeQueryTestSuite  extends JUnitTestCase {
    
    public JUnitNativeQueryTestSuite() {
        super();
    }
    
    public JUnitNativeQueryTestSuite(String name) {
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("NativeQueryTestSuite");
        suite.addTest(new JUnitNativeQueryTestSuite("testSetup"));
        suite.addTest(new JUnitNativeQueryTestSuite("testNativeQuery"));
        suite.addTest(new JUnitNativeQueryTestSuite("testNativeQueryHint"));
        suite.addTest(NamedNativeQueryJUnitTest.suite());
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = JUnitTestCase.getServerSession();
        //create a new EmployeePopulator
        EmployeePopulator employeePopulator = new EmployeePopulator();
        new AdvancedTableCreator().replaceTables(session);
        //Populate the tables
        employeePopulator.buildExamples();
        //Persist the examples in the database
        employeePopulator.persistExample(session);
        clearCache();
    }
    
    //force results to be returned in camel case - any case that is different from what is defined in the Entity's field definitions
    public void testNativeQuery() {
        Address expectedAddress = (Address)createEntityManager().createQuery("select a from Address a").getResultList().get(0);
        Address returnedAddress = null;
        ServerSession session = JUnitTestCase.getServerSession();
        String delimiter = session.getPlatform().getStartDelimiter();
        Query q = createEntityManager().createNativeQuery(
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
        q = createEntityManager().createNativeQuery(
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
    }
    
    // Test that hints work for native queries.
    public void testNativeQueryHint() {
        EntityManager em = createEntityManager();
        Query query = em.createNativeQuery("Select * from CMP3_ADDRESS");
        query.setHint("somehint", "whatever");
        query.setHint(QueryHints.BIND_PARAMETERS, "false");
        query.getResultList();
        closeEntityManager(em);
    }
}
