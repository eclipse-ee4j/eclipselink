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
package org.eclipse.persistence.testing.models.aggregate;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.nested.*;

public class AggregateSystem extends TestSystem {
    // The new apis added to AggregateCollectionMapping
    // in order to support jpa 2.0 element collections currently
    // are not compatible with project.xml
    // The flag provided so that AggregateWorkbenchIntegrationSystem
    // could remove all the setup that uses this new feature.
    protected boolean useNewAggregateCollection = true;
    
    public AggregateSystem() {
        this(true);
    }

    public AggregateSystem(boolean useNewAggregateCollection) {
        this.useNewAggregateCollection = useNewAggregateCollection;  
        project = new AggregateProject(useNewAggregateCollection);
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new AggregateProject(useNewAggregateCollection);
        }
        session.addDescriptors(project);

        project = new Project_case2();
        session.addDescriptors(project);

        project = new NestedAggregateProject();
        session.addDescriptors(project);

        project = new SwitchProject();
        session.addDescriptors(project);
        
        project = new AggregateRelationshipsProject();
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        SchemaManager schemaManager = new SchemaManager(session);

        schemaManager.replaceObject(Worker.tableDefinition());

        schemaManager.replaceObject(Address.tableDefinition());
        schemaManager.replaceObject(Client.tableDefinition());
        schemaManager.replaceObject(EvaluationClient.tableDefinition());
        schemaManager.replaceObject(Computer.tableDefinition());
        schemaManager.replaceObject(Employee.tableDefinition());
        schemaManager.replaceObject(Language.tableDefinition());
        schemaManager.replaceObject(EvaluationClient.tableDefinition());
        schemaManager.replaceObject(Responsibility.tableDefinition());

        // Added May 5, 2000 - Jon D. for pr381
        schemaManager.replaceObject(Vehicle.tableDefinition());

        //the added tables for aggregate 1:m testing
        schemaManager.replaceObject(Agent.tableDefinition());
        schemaManager.replaceObject(Customer.tableDefinition());
        schemaManager.replaceObject(House.tableDefinition());
        schemaManager.replaceObject(Dependant.tableDefinition());
        schemaManager.replaceObject(Company.tableDefinition());
        schemaManager.replaceObject(SingleHouse.tableDefinition());
        schemaManager.replaceObject(SellingPoint.tableDefinition());
        // the added tables for jpa 2.0 aggregate testing
        if(useNewAggregateCollection) {
            schemaManager.replaceObject(Builder.tableDefinition());
            schemaManager.replaceObject(Builder.houseTableDefinition());
            schemaManager.replaceObject(Builder.singleHouseTableDefinition());
            schemaManager.replaceObject(Builder.sellingPointTableDefinition());
            schemaManager.replaceObject(Builder.customerTableDefinition());
            schemaManager.replaceObject(Builder.dependantTableDefinition());
            schemaManager.replaceObject(Builder.vehicleTableDefinition());
        }

        TableDefinition table = Employee1.tableDefinition();
        schemaManager.buildFieldTypes(table);
        schemaManager.replaceObject(table);

        schemaManager.replaceObject(getRelationTable());
        schemaManager.createSequences();

        // NESTED AGGREGATES
        schemaManager.replaceObject(NestedAggregateTableCreator.tableDefinition());
        //bug 3920154 - build field types to make the types generic.    
        table = GolfClub.buildGOLF_CLUBTable();
        schemaManager.buildFieldTypes(table);
        schemaManager.replaceObject(table);

        table = Manufacturer.buildMANUFACTURERTable();
        schemaManager.buildFieldTypes(table);
        schemaManager.replaceObject(table);

        (new SwitchTableCreator()).replaceTables(session);
        
        new AggregateRelationshipsTableCreator().replaceTables(session);
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public TableDefinition getRelationTable() {
        TableDefinition definition = new TableDefinition();

        definition.setName("EMP_LAN");

        definition.addField("EMP_ID", java.math.BigDecimal.class);
        definition.addField("LAN_ID", java.math.BigDecimal.class);

        return definition;
    }

    public void populate(DatabaseSession session) {
        Object instance;
        PopulationManager manager = PopulationManager.getDefaultManager();

        instance = Employee1.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "example1");

        instance = Employee1.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "example2");

        // ==============================
        instance = Language.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "example1");

        instance = Language.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "example2");

        instance = Language.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "example3");

        instance = Language.example4();
        session.writeObject(instance);
        manager.registerObject(instance, "example4");

        instance = Language.example5();
        session.writeObject(instance);
        manager.registerObject(instance, "example5");

        instance = Language.example6();
        session.writeObject(instance);
        manager.registerObject(instance, "example6");

        instance = Employee.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "example1");

        instance = Employee.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "example2");

        instance = Employee.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "example3");

        instance = Client.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "example1");

        instance = Client.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "example2");

        instance = Client.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "example3");

        instance = EvaluationClient.example1();
        session.writeObject(instance);
        manager.registerObject(instance, "example1");

        instance = EvaluationClient.example2();
        session.writeObject(instance);
        manager.registerObject(instance, "example2");

        instance = EvaluationClient.example3();
        session.writeObject(instance);
        manager.registerObject(instance, "example3");

        //populate tables for 1:m testing purpose
        instance = Agent.example1();
        //use uow to ensure that all parts get inserted not just privately owned parts
        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerObject(instance);
        uow.commit();
        manager.registerObject(instance, "example1");

        // Added May 5, 2000 - Jon D. for pr381
        Vehicle.loadTables(session);

        if(useNewAggregateCollection) {
            instance = Builder.example1();
            //use uow to ensure that all parts get inserted not just privately owned parts
            uow = session.acquireUnitOfWork();
            uow.registerObject(instance);
            uow.commit();
            manager.registerObject(instance, "example1");
        }
    }
}
