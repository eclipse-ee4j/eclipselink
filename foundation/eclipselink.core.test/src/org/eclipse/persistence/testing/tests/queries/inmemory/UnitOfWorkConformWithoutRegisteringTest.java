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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import java.util.Vector;
import java.util.Enumeration;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Test for feature 2612601: conforming without registering in the Unit Of Work.
 * The test must insure the following things:
 *    -Conforming still works, even though objects are not being registered.
 *    -Objects returned by a conforming without registering query
 *    are in fact not being registered.
 *    -Registered objects never point to unregistered objects, and vice versa.
 *    -An unregistered object can later be safely registered.
 *
 * Test plan:
 *    -First execute two queries on the session: all employees whose address is
 *    in Ontario, and the complement.
 *    -Register employee 1 and change his address to outside Ontario.
 *    -Register employee 2's address and change it to outside Ontario.
 *    -Register employee 3 from outside Ontario and change address to Ontario.
 *
 *    At this point there are three registered addresses, and two registered
 *    employees.
 *
 *    If the same query is now executed again on the Unit of Work, conforming
 *    without registering, the following results should be expected:
 *
 *    -employee 1 will be returned from database, but on conform it will be
 *    replaced with its clone, which points to an address outside Ontario.  So
 *    it will be dropped.
 *
 *    -employee 2 will be returned from database, and since it was not registered
 *    will be returned automatically.  This is a limitation from bug 2675667:
 *    CONFORMING QUERIES MISS RESULTS NEWLY CONFORMING TO YET UNREGISTERED IN UOW.
 *
 *    -employee 3 will not be returned from database, but it will be added in
 *    later, as its address does conform.
 *
 *    -Even though the query was run on a unit of work, no additional employees
 *    or addresses were registered.
 *
 *    -Though employee 2's cloned address was used to conform, employee 2 does
 *    not point to the cloned address and vice versa.
 *
 *    Test Limitations:
 *
 *    -Only a single simple query was tested.  Employee to address is a one to
 *    one mapping, and there is only a single join.
 *
 *    -Employee 2 has an address outside of Ontario, but is returned by the
 *    conforming query.  This is a limitation from bug 2675667:
 *    CONFORMING QUERIES MISS RESULTS NEWLY CONFORMING TO YET UNREGISTERED IN UOW.
 *    Better to not work at all then sometimes.
 */
public class UnitOfWorkConformWithoutRegisteringTest extends TestCase {
    protected int expected;
    protected UnitOfWork uow;
    protected Vector result;
    protected Object result2;
    protected Object result3;
    protected ReadAllQuery query;
    protected int numberOfOntarioAddresses;
    protected Employee emigrant;
    protected Employee emigrant2;
    protected Employee immigrant;
    protected Employee outsider2;

    public UnitOfWorkConformWithoutRegisteringTest() {
        setDescription("Test that the query conforms to changes but does not register objects.");
    }

    public void reset() {
        uow.release();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        uow = getSession().acquireUnitOfWork();
        Expression expression = new ExpressionBuilder(Employee.class).get("address").get("province").equal("ONT");

        Vector ontarioEmployees = getSession().readAllObjects(Employee.class, expression);
        numberOfOntarioAddresses = ontarioEmployees.size();

        expression = new ExpressionBuilder(Employee.class).get("address").get("province").equal("ONT").not();
        Vector nonOntarioEmployees = getSession().readAllObjects(Employee.class, expression);

        // Case 1: Change one employee's address so he/she no longer lives in Ontario,
        // and register the employee too.
        Employee insider = (Employee)ontarioEmployees.elementAt(0);
        this.emigrant = (Employee)uow.registerExistingObject(insider);
        this.emigrant.getAddress().setProvince("ICN");

        // Case 2: Change one employee's address so he/she no longer lives in Ontario,
        // but only register the address, not the employee.
        this.emigrant2 = (Employee)ontarioEmployees.elementAt(1);
        Address incheonAddress = (Address)uow.registerExistingObject(emigrant2.getAddress());
        incheonAddress.setProvince("ICN");

        // Case 3:
        // Here is a dilemma.  We would like to change one address so that it now
        // conforms, but its owner must be in the cache!  Lets say Bob's address
        // is changed to Ontario, but Bob is not registered in the UOW.  The query
        // will not return Bob from the database, and Bob can not be added to the
        // result at conform time, as Bob is not in the cache.  Thus even though
        // Bob should be returned by the conforming query, as its address is now
        // in Ontario, it won't be unless Bob is registered first.
        // Bug 2675667 has been entered for this.
        Employee outsider = (Employee)nonOntarioEmployees.elementAt(0);
        this.immigrant = (Employee)uow.registerExistingObject(outsider);
        this.immigrant.getAddress().setProvince("ONT");

        // This is for the bug with not using the correct in-memory indiretion policy
        // to check the UOW cache.
        // Bug 2667870
        //outsider2 = (Employee)nonOntarioEmployees.elementAt(1);
        //outsider2 = (Employee)uow.registerExistingObject(outsider2);
        // At this point there should be only 3 addresses registered in the UOW cache.
        // Two of them once conformed but no longer do.  One of them did not but now
        // does.
        query = new ReadAllQuery(Employee.class);
        expression = new ExpressionBuilder(Employee.class).get("address").get("province").equal("ONT");
        query.setSelectionCriteria(expression);
        query.setShouldRegisterResultsInUnitOfWork(false);
        query.conformResultsInUnitOfWork();
        // When bug 2667870 is fixed this line should no longer be needed.
        query.getInMemoryQueryIndirectionPolicy().triggerIndirection();
    }

