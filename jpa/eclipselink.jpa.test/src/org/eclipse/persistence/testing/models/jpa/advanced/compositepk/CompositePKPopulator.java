/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     03/03/2010 - 2.1 Michael O'Brien
//       - 260263: SQLServer 2005/2008 requires stored procedure creation select clause variable and column name matching
package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import java.util.*;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureDefinition;

/**
 * <p><b>Purpose</b>: To build and populate the database for example and testing purposes.
 * This population routine is fairly complex and makes use of the population manager to
 * resolve interrelated objects as the employee objects are an interconnection graph of objects.
 *
 * This is not the recommended way to create new objects in your application,
 * this is just the easiest way to create interconnected new example objects from code.
 * Normally in your application the objects will be defined as part of a transactional and user interactive process.
 *
 * Be careful in changing any of the examples (names, projects etc) as they may
 * be used and relied on in testing.
 */
public class CompositePKPopulator {
    protected static boolean useFastTableCreatorAfterInitialCreate = Boolean.getBoolean("eclipselink.test.toggle-fast-table-creator");
    protected static boolean isFirstCreation = true;

    protected PopulationManager populationManager;
    protected Calendar startCalendar = Calendar.getInstance();
    protected Calendar endCalendar = Calendar.getInstance();

    public CompositePKPopulator() {
        this.populationManager = PopulationManager.getDefaultManager();
        this.startCalendar = Calendar.getInstance();
        this.startCalendar.set(Calendar.MILLISECOND, 0);
        this.endCalendar = Calendar.getInstance();
        this.endCalendar.set(Calendar.MILLISECOND, 0);

    }

    public Scientist scientistExample1() {
        if (containsObject(Scientist.class, "0001")) {
            return getObject(Scientist.class, "0001");
        }
        Cubicle cubicle1 = new Cubicle("G");

        Scientist scientist1 = new Scientist();
        scientist1.setFirstName("Guy");
        scientist1.setLastName("Pelletier");
        scientist1.setCubicle(cubicle1);
        registerObject(Scientist.class, scientist1, "0001");
        return scientist1;
    }

    public Scientist scientistExample2() {
        if (containsObject(Scientist.class, "0002")) {
            return getObject(Scientist.class, "0002");
        }
        Cubicle cubicle1 = new Cubicle("A");

        Scientist scientist1 = new Scientist();
        scientist1.setFirstName("Bob");
        scientist1.setLastName("Sparcly");
        scientist1.setCubicle(cubicle1);
        registerObject(Scientist.class, scientist1, "0002");
        return scientist1;
    }


    /**
     * Call all of the example methods in this system to guarantee that all our objects
     * are registered in the population manager
     */
    public void buildExamples() {
        // First ensure that no previous examples are hanging around.
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(Department.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(Scientist.class);
        scientistExample1();
        scientistExample2();
        departmentExample1();

    }

    public void persistExample(Session session) {
        Vector allObjects = new Vector();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();
        // Disable the read-only classes for model population. Specifically,
        // in this case we want to be able to create EquipmentCode objects.
        unitOfWork.removeAllReadOnlyClasses();

        PopulationManager.getDefaultManager().addAllObjectsForClass(Department.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(Scientist.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();

        if (TestCase.supportsStoredProcedures(session)) {
            boolean orig_FAST_TABLE_CREATOR = SchemaManager.FAST_TABLE_CREATOR;
            // on Symfoware, to avoid table locking issues only the first invocation
            // of an instance of this class (drops & re-)creates the tables.
            if (useFastTableCreatorAfterInitialCreate && !isFirstCreation) {
                SchemaManager.FAST_TABLE_CREATOR = true;
            }
            // next time it deletes the rows instead.
            isFirstCreation = false;
        }
    }

    protected boolean containsObject(Class domainClass, String identifier) {
        return populationManager.containsObject(domainClass, identifier);
    }

    public Department basicDepartmentExample1(){
        Department department = new Department();
        department.setName("SETUP 1");
        department.setRole("ROLE B");
        department.setLocation("LOCATION B");
        return department;
    }

    public Department departmentExample1() {
        if (containsObject(Department.class, "0001")) {
            return getObject(Department.class, "0001");
        }

        Department department = basicDepartmentExample1();
        department.addCompetency(competencyExample1());
        department.addCompetency(competencyExample2());

        department.addScientist(scientistExample1());
        department.addScientist(scientistExample2());
        registerObject(Department.class, department, "0001");
        return department;
    }

    public Competency competencyExample1(){
        Competency competency = new Competency();
        competency.description = "Manage groups";
        competency.rating = 9;
        return competency;
    }

    public Competency competencyExample2(){
        Competency competency = new Competency();
        competency.description = "Group Managment";
        competency.rating = 4;
        return competency;
    }
    protected Vector getAllObjects() {
        return populationManager.getAllObjects();
    }

    public Vector getAllObjectsForClass(Class domainClass) {
        return populationManager.getAllObjectsForClass(domainClass);
    }

    protected <T> T getObject(Class<T> domainClass, String identifier) {
        return (T) populationManager.getObject(domainClass, identifier);
    }


    protected <T> void registerObject(Class<T> domainClass, T domainObject, String identifier) {
        populationManager.registerObject(domainClass, domainObject, identifier);
    }

    protected void registerObject(Object domainObject, String identifier) {
        populationManager.registerObject(domainObject, identifier);
    }

}
