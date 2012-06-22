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
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import java.util.Vector;

import junit.framework.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.JoinedAttributeTestHelper;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.inheritance.*;
import org.eclipse.persistence.queries.ReadAllQuery;
 
public class JoinedAttributeInheritanceJunitTest extends JUnitTestCase {
        
    static protected Class[] classes = {Company.class, Vehicle.class};
    static protected Vector[] objectVectors = {null, null};
    
    protected DatabaseSession dbSession;

    public JoinedAttributeInheritanceJunitTest() {
        super();
    }
    
    public JoinedAttributeInheritanceJunitTest(String name) {
        super(name);
    }
    
    // This method is designed to make sure that the tests always work in the same environment:
    // db has all the objects produced by populate method - and no other objects of the relevant classes.
    // In order to enforce that the first test populates the db and caches the objects in static collections,
    // the following test reads all the objects from the db, compares them with the cached ones - if they are the
    // same (the case if the tests run directly one after another) then no population occurs.
    public void setUp() {
        super.setUp();
        dbSessionClearCache();
        if(!compare()) {
            clear();
            populate();
        }
        dbSessionClearCache();
    }
    
    // the session is cached to avoid extra dealing with SessionManager - 
    // without session caching each test caused at least 5 ClientSessins being acquired / released.
    protected DatabaseSession getDbSession() {
        if(dbSession == null) {
            dbSession = getServerSession();
        }
        return dbSession;
    }
    
    protected UnitOfWork acquireUnitOfWork() {
        return getDbSession().acquireUnitOfWork();   
    }
    
    protected void clear() {
        UnitOfWork uow = acquireUnitOfWork();
        // delete all Vechicles
        uow.executeQuery(new DeleteAllQuery(Vehicle.class));
        // delete all Companies
        uow.executeQuery(new DeleteAllQuery(Company.class));
        uow.commit();
        dbSessionClearCache();
    }
    
    protected void populate() {
        UnitOfWork uow = acquireUnitOfWork();
        uow.registerNewObject(InheritanceModelExamples.companyExample1());
        uow.registerNewObject(InheritanceModelExamples.companyExample2());
        uow.registerNewObject(InheritanceModelExamples.companyExample3());
        uow.commit();
        dbSessionClearCache();
        for(int i=0; i < classes.length; i++) {
            objectVectors[i] = getDbSession().readAllObjects(classes[i]);
        }
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(JoinedAttributeInheritanceJunitTest.class);
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new InheritanceTableCreator().replaceTables(JUnitTestCase.getServerSession());
        clearCache();
    }
    
    public void tearDown() {
        dbSessionClearCache();
        dbSession = null;
        super.tearDown();
    }
        
    public void testVehicleJoinCompany() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Vehicle.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        query.addJoinedAttribute(query.getExpressionBuilder().get("owner"));

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testVehicleJoinCompanyWherePassengerCapacity() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Vehicle.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("passengerCapacity").greaterThan(2));
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        query.addJoinedAttribute(query.getExpressionBuilder().get("owner"));

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testCompanyJoinVehicles() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Company.class);
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        query.addJoinedAttribute(query.getExpressionBuilder().anyOf("vehicles"));

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    public void testCompanyJoinVehiclesWhereNameTOP() {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(Company.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("name").equal("TOP"));
        
        ReadAllQuery controlQuery = (ReadAllQuery)query.clone();
        
        query.addJoinedAttribute(query.getExpressionBuilder().anyOf("vehicles"));

        String errorMsg = executeQueriesAndCompareResults(controlQuery, query);
        if(errorMsg.length() > 0) {
            fail(errorMsg);
        }
    }

    protected String executeQueriesAndCompareResults(ObjectLevelReadQuery controlQuery, ObjectLevelReadQuery queryWithJoins) {
        return JoinedAttributeTestHelper.executeQueriesAndCompareResults(controlQuery, queryWithJoins, (AbstractSession)getDbSession());
    }

    protected boolean compare() {
        for(int i=0; i < classes.length; i++) {
            if(!compare(i)) {
                return false;
            }
        }
        return true;
    }

    protected boolean compare(int i) {
        if(objectVectors[i] == null) {
            return false;
        }
        Vector currentVector = getDbSession().readAllObjects(classes[i]);
        
        if(currentVector.size() != objectVectors[i].size()) {
            return false;
        }
        ClassDescriptor descriptor = getDbSession().getDescriptor(classes[i]);
        for(int j=0; j < currentVector.size(); j++) {
            Object obj1 = objectVectors[i].elementAt(j);
            Object obj2 = currentVector.elementAt(j);
            if(!descriptor.getObjectBuilder().compareObjects(obj1, obj2, (org.eclipse.persistence.internal.sessions.AbstractSession)getDbSession())) {
                return false;
            }
        }
        return true;
    }

    public void dbSessionClearCache() {
        getDbSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