    public void test() {
        this.result = (Vector)this.uow.executeQuery(this.query);

        // Also test for ReadObjectQuery...
        ReadObjectQuery emigrantQuery = new ReadObjectQuery(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder(Employee.class);
        Expression expression = builder.get("address").get("province").equal("ONT");
        expression = expression.and(builder.get("firstName").equal(this.emigrant.getFirstName()));
        emigrantQuery.setSelectionCriteria(expression);
        emigrantQuery.setShouldRegisterResultsInUnitOfWork(false);
        emigrantQuery.conformResultsInUnitOfWork();
        this.result2 = this.uow.executeQuery(emigrantQuery);

        // Also test for ReadObjectQuery...
        emigrantQuery = new ReadObjectQuery(Employee.class);
        builder = new ExpressionBuilder(Employee.class);
        expression = builder.get("address").get("province").equal("ONT");
        expression = expression.and(builder.equal(this.emigrant));
        emigrantQuery.setSelectionCriteria(expression);
        emigrantQuery.setShouldRegisterResultsInUnitOfWork(false);
        emigrantQuery.conformResultsInUnitOfWork();
        this.result3 = this.uow.executeQuery(emigrantQuery);
    }

    protected void verify() {
        // First check that reading through the unit of work properly conformed
        // to the changes made in the unit of work.
        if (this.result.contains(this.emigrant)) {
            throw new TestErrorException("An employee no longer in Ontario was returned.");
        }
        if (!this.result.contains(this.immigrant)) {
            throw new TestErrorException("An employee that now lives in Ontario was not returned.");
        }
        if (this.result.size() != (this.numberOfOntarioAddresses)) {
            throw new TestErrorException("Expecting: " + (this.numberOfOntarioAddresses) + " retrieved: " + this.result.size());
        }

        // Now check that no addresses were registered and put in the unit of work cache.
        Vector registeredAddresses = uow.getIdentityMapAccessor().getAllFromIdentityMap(null, Address.class, null, null);
        if (registeredAddresses.size() != 3) {
            throw new TestErrorException("Should be only three addresses registered in UOW cache, not: " + registeredAddresses.size());
        }

        // Now check that no employees were registered and put in the UOW cache.
        Vector registeredEmployees = uow.getIdentityMapAccessor().getAllFromIdentityMap(null, Employee.class, null, null);
        if (registeredEmployees.size() != 2) {
            throw new TestErrorException("Should be only two employees registered in UOW cache, not: " + registeredEmployees.size());
        }

        // Now verify that no registered objects point to unregistered objects.
        // Also verify that no unregistered objects point to registered objects.
        for (Enumeration enumtr = result.elements(); enumtr.hasMoreElements();) {
            Employee unregistered = (Employee)enumtr.nextElement();
            if ((unregistered != this.immigrant) && registeredAddresses.contains(unregistered.getAddress())) {
                throw new TestErrorException("An unregistered object references a registered object.");
            }
        }

        // Now check that the read object query returned nothing: this should be
        // automatic if the above tests passed...
        if (result2 != null) {
            throw new TestErrorException("A no longer conforming object was returned by a read object query: " + result2 + ", emigrant: " + this.emigrant2);
        }

        // Now check that the read object query returned nothing: this should be
        // automatic if the above tests passed...
        if (result3 != null) {
            throw new TestErrorException("A no longer conforming object was returned by a read object query: " + result3 + ", emigrant: " + this.emigrant);
        }

        // Should also check that we can now safely register the result using
        // registerExistingObject.
        for (Enumeration enumtr = result.elements(); enumtr.hasMoreElements();) {
            Employee unregistered = (Employee)enumtr.nextElement();
            Employee registeredEmp = (Employee)uow.registerExistingObject(unregistered);
            Vector unregisteredAddresses = getSession().getIdentityMapAccessor().getAllFromIdentityMap(null, Address.class, null, null);
            if (unregisteredAddresses.contains(registeredEmp.getAddress()) || (!registeredEmp.getAddress().getProvince().equals("ONT") && !registeredEmp.getFirstName().equals(emigrant2.getFirstName()))) {
                throw new TestErrorException("Unregistered objects could not be properly registered later.");
            }
        }
    }
}
