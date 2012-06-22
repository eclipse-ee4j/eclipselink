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
package org.eclipse.persistence.testing.models.relationshipmaintenance;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * This model is a complex ownership model. The complex ownership test cases
 * would use this model to test TopLink ownership feature.
 */
public class RelationshipsSystem extends TestSystem {
    public RelationshipsSystem() {
        project = new RelationshipsProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new RelationshipsProject();
        }
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        new RelationshipsTableCreator().replaceTables(session);
        SchemaManager schemaManager = new SchemaManager(session);
        schemaManager.createSequences();
    }

    public void populate(DatabaseSession session) {
        FieldOffice fieldOffice1 = new FieldOffice();
        FieldOffice fieldOffice2 = new FieldOffice();

        FieldLocation fieldLocation1 = FieldLocation.example1();
        FieldLocation fieldLocation2 = FieldLocation.example2();

        fieldOffice1.setLocation(fieldLocation1);
        fieldOffice2.setLocation(fieldLocation2);

        FieldManager manager1 = FieldManager.example1();
        FieldManager manager2 = FieldManager.example2();

        fieldOffice1.setManager(manager1);
        manager1.setOffice(fieldOffice1);
        fieldOffice2.setManager(manager2);
        manager2.setOffice(fieldOffice2);

        // setup SalesPerson objects
        SalesPerson salesPerson1 = SalesPerson.example1();
        SalesPerson salesPerson2 = SalesPerson.example2();
        SalesPerson salesPerson3 = SalesPerson.example3();
        SalesPerson salesPerson4 = SalesPerson.example4();
        SalesPerson salesPerson5 = SalesPerson.example5();

        fieldOffice1.addSalesPerson(salesPerson1);
        salesPerson1.setFieldOffice(fieldOffice1);
        fieldOffice1.addSalesPerson(salesPerson2);
        salesPerson2.setFieldOffice(fieldOffice1);

        fieldOffice2.addSalesPerson(salesPerson3);
        salesPerson3.setFieldOffice(fieldOffice2);
        fieldOffice2.addSalesPerson(salesPerson4);
        salesPerson4.setFieldOffice(fieldOffice2);
        fieldOffice2.addSalesPerson(salesPerson5);
        salesPerson5.setFieldOffice(fieldOffice2);

        fieldOffice1.getResources().add(new Resource("Copier 454", fieldOffice1));
        fieldOffice1.getResources().add(new Resource("Fax 123", fieldOffice1));
        fieldOffice1.getResources().add(new Resource("Overhead Projector 23", fieldOffice1));

        fieldOffice2.getResources().add(new Resource("LCD Projector", fieldOffice2));
        fieldOffice2.getResources().add(new Resource("Silverado", fieldOffice2));
        fieldOffice2.getResources().add(new Resource("SV650", fieldOffice2));

        // setup Customers, associate them with SalesPeople.
        Customer customer1 = Customer.example1();
        Customer customer2 = Customer.example2();
        Customer customer3 = Customer.example3();
        Customer customer4 = Customer.example4();
        Customer customer5 = Customer.example5();

        salesPerson1.addCustomer(customer1);
        customer1.addSalesPerson(salesPerson1);

        salesPerson1.addCustomer(customer2);
        customer2.addSalesPerson(salesPerson1);

        salesPerson2.addCustomer(customer2);
        customer2.addSalesPerson(salesPerson2);

        salesPerson2.addCustomer(customer3);
        customer3.addSalesPerson(salesPerson2);

        salesPerson3.addCustomer(customer4);
        customer4.addSalesPerson(salesPerson3);

        salesPerson4.addCustomer(customer4);
        customer4.addSalesPerson(salesPerson4);

        salesPerson4.addCustomer(customer5);
        customer5.addSalesPerson(salesPerson4);

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerObject(fieldOffice1);
        uow.registerObject(fieldOffice2);

        Dept dept = new Dept();
        dept.setDeptno(new Double(5.0));
        dept.setDname("Goofs");

        Emp emp = new Emp();
        emp.setEmpno(new Double(5.0));
        emp.setEname("Anthony");
        emp.setDeptno(dept);

        Emp emp1 = new Emp();
        emp1.setEmpno(new Double(6.0));
        emp1.setEname("Bob");
        emp1.setDeptno(dept);

        Emp emp2 = new Emp();
        emp2.setEmpno(new Double(7.0));
        emp2.setEname("Fargo");
        emp2.setDeptno(dept);

        Emp emp3 = new Emp();
        emp3.setEmpno(new Double(8.0));
        emp3.setEname("Oneder");
        emp3.setDeptno(dept);

        uow.registerObject(dept);
        uow.registerObject(emp);
        uow.registerObject(emp1);
        uow.registerObject(emp2);
        uow.registerObject(emp3);

        uow.commit();

    }
}
