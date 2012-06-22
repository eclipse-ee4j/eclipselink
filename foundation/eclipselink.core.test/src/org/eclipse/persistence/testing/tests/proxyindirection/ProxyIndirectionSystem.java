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
package org.eclipse.persistence.testing.tests.proxyindirection;

import java.util.Vector;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;

public class ProxyIndirectionSystem extends TestSystem {
    public ProxyIndirectionSystem() {
        project = new ProxyIndirectionProject();
    }

    public void addDescriptors(DatabaseSession session) {
        Vector descriptors = new Vector();
        if (project == null) {
            project = new ProxyIndirectionProject();
        }

        (session).addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        ProxyIndirectionTableCreator creator = new ProxyIndirectionTableCreator();
        creator.replaceTables(session);
    }

    public void populate(DatabaseSession session) {
        // ===================================
        // Create model objects
        // ===================================
        Employee emp1 = new EmployeeImpl();
        emp1.setFirstName("Rick");
        emp1.setLastName("Barkhouse");
        emp1.setGender("Male");
        emp1.setAge(24);
        Employee emp2 = new EmployeeImpl();
        emp2.setFirstName("Angie");
        emp2.setLastName("MacIvor");
        emp2.setGender("Female");
        emp2.setAge(24);
        Employee emp3 = new EmployeeImpl();
        emp3.setFirstName("James");
        emp3.setLastName("Sutherland");
        emp3.setGender("Male");
        emp3.setAge(28);

        Address add1 = new AddressImpl();
        add1.setStreet("509-171 Lees Ave.");
        add1.setCity("Ottawa");
        add1.setState("ON");
        add1.setCountry("Canada");
        add1.setPostalCode("K1S 5P3");
        Address add2 = new AddressImpl();
        add2.setStreet("14421 Baseline Rd.");
        add2.setCity("Ottawa");
        add2.setState("ON");
        add2.setCountry("Canada");
        add2.setPostalCode("K1T 5A1");

        Cubicle cube1 = new CubicleImpl();
        cube1.setLength(5.7f);
        cube1.setWidth(5.7f);
        cube1.setHeight(5.0f);
        Cubicle cube2 = new CubicleImpl();
        cube2.setLength(6.4f);
        cube2.setWidth(5.7f);
        cube2.setHeight(5.0f);
        Cubicle cube3 = new CubicleImpl();
        cube3.setLength(8.4f);
        cube3.setWidth(8.7f);
        cube3.setHeight(5.0f);

        Project project1 = new ProjectImpl();
        project1.setName("Support Tracker 3");
        project1.setDescription("Customer support software to keep track of support e-mail");
        LargeProject project2 = new LargeProjectImpl();
        project2.setName("Virtual Coffee Machine");
        project2.setDescription("Espresso machine simulator written in Java");
        project2.setBudget(125000);
        project2.setInvestor("Second Cup Coffee Co.");

        EmailImpl email1 = new EmailImpl();
        email1.setIsPublic(true);
        email1.setWantsHTMLMail(true);
        email1.setUsername("rick_barkhouse");
        email1.setDomain("yahoo.ca");
        EmailImpl email2 = new EmailImpl();
        email2.setIsPublic(true);
        email2.setWantsHTMLMail(false);
        email2.setUsername("amacivor");
        email2.setDomain("sympatico.ca");
        PhoneImpl phone1 = new PhoneImpl();
        phone1.setIsPublic(false);
        phone1.setNumber("613-123-4567");
        phone1.setType(PhoneImpl.CELL);
        
        DesktopComputer comp1 = new DesktopComputerImpl();        
        comp1.setDescription("Compaq Pentium 200");
        comp1.setSerialNumber("CP18176-187262");
        comp1.setMoniterSize(21);
        
        DesktopComputer comp2 = new DesktopComputerImpl();        
        comp2.setDescription("Acer PentiumII 300");
        comp2.setSerialNumber("2876-298769232736");
        comp2.setMoniterSize(19);

        DesktopComputer comp3 = new DesktopComputerImpl();
        comp3.setDescription("Micron PentiumII 450");
        comp3.setSerialNumber("OU812-URABUTLN");
        comp3.setMoniterSize(17);

        // ===================================
        // Register and make relationships
        // ===================================
        UnitOfWork uow = session.acquireUnitOfWork();

        uow.registerNewObject(cube1);
        uow.registerNewObject(cube2);
        uow.registerNewObject(cube3);
        uow.assignSequenceNumbers();

        cube1.setEmployee(emp1);
        cube2.setEmployee(emp2);
        cube3.setEmployee(emp3);
        cube1.setComputer(comp1);
        cube2.setComputer(comp2);
        cube3.setComputer(comp3);

        emp1.setAddress(add1);
        emp1.setProject(project1);
        emp1.setLargeProject(project2);
        emp1.setCubicleID(cube1.getID());
        emp1.setContact(email1);
        emp2.setAddress(add1);
        emp2.setProject(project2);
        emp2.setCubicleID(cube2.getID());
        emp2.setContact(email2);
        emp3.setAddress(add2);
        emp3.setCubicleID(cube3.getID());
        emp3.addManagedEmployee(emp1);
        emp3.addManagedEmployee(emp2);
        emp3.setContact(phone1);

        uow.commit();
    }
}
