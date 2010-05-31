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
 *     Dies Koper - use the default sequence name from the platform
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.interfaces;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class InterfaceWithoutTablesSystem extends TestSystem {
    public InterfaceWithoutTablesSystem() {
        project = new InterfaceWithoutTablesProject();
    }

    public void addDescriptors(DatabaseSession session) {
        Admendments.addToManagerialJobDescriptor(project.getDescriptors().get(ManagerialJob.class));
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        schemaManager.replaceObject(Secretary.secretaryTable());
        schemaManager.replaceObject(Receptionist.receptionistTable());
        schemaManager.replaceObject(ProductManager.productManagerTable());
        schemaManager.replaceObject(PersonnelManager.personnelManagerTable());
        schemaManager.replaceObject(ProductDeveloper.productDeveloperTable());
        schemaManager.replaceObject(CourseDeveloper.courseDeveloperTable());
        schemaManager.replaceObject(DevelopmentJob.developmentJobTable());
        schemaManager.replaceObject(AdministrativeJob.administrativeJobTable());
        schemaManager.replaceObject(Employee.tableDefinition());
        schemaManager.replaceObject(Company.tableDefinition());
        schemaManager.replaceObject(Email.tableDefinition());
        schemaManager.replaceObject(Phone.tableDefinition());
        schemaManager.replaceObject(Computer.tableDefinition());
        schemaManager.replaceObject(Vehicle.tableDefinition());
        schemaManager.replaceObject(Actor.actorTable());
        schemaManager.replaceObject(Documentary.documentaryTable());
        schemaManager.replaceObject(Film.filmTable());
        /**
         * This hard coded SQL is to ensure that email and phone will have instances with the same primary key
         * This is important to test the type field fix  in 2.5.0.5.
         * There is no possibility of existing instances being messed up, because all the tables are dropped
         * here as well
         */
        String sequenceTableName = "SEQUENCE";
        if (session.getPlatform().getDefaultSequence().isTable()) {
            sequenceTableName = session.getPlatform().getQualifiedSequenceTableName();
        }
        session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("DELETE FROM " + sequenceTableName + " WHERE SEQ_NAME = 'EMAIL_SEQ'"));
        session.executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("DELETE FROM " + sequenceTableName  + " WHERE SEQ_NAME = 'PHONE_SEQ'"));
        schemaManager.createSequences();
    }

    public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        /***************************************/
        Film film1 = Film.example1();
        Film film3 = Film.example3();
        Documentary documentary1 = Documentary.example1();
        Actor actor4 = Actor.example4();

        unitOfWork.registerObject(film1);
        unitOfWork.registerObject(film3);
        unitOfWork.registerObject(documentary1);
        unitOfWork.registerObject(actor4);
        manager.registerObject(film1, "example1");
        manager.registerObject(film3, "example3");
        manager.registerObject(documentary1, "example1");
        manager.registerObject(actor4, "example4");

        /***************************************/
        PersonnelManager personnelManager1 = PersonnelManager.example1();
        PersonnelManager personnelManager2 = PersonnelManager.example2();
        PersonnelManager personnelManager3 = PersonnelManager.example3();

        unitOfWork.registerObject(personnelManager1);
        unitOfWork.registerObject(personnelManager2);
        unitOfWork.registerObject(personnelManager3);
        manager.registerObject(personnelManager1, "example1");
        manager.registerObject(personnelManager2, "example2");
        manager.registerObject(personnelManager3, "example3");

        /***************************************/
        ProductManager productManager1 = ProductManager.example1();
        ProductManager productManager2 = ProductManager.example2();
        ProductManager productManager3 = ProductManager.example3();

        unitOfWork.registerObject(productManager1);
        unitOfWork.registerObject(productManager2);
        unitOfWork.registerObject(productManager3);
        manager.registerObject(productManager1, "example1");
        manager.registerObject(productManager2, "example2");
        manager.registerObject(productManager3, "example3");

        /***************************************/
        ProductDeveloper productDeveloper1 = ProductDeveloper.example1();
        ProductDeveloper productDeveloper2 = ProductDeveloper.example2();
        ProductDeveloper productDeveloper3 = ProductDeveloper.example3();

        unitOfWork.registerObject(productDeveloper1);
        unitOfWork.registerObject(productDeveloper2);
        unitOfWork.registerObject(productDeveloper3);
        manager.registerObject(productDeveloper1, "example1");
        manager.registerObject(productDeveloper2, "example2");
        manager.registerObject(productDeveloper3, "example3");

        /***************************************/
        CourseDeveloper courseDeveloper1 = CourseDeveloper.example1();
        CourseDeveloper courseDeveloper2 = CourseDeveloper.example2();
        CourseDeveloper courseDeveloper3 = CourseDeveloper.example3();

        unitOfWork.registerObject(courseDeveloper1);
        unitOfWork.registerObject(courseDeveloper2);
        unitOfWork.registerObject(courseDeveloper3);
        manager.registerObject(courseDeveloper1, "example1");
        manager.registerObject(courseDeveloper2, "example2");
        manager.registerObject(courseDeveloper3, "example3");

        /***************************************/
        Secretary secretary1 = Secretary.example1();
        Secretary secretary2 = Secretary.example2();
        Secretary secretary3 = Secretary.example3();

        unitOfWork.registerObject(secretary1);
        unitOfWork.registerObject(secretary2);
        unitOfWork.registerObject(secretary3);
        manager.registerObject(secretary1, "example1");
        manager.registerObject(secretary2, "example2");
        manager.registerObject(secretary3, "example3");

        /***************************************/
        Receptionist receptionist1 = Receptionist.example1();
        Receptionist receptionist2 = Receptionist.example2();
        Receptionist receptionist3 = Receptionist.example3();

        unitOfWork.registerObject(receptionist1);
        unitOfWork.registerObject(receptionist2);
        unitOfWork.registerObject(receptionist3);
        manager.registerObject(receptionist1, "example1");
        manager.registerObject(receptionist2, "example2");
        manager.registerObject(receptionist3, "example3");

        /***************************************/
        /*    Enumeration enum1 = personnelManager1.getManagedEmployees().elements();
		    while (enum1.hasMoreElements()) {
		        manager.registerObject(enum1.nextElement(), "example1");
		    }

		    Enumeration enum2 = personnelManager2.getManagedEmployees().elements();
		    while (enum2.hasMoreElements()) {
		        manager.registerObject(enum2.nextElement(), "example2");
		    }
		    Enumeration enum3 = personnelManager3.getManagedEmployees().elements();
		    while (enum3.hasMoreElements()) {
		        manager.registerObject(enum3.nextElement(), "example3");
		    }
		*/
        unitOfWork.commit();

        /***************************************/
        Employee employee1 = Employee.example1();
        Employee employee2 = Employee.example2();
        Employee employee3 = Employee.example3();
        Employee employee4 = Employee.example4();

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerObject(employee1);
        uow.registerObject(employee3);
        uow.registerObject(employee2);
        uow.registerObject(employee4);
        uow.commit();

        manager.registerObject(employee1, "example1");
        manager.registerObject(employee2, "example2");
        manager.registerObject(employee3, "example3");
        manager.registerObject(employee4, "example4");

        /***************************************/
        Company company1 = Company.example1();
        Company company2 = Company.example2();
        Company company3 = Company.example3();

        session.writeObject(company1);
        session.writeObject(company2);
        session.writeObject(company3);
        manager.registerObject(company1, "example1");
        manager.registerObject(company2, "example2");
        manager.registerObject(company3, "example3");

        /***************************************/
        Email email1 = Email.example1();
        Email email2 = Email.example2();
        Email email3 = Email.example3();

        session.writeObject(email1);
        session.writeObject(email2);
        session.writeObject(email3);
        manager.registerObject(email1, "example1");
        manager.registerObject(email2, "example2");
        manager.registerObject(email3, "example3");

        /***************************************/
        Phone phone1 = Phone.example1();
        Phone phone2 = Phone.example2();
        Phone phone3 = Phone.example3();

        session.writeObject(phone1);
        session.writeObject(phone2);
        session.writeObject(phone3);
        manager.registerObject(phone1, "example1");
        manager.registerObject(phone2, "example2");
        manager.registerObject(phone3, "example3");

        /***************************************/
    }
}
