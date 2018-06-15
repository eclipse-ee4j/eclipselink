/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Praba Vijayaratnam - 2.3 - inital implementation
package org.eclipse.persistence.testing.jaxrs.utils;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.jaxrs.model.Address;
import org.eclipse.persistence.testing.jaxrs.model.Customer;
import org.eclipse.persistence.testing.jaxrs.model.PhoneNumber;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

import java.util.Vector;

public class JAXRSPopulator {

    protected PopulationManager populationManager;

    public JAXRSPopulator() {
        this.populationManager = PopulationManager.getDefaultManager();
        removeRegisteredObjects();
    }

    public void removeRegisteredObjects() {
        populationManager.getRegisteredObjects().remove(Address.class);
        populationManager.getRegisteredObjects().remove(PhoneNumber.class);
        populationManager.getRegisteredObjects().remove(Customer.class);
    }

    /**
     * Call all of the example methods in this system to guarantee that all our
     * objects are registered in the population manager
     */
    public void buildExamplesCustomer1() {
        // First ensure that no previous examples are hanging around.
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample1(), "c1");
    }

    public void buildExamplesCustomer2() {
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample2(), "c2");
    }

    public void buildExamplesCustomer3() {
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample3(), "c3");
    }

    public void buildExamplesCustomer4() {
        // used for Get tests (non-JSON)
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample4(), "c4");
    }

    public void buildExamplesCustomer5() {
        // used for GetCustomer JSON tests
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample5(), "c5");
    }

    public void buildEmptyTables() {
        // used for PostInsert tests which will insert C6 and c7
        removeRegisteredObjects();
    }

    public void buildExamplesCustomer8() {
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample8(), "c8");
    }

    public void buildExamplesCustomer9() {
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample9(), "c9");
    }

    public void buildExamplesCustomer10() {
        // used for Delete tests
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample10(), "c10");
    }

    public void buildExamplesCustomer11() {
        // used for GetAddressJSON tests
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample11(), "c11");
    }

    public void buildExamplesCustomer12() {
        // used for GetAddressJSON tests
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample12(), "c12");
    }

    public void buildExamplesCustomer1and3and10() {
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample1(), "c1");
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample3(), "c3");
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample10(), "c10");
    }

    public void buildExamplesCustomer13and14and15() {
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample13(), "c13");
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample14(), "c14");
        populationManager.registerObject(Customer.class, JAXRSExamples.customerExample15(), "c15");
    }

    public void persistExample(Session session) {
        Vector allObjects = new Vector();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();
        populationManager.addAllObjectsForClass(Address.class, allObjects);
        populationManager.addAllObjectsForClass(PhoneNumber.class, allObjects);
        populationManager.addAllObjectsForClass(Customer.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }

    protected boolean containsObject(Class domainClass, String identifier) {
        return populationManager.containsObject(domainClass, identifier);
    }
}
