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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.aggregate.Company;
import org.eclipse.persistence.testing.models.aggregate.Customer;
import org.eclipse.persistence.testing.models.aggregate.Dependant;
import org.eclipse.persistence.testing.models.aggregate.House;
import org.eclipse.persistence.testing.models.aggregate.Oid;

public class AggregateCollectionUoWTest extends WriteObjectTest {
    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork;

    public AggregateCollectionUoWTest() {
        super();
    }

    // must be Agent or Builder
    public AggregateCollectionUoWTest(Object originalObject) {
        super(originalObject);
    }

    protected void changeUnitOfWorkWorkingCopy() {
        Object object = this.unitOfWorkWorkingCopy;
        AgentBuilderHelper.setLastName(object, "Jackson");
        List customers = AgentBuilderHelper.getCustomers(object);
        Customer customer1 = (Customer)customers.get(0);
        customer1.setName("Vince Carter");
        //customer1.removeDependant((Dependant) customer1.getDependants().firstElement());
        customer1.addDependant(new Dependant("lily", 12));
        //agent.removeCustomer((Customer) customers.lastElement());
        List houses = AgentBuilderHelper.getHouses(object);
        AgentBuilderHelper.removeHouse(object, (House)houses.get(0));
        House house2 = (House)houses.get(houses.size()-1);
        house2.setDescriptions("do not buy it, it collapses -:)");
        Oid newInsurancePolicyId = new Oid();
        newInsurancePolicyId.setOid(new Integer(893453));
        house2.setInsuranceId(newInsurancePolicyId);
        House newHouse = new House();
        newHouse.setLocation("123 Slater Street");
        newHouse.setDescriptions("every convinent to who works with The Object People");
        AgentBuilderHelper.addHouse(object, newHouse);
        Customer newCustomer = new Customer();
        newCustomer.setName("Micheal Chang");
        newCustomer.setIncome(1000000);
        newCustomer.setCompany(Company.example4());
        Vector changDependnants = new Vector(3);
        changDependnants.addElement(new Dependant("Susan", 9));
        changDependnants.addElement(new Dependant("Julie", 5));
        changDependnants.addElement(new Dependant("David", 2));
        newCustomer.setDependants(changDependnants);
        AgentBuilderHelper.addCustomer(object, newCustomer);
    }

    protected void setup() {
        super.setup();

        // Acquire unit of work
        this.unitOfWork = getSession().acquireUnitOfWork();

        this.unitOfWorkWorkingCopy = this.unitOfWork.registerObject(this.objectToBeWritten);
        changeUnitOfWorkWorkingCopy();
        // Use the original session for comparision
        if (!compareObjects(this.originalObject, this.objectToBeWritten)) {
            throw new TestErrorException("The original object was changed through changing the clone.");
        }

        //testing optimistic locking
        //	getSession().executeNonSelectingSQL("UPDATE HOUSE SET LOCATION = 'no where' WHERE (LOCATION = '33D King Edward Street')");
    }

    protected void test() {
        try {
            this.unitOfWork.commit();
        } catch (org.eclipse.persistence.exceptions.OptimisticLockException ex) {
            new TestWarningException("Optimistic locking exception thrown when object was changed outside during the transaction");
        }
    }

    protected void verify() {
        // Using the original session for comparison verify that the changes were merged correctly
        if (!compareObjects(this.unitOfWorkWorkingCopy, this.objectToBeWritten)) {
            throw new TestErrorException("The original object did not receive the changes correctly, in the merge.");
        }

        // Verify that the changes were made on the database correctly.
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        this.objectFromDatabase = getSession().executeQuery(this.query);

        if (!(compareObjects(this.objectToBeWritten, this.objectFromDatabase))) {
            throw new TestErrorException("The object inserted into the database, '" + this.objectFromDatabase + "' does not match the original, '" + this.objectToBeWritten + ".");
        }
    }
}
