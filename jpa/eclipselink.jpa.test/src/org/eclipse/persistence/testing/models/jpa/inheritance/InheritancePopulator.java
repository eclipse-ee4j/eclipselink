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


 
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.util.Vector;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

/**
 * <p><b>Purpose</b>: To build and populate the database for example and testing purposes.
 * This population routine is fairly complex and makes use of the population manager to
 * resolve interrelated objects as the employee objects are an interconnection graph of objects.
 *
 * This is not the recommended way to create new objects in your application,
 * this is just the easiest way to create interconnected new example objects from code.
 * Normally in your application the objects will be defined as part of a transactional and user interactive process.
 */
public class InheritancePopulator {
    protected PopulationManager populationManager;

    public InheritancePopulator() {
        this.populationManager = PopulationManager.getDefaultManager();

    }

    /**
     * Call all of the example methods in this system to guarantee that all our objects
     * are registered in the population manager
     */
    public void buildExamples() {
        // First ensure that no previous examples are hanging around.
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(Person.class);

        PopulationManager.getDefaultManager().registerObject(Person.class, InheritanceModelExamples.personExample1(), "e1");
        Engineer engineer = InheritanceModelExamples.personExample2();
        PopulationManager.getDefaultManager().registerObject(Person.class, engineer, "e2");
        PopulationManager.getDefaultManager().registerObject(Person.class, InheritanceModelExamples.personExample3(), "e3");
        PopulationManager.getDefaultManager().registerObject(Person.class, InheritanceModelExamples.personExample4(), "e4");
        PopulationManager.getDefaultManager().registerObject(Person.class, InheritanceModelExamples.personExample5(), "e5");
        PopulationManager.getDefaultManager().registerObject(Person.class, InheritanceModelExamples.personExample6(), "e6");

        PopulationManager.getDefaultManager().registerObject(AAA.class, InheritanceModelExamples.aaaExample1(), "a1");
        PopulationManager.getDefaultManager().registerObject(AAA.class, InheritanceModelExamples.bbbExample1(), "b1");
        PopulationManager.getDefaultManager().registerObject(AAA.class, InheritanceModelExamples.cccExample1(), "c1");
        PopulationManager.getDefaultManager().registerObject(AAA.class, InheritanceModelExamples.cccExample1(), "c2");

        Company company = InheritanceModelExamples.companyExample1();
        engineer.setCompany(company);
        
        PopulationManager.getDefaultManager().registerObject(Company.class, company, "co1");
        PopulationManager.getDefaultManager().registerObject(Company.class, InheritanceModelExamples.companyExample2(), "co2");
        PopulationManager.getDefaultManager().registerObject(Company.class, InheritanceModelExamples.companyExample3(), "co3");
        
        PopulationManager.getDefaultManager().registerObject(Computer.class, InheritanceModelExamples.laptopExample1(), "lap1");
        PopulationManager.getDefaultManager().registerObject(Computer.class, InheritanceModelExamples.laptopExample2(), "lap2");
        PopulationManager.getDefaultManager().registerObject(Computer.class, InheritanceModelExamples.desktopExample1(), "desk1");
        PopulationManager.getDefaultManager().registerObject(Computer.class, InheritanceModelExamples.desktopExample2(), "desk2");
    }
    
    
    public void persistExample(Session session)
    {        
        Vector allObjects = new Vector();        
        UnitOfWork unitOfWork = session.acquireUnitOfWork();        
        PopulationManager.getDefaultManager().addAllObjectsForClass(Person.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(AAA.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(Company.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(Computer.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
        
    }
    protected boolean containsObject(Class domainClass, String identifier) {
        return populationManager.containsObject(domainClass, identifier);
    }


}
