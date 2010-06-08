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
package org.eclipse.persistence.testing.tests.jpa.relationships;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.jpa.relationships.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/*
 * Tests using the 'getSingleResult' api on a Query object obtained from the
 * EntityManager Tests fixes for bugs 4202835 and 4301674
 * 
 * modified for changes in bug:4628215 (EntityNotFoundException)
 * EntityNotFoundException changed to NoResultException as per new spec
 */
public class GetSingleResultTest extends EntityContainerTestBase {

    // reset gets called twice on error
    protected boolean reset = false;

    // used for verification
    protected Customer returnedCustomer1, returnedCustomer2 = null;
    protected NonUniqueResultException expectedException1 = null;
    protected NoResultException expectedException2 = null;

    protected String searchString = "notAnItemName";

    public Integer[] cusIDs = new Integer[3];

    public GetSingleResultTest() {
    }

    public void setup() {
        super.setup();
        this.reset = true;

        Customer cusClone1 = RelationshipsExamples.customerExample1();
        Customer cusClone2 = RelationshipsExamples.customerExample2();
        try {
            beginTransaction();
            getEntityManager().persist(cusClone1);
            getEntityManager().persist(cusClone2);
            commitTransaction();
        } catch (Exception ex) {
            throw new TestException("Unable to setup Test" + ex);
        }
        ((EntityManagerImpl) getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        cusIDs[0] = cusClone1.getCustomerId();
        cusIDs[1] = cusClone2.getCustomerId();

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void reset() {
        if (reset) {// ensures it is only done once
            try {
                beginTransaction();
                Customer cus1 = getEntityManager().find(Customer.class, cusIDs[0]);
                getEntityManager().remove(cus1);
                Customer cus2 = getEntityManager().find(Customer.class, cusIDs[1]);
                getEntityManager().remove(cus2);
                commitTransaction();
                reset = false;
            } catch (Exception ex) {
                throw new TestException("Unable to reset Test" + ex);
            }
        }
    }

    public void test() {
        try {
            beginTransaction();
            try {
                returnedCustomer1 = (Customer) getEntityManager().createNamedQuery("findAllCustomers").getSingleResult();
            } catch (NonUniqueResultException exceptionExpected1) {
                expectedException1 = exceptionExpected1;
            }
            try {
                // should be no Items to find, which should cause an
                // NoResultException
                Query query1 = getEntityManager().createNamedQuery("findAllItemsByName");
                Item item = (Item) query1.setParameter(1, searchString).getSingleResult();
                item.toString();
            } catch (NoResultException exceptionExpected2) {
                expectedException2 = exceptionExpected2;
            }
            // bug 4301674 test
            EJBQueryImpl query2 = (EJBQueryImpl) getEntityManager().createNamedQuery("findAllCustomers");
            ReadAllQuery readAllQuery = new ReadAllQuery(Customer.class);
            MapContainerPolicy mapContainerPolicy = new MapContainerPolicy();
            mapContainerPolicy.setContainerClass(HashMap.class);
            mapContainerPolicy.setKeyName("hashCode");
            readAllQuery.setContainerPolicy(mapContainerPolicy);
            query2.setDatabaseQuery(readAllQuery);
            Map result = (Map) query2.getSingleResult();
            result.toString();

            // check for single result found.
            Query query3 = getEntityManager().createQuery("SELECT OBJECT(thecust) FROM Customer thecust WHERE thecust.customerId = :id");
            returnedCustomer1 = (Customer) query3.setParameter("id", cusIDs[0]).getSingleResult();

            // check for single result using a ReadObjectQuery (tests previous
            // fix for 4202835)
            EJBQueryImpl query4 = (EJBQueryImpl) getEntityManager().createQuery("SELECT OBJECT(thecust) FROM Customer thecust WHERE thecust.customerId = :id");
            query4.setParameter("id", cusIDs[0]);
            ReadObjectQuery readObjectQuery = new ReadObjectQuery(Customer.class);
            readObjectQuery.setEJBQLString("SELECT OBJECT(thecust) FROM Customer thecust WHERE thecust.customerId = :id");
            query4.setDatabaseQuery(readObjectQuery);
            returnedCustomer2 = (Customer) query4.getSingleResult();

            commitTransaction();

        } catch (Exception unexpectedException) {
            throw new TestErrorException("Problem in GetSingleResultTest: " + unexpectedException);
        } finally {
            rollbackTransaction();
        }
    }

    public void verify() {
        if (expectedException1 == null) {
            throw new TestErrorException("getSingelResult on query returning multiple values did not throw a NonUniqueResultException");
        }
        if (expectedException2 == null) {
            throw new TestErrorException("getSingelResult on query returning multiple values did not throw an NoResultException");
        }
        if (returnedCustomer1 == null || (!returnedCustomer1.getCustomerId().equals(cusIDs[0]))) {
            throw new TestErrorException("Incorrect Single Customer returned, found: " + returnedCustomer1);
        }
        if (returnedCustomer2 == null || (!returnedCustomer2.getCustomerId().equals(cusIDs[0]))) {
            throw new TestErrorException("Incorrect Single Customer returned, found: " + returnedCustomer2);
        }
    }

}
