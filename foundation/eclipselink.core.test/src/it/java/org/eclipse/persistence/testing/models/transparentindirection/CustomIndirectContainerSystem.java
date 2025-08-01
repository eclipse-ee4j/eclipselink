/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.transparentindirection;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * This system just extends the transparent indirection system with a single class Dog,
 * which uses custom indirect container (SalesRepContainer) for its 'owner' (SalesRep).
 */
public class CustomIndirectContainerSystem extends TestSystem {

    public CustomIndirectContainerSystem() {
        this.project = new CustomIndirectContainerProject();
    }

    @Override
    public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(project);
    }

    @Override
    public void createTables(DatabaseSession session) {
        TableCreator tableCreator = new GeneratedIndirectContainerTableCreator();
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("DOG");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("INTEGER");
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(50);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        field = new FieldDefinition();
        field.setName("SREP_ID");
        field.setTypeName("INTEGER");
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        tabledefinition.addField(field);

        tableCreator.addTableDefinition(tabledefinition);
        tableCreator.replaceTables(session);
    }

    public Dog dogExample1() {
        return new Dog("Bart");
    }

    @Override
    public void populate(DatabaseSession session) {
        PopulationManager manager = PopulationManager.getDefaultManager();

        SalesRep srep = salesRepExample1();
        (session).writeObject(srep);
        manager.registerObject(srep, srep.getKey());

        Dog dog = dogExample1();
        dog.setOwner(srep);
        (session).writeObject(dog);
        manager.registerObject(dog, dog.getName());

        srep = salesRepExample2();
        (session).writeObject(srep);
        manager.registerObject(srep, srep.getKey());
    }

    public SalesRep salesRepExample1() {
        return new SalesRep("Joey");
    }

    public SalesRep salesRepExample2() {
        return new SalesRep("Jerry");
    }
}
