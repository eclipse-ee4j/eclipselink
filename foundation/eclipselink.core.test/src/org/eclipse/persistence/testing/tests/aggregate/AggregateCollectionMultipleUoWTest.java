/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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

    protected void changeUnitOfWorkWorkingCopy1(Agent agent) {
        agent.setLastName("Jackson");
        Vector customers = agent.getCustomers();
        Customer customer1 = (Customer)customers.firstElement();
        customer1.setName("Vince Carter");
        customer1.removeDependant((Dependant)customer1.getDependants().lastElement());

        Vector houses = agent.getHouses();
        House house2 = (House)houses.lastElement();
        house2.setDescriptions("do not buy it, it collapses -:)");
    }

    protected void changeUnitOfWorkWorkingCopy2(Agent agent) {
        agent.setLastName("White");
        Vector customers = agent.getCustomers();
        Customer customer1 = (Customer)customers.firstElement();
        customer1.setName("Tracy Martins");
        Customer newCustomer = new Customer();
        newCustomer.setIncome(753923);
        newCustomer.setName("Pete Lee");
        newCustomer.setCompany(Company.example5());
        newCustomer.addDependant(new Dependant("Sue", 5));
        newCustomer.addDependant(new Dependant("David", 1));
        agent.addCustomer(newCustomer);
        SingleHouse newHouse = new SingleHouse();
        newHouse.setLocation("123 Slater Street");
        newHouse.setDescriptions("every convinent to who works with The Object People");
        newHouse.setNumberOfGarages(3);
        agent.addHouse(newHouse);
    }

    protected void changeUnitOfWorkWorkingCopy3(Agent agent) {
        agent.setFirstName("Johnie");
        Customer customer1 = (Customer)agent.getCustomers().elementAt(2);
        customer1.setName("James");
        customer1.removeDependant((Dependant)customer1.getDependants().firstElement());
        customer1.addDependant(new Dependant("Stevenson", 14));
        agent.removeHouse((House)agent.getHouses().lastElement());
        House newHouse = new House();
        newHouse.setLocation("45 Mann Ave");
        newHouse.setDescriptions("Close to Ottawa U.");
        agent.addHouse(newHouse);
    }

    protected void setup() {
        super.setup();
    }

    protected void test() {
        try {
            // Acquire one unit of work
            unitOfWork1 = getSession().acquireUnitOfWork();
            Agent agent = (Agent)unitOfWork1.registerObject(this.objectToBeWritten);
            changeUnitOfWorkWorkingCopy1(agent);
            //		unitOfWork1.commit();
            unitOfWork1.commitAndResume();

            /// Acquire nested unit of work
            UnitOfWork nestedUnitOfWork = unitOfWork1.acquireUnitOfWork();
            Agent agentClone1 = (Agent)nestedUnitOfWork.registerObject(agent);

            /// Acquire nested nested unit of work
            UnitOfWork nestedNestedUnitOfWork = nestedUnitOfWork.acquireUnitOfWork();
            Agent agentClone = (Agent)nestedNestedUnitOfWork.registerObject(agentClone1);

            changeUnitOfWorkWorkingCopy2(agentClone);
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
            Agent agentClone2 = (Agent)unitOfWork2.readObject(Agent.class);
            changeUnitOfWorkWorkingCopy3(agentClone2);
            unitOfWork2.commit();

        } catch (org.eclipse.persistence.exceptions.OptimisticLockException ex) {
            throw new TestWarningException("Optimistic locking exception thrown when object was changed outside during the transaction");
        }
    }

    protected void verify() {

    }
}
