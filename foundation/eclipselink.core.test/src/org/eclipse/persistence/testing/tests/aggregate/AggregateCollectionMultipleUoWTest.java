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
package org.eclipse.persistence.testing.tests.aggregate;

import java.util.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.*;

public class AggregateCollectionMultipleUoWTest extends WriteObjectTest {
    public Object unitOfWorkWorkingCopy;
    public UnitOfWork unitOfWork1, unitOfWork2;

    public AggregateCollectionMultipleUoWTest() {
        super();
    }

    public AggregateCollectionMultipleUoWTest(Object originalObject) {
        super(originalObject);
    }

    protected void changeUnitOfWorkWorkingCopy1(Object object) {
        AgentBuilderHelper.setLastName(object, "Jackson");
        List customers = AgentBuilderHelper.getCustomers(object);
        Customer customer1 = (Customer)customers.get(0);
        customer1.setName("Vince Carter");
        customer1.removeDependant((Dependant)customer1.getDependants().lastElement());

        List houses = AgentBuilderHelper.getHouses(object);
        House house2 = (House)houses.get(houses.size()-1);
        house2.setDescriptions("do not buy it, it collapses -:)");
    }

    protected void changeUnitOfWorkWorkingCopy2(Object object) {
        AgentBuilderHelper.setLastName(object, "White");
        List customers = AgentBuilderHelper.getCustomers(object);
        Customer customer1 = (Customer)customers.get(0);
        customer1.setName("Tracy Martins");
        Customer newCustomer = new Customer();
        newCustomer.setIncome(753923);
        newCustomer.setName("Pete Lee");
        newCustomer.setCompany(Company.example5());
        newCustomer.addDependant(new Dependant("Sue", 5));
        newCustomer.addDependant(new Dependant("David", 1));
        AgentBuilderHelper.addCustomer(object, newCustomer);
        SingleHouse newHouse = new SingleHouse();
        newHouse.setLocation("123 Slater Street");
        newHouse.setDescriptions("every convinent to who works with The Object People");
        newHouse.setNumberOfGarages(3);
        AgentBuilderHelper.addHouse(object, newHouse);
    }

    protected void changeUnitOfWorkWorkingCopy3(Object object) {
        AgentBuilderHelper.setFirstName(object, "Johnie");
        Customer customer1 = (Customer)AgentBuilderHelper.getCustomers(object).get(2);
        customer1.setName("James");
        customer1.removeDependant((Dependant)customer1.getDependants().firstElement());
        customer1.addDependant(new Dependant("Stevenson", 14));
        AgentBuilderHelper.removeHouse(object, (House)AgentBuilderHelper.getHouses(object).get(AgentBuilderHelper.getHouses(object).size()-1));
        House newHouse = new House();
        newHouse.setLocation("45 Mann Ave");
        newHouse.setDescriptions("Close to Ottawa U.");
        AgentBuilderHelper.addHouse(object, newHouse);
    }

    protected void setup() {
        super.setup();
    }

    protected void test() {
        try {
            // Acquire one unit of work
            unitOfWork1 = getSession().acquireUnitOfWork();
            Object object = unitOfWork1.registerObject(this.objectToBeWritten);
            changeUnitOfWorkWorkingCopy1(object);
            //		unitOfWork1.commit();
            unitOfWork1.commitAndResume();

            /// Acquire nested unit of work
            UnitOfWork nestedUnitOfWork = unitOfWork1.acquireUnitOfWork();
            Object objectClone1 = nestedUnitOfWork.registerObject(object);

            /// Acquire nested nested unit of work
            UnitOfWork nestedNestedUnitOfWork = nestedUnitOfWork.acquireUnitOfWork();
            Object objectClone = nestedNestedUnitOfWork.registerObject(objectClone1);

            changeUnitOfWorkWorkingCopy2(objectClone);
            nestedNestedUnitOfWork.commit();
            nestedUnitOfWork.commit();
            unitOfWork1.commit();

            /*
		UnitOfWork uow = getSession().acquireUnitOfWork();
		Agent agentClone = (Agent) uow.registerObject(agent);
		changeUnitOfWorkWorkingCopy2(agentClone);	
		uow.commit();	
*/

            // Acquire another unit of work
            unitOfWork2 = getSession().acquireUnitOfWork();
            Object objectClone2 = unitOfWork2.readObject(originalObject.getClass());
            changeUnitOfWorkWorkingCopy3(objectClone2);
            unitOfWork2.commit();

        } catch (org.eclipse.persistence.exceptions.OptimisticLockException ex) {
            throw new TestWarningException("Optimistic locking exception thrown when object was changed outside during the transaction");
        }
    }

    protected void verify() {

    }
}
