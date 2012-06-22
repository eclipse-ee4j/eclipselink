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
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.util.Vector;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

/**
 * <p><b>Purpose</b>: To build and populate the database for example and
 * testing purposes. This population routine is fairly complex and makes use
 * of the population manager to resolve interrated objects as the employee
 * objects are an interconnection graph of objects. 
 *
 * This is not the recomended way to create new objects in your application, 
 * this is just the easiest way to create interconnected new example objects
 * from code. Normally in your application the objects will be defined as part
 * of a transactional and user interactive process. 
 */
public class PartnerLinkPopulator {

    protected PopulationManager populationManager;

    public PartnerLinkPopulator() {
        this.populationManager = PopulationManager.getDefaultManager();
    }

    public Man manExample1() {
        if (containsObject(Man.class, "0001")) {
            return (Man)getObject(Man.class, "0001");
        }

        Man man = new Man();
        man.setFirstName("Bob");
        man.setLastName("Smith");
        registerObject(Man.class, man, "0001");

        return man;
    }

    public Man manExample2() {
        if (containsObject(Man.class, "0002")) {
            return (Man)getObject(Man.class, "0002");
        }

        Man man = new Man();
        man.setFirstName("Jim-bob");
        man.setLastName("Jefferson");
        registerObject(Man.class, man, "0002");

        return man;
    }

    public Woman womanExample1() {
        if (containsObject(Woman.class, "0001")) {
            return (Woman)getObject(Woman.class, "0001");
        }

        Woman woman = new Woman();
        woman.setFirstName("Jill");
        woman.setLastName("May");
        registerObject(Woman.class, woman, "0001");

        return woman;
    }

    public PartnerLink partnerLinkExample1() {
        if (containsObject(PartnerLink.class, "0001")) {
            return (PartnerLink)getObject(PartnerLink.class, "0001");
        }

        PartnerLink partnerLink = new PartnerLink();
        partnerLink.setMan(manExample1());
        partnerLink.setWoman(womanExample1());
        registerObject(PartnerLink.class, partnerLink, "0001");

        return partnerLink;
    }

    /**
     * Call all of the example methods in this system to guarantee that all
     * our objects are registered in the population manager.
     */
    public void buildExamples() {
        // First ensure that no preivous examples are hanging around.
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(Man.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(Woman.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(PartnerLink.class);

        manExample1();
        womanExample1();
        partnerLinkExample1();
        manExample2();
    }
    
    public void persistExample(Session session) {        
        Vector allObjects = new Vector();        
        UnitOfWork unitOfWork = session.acquireUnitOfWork();        
        PopulationManager.getDefaultManager().addAllObjectsForClass(Man.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(Woman.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(PartnerLink.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }
    
    protected void registerObject(Class domainClass, Object domainObject, String identifier) {
        populationManager.registerObject(domainClass, domainObject, identifier);
    }

    protected void registerObject(Object domainObject, String identifier) {
        populationManager.registerObject(domainObject, identifier);
    }

    protected boolean containsObject(Class domainClass, String identifier) {
        return populationManager.containsObject(domainClass, identifier);
    }

    protected Vector getAllObjects() {
        return populationManager.getAllObjects();
    }

    public Vector getAllObjectsForClass(Class domainClass) {
        return populationManager.getAllObjectsForClass(domainClass);
    }

    protected Object getObject(Class domainClass, String identifier) {
        return populationManager.getObject(domainClass, identifier);
    }

}
