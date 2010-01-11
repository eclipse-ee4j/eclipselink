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

import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;

/**
 * This type was created in VisualAge.
 */
public class Vehicle {
    public String colour;
    public int capacity;

    //Used for bug4719341 fix
    private transient String transientValue;

    public String getTransientValue() {
        return transientValue;
    }
        
    public void setTransientValue(String transientValue) {
        this.transientValue = transientValue;
    }

    /**
     * Vehicle constructor comment.
     */
    public Vehicle() {
        super();
    }

    /**
     * This method was created in VisualAge.
     * @return int
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.String
     */
    public String getColour() {
        return colour;
    }

    /**
     * This method was created in VisualAge.
     */
    public static void loadTables(Session session) {
        try {
            UnitOfWork uow = session.acquireUnitOfWork();
            uow.registerNewObject(Transport.example2());
            uow.registerNewObject(Transport.example3());
            uow.registerNewObject(Transport.example4());
            uow.registerNewObject(Transport.example5());
            uow.commit();
            session.getIdentityMapAccessor().initializeIdentityMaps();
        } catch (Exception e) {
            System.out.println("An exception ocurred loading the database -> " + e);
        }
    }

    /**
     * This method was created in VisualAge.
     * @param newValue int
     */
    public void setCapacity(int newValue) {
        this.capacity = newValue;
    }

    /**
     * This method was created in VisualAge.
     * @param newValue java.lang.String
     */
    public void setColour(String newValue) {
        this.colour = newValue;
    }

    /**
     * This method was created in VisualAge.
     * @param session org.eclipse.persistence.sessions.Session
     */
    public static void setupDatabase(Session session) {
        try {
            SchemaManager schemaManager = new SchemaManager((DatabaseSession)session);
            schemaManager.replaceObject(Vehicle.tableDefinition());
        } catch (Exception e) {
            System.out.println("Replace object failed because -> " + e);
        }
        try {
            Vehicle.loadTables(session);
        } catch (DatabaseException dbe) {
            System.out.println("an exception was caught setting up the db -> " + dbe);
        }
    }

    /**
     * This method was created in VisualAge.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("AGGVEHICLE");

        definition.addPrimaryKeyField("ID", java.math.BigDecimal.class, 15);// changed from 38 so test can run on DB2
        definition.addField("TYPE", java.math.BigDecimal.class, 4);
        definition.addField("COLOUR", String.class, 255);
        definition.addField("CAPACITY", java.math.BigDecimal.class, 2);
        definition.addField("BICYCLE_DESC", String.class, 255);
        definition.addField("CAR_MAKE", String.class, 255);
        definition.addField("CAR_MODEL", String.class, 255);

        return definition;
    }
}
