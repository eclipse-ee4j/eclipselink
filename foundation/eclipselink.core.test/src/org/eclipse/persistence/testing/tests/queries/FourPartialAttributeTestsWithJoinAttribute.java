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
package org.eclipse.persistence.testing.tests.queries;

import java.util.Vector;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;

/**
 * Bug 5501751: USING GETALLOWINGNULL() WITH ADDPARTIALATTRIBUTE() BROKEN IN 10.1.3
 */
@SuppressWarnings("deprecation")
public class FourPartialAttributeTestsWithJoinAttribute extends TestCase {

    static String lastName = "PartialAttributeTest";
    Vector[] results = new Vector[4];
    
    public FourPartialAttributeTestsWithJoinAttribute() {
        setDescription("A partial attribute query where the partial attribute is also joined - 4 tests: all combinations of (inner join vs outer join) and (get(address) vs get(address).get(city)).");
    }

    public void setup() {
        // remove Employees with the lastName in case they exist.
        cleanup();
        
        // create two Employees with the lastName: one with an address another without.
        Employee empA = new Employee();
        empA.setLastName(lastName);
        empA.setFirstName("A");
        
        Address address = new Address();
        address.setCountry("Canada");
        address.setCity("Ottawa");
        address.setStreet("O'Connor");
        // Employee A has Address
        empA.setAddress(address);
        
        // Employee B doesn't have an Address
        Employee empB = new Employee();
        empB.setLastName(lastName);
        empB.setFirstName("B");
        
        // save the created objects
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerNewObject(empA);
        uow.registerNewObject(empB);
        uow.commit();
        
        // clear the cache
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        ReadAllQuery[] queries = {createQuery(), createQuery(), createQuery(), createQuery()};

        // The same as PartialAttributeTestWithJoinAttribute, but without changing any settings on the mapping.
        // Inner join: should return 
        // empA's first and last names and it's address.
        queries[0].addPartialAttribute("address");

        // Inner join: should return 
        // empA's first and last names and it's address's city.
        queries[1].addPartialAttribute(queries[1].getExpressionBuilder().get("address").get("city"));

        // Outer join: should return 
        // empA's first and last names and it's address;
        // empB's first and last names and null address.
        queries[2].addPartialAttribute(queries[2].getExpressionBuilder().getAllowingNull("address"));

        // Outer join: should return 
        // empA's first and last names and it's address's city;
        // empB's first and last names and address containing all nulls.
        queries[3].addPartialAttribute(queries[3].getExpressionBuilder().getAllowingNull("address").get("city"));

        for(int i=0; i<4; i++) {
            results[i] = (Vector)getSession().executeQuery(queries[i]);
        }

    }

    protected void verify() {
        String errorMsg = "";
        for(int i=0; i<4; i++) {
            Employee empA = null;
            if(results[i].size() >= 1) {
                empA = (Employee)results[i].elementAt(0);
            }
            Employee empB = null;
            if(results[i].size() >= 2) {
                empB = (Employee)results[i].elementAt(1);
            }
            if(empA == null) {
                errorMsg = errorMsg + "query"+i+" hasn't returned Employee A; ";
            } else {
                if(empA.getAddress() == null) {
                    errorMsg = errorMsg + "query"+i+" returned Employee A with null Address; ";
                }
            }
            if(i==0 || i==1) {
                // i==0 || i==1 - Inner join
                if(empB != null) {
                    errorMsg = errorMsg + "query"+i+" has returned Employee B; ";
                }
            } else {
                // i==2 || i==3 - Outer join
                if(empB == null) {
                    errorMsg = errorMsg + "query"+i+" has not returned Employee B; ";
                } else {
                    if(i==2) {
                        // builder.getAllowingNull("address")
                        if(empB.getAddress() != null) {
                            errorMsg = errorMsg + "query"+i+" has returned Employee B's address; ";
                        }
                    } else {
                        // builder.getAllowingNull("address").get("city")
                        if(empB.getAddress() == null) {
                            errorMsg = errorMsg + "query"+i+" has not returned Employee B's address; ";
                        } else {
                            if(empB.getAddress().getCity() != null) {
                                errorMsg = errorMsg + "query"+i+" has returned Employee B's address with city != null; ";
                            }
                        }
                    }
                }
            }
        }
        
        if(errorMsg.length() > 0) {
            throw new TestErrorException(errorMsg);
        }
    }

    public void reset() {
        cleanup();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected ReadAllQuery createQuery() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.dontMaintainCache();
        query.setSelectionCriteria(query.getExpressionBuilder().get("lastName").equal(lastName));
        query.addPartialAttribute("firstName");
        query.addPartialAttribute("lastName");
        query.addOrdering(query.getExpressionBuilder().get("firstName").ascending());
        return query;
    }
    
    protected void cleanup() {
        ReadAllQuery query = new ReadAllQuery(Employee.class);
        query.setSelectionCriteria(query.getExpressionBuilder().get("lastName").equal(lastName));
        Vector result = (Vector)getSession().executeQuery(query);
        if(!result.isEmpty()) {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            for(int i=0; i<result.size(); i++) {
                uow.deleteObject(result.elementAt(i));
            }
            uow.commit();
        }
    }
}
