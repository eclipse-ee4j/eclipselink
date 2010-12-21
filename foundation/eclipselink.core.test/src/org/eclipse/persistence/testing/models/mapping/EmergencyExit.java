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
package org.eclipse.persistence.testing.models.mapping;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class EmergencyExit implements Serializable {
    public String id;
    public String location;
    public Vector emergencyExits;

    /**
     * This method was created by a SmartGuide.
     */
    public EmergencyExit() {
    }

    public static EmergencyExit example1() {
        EmergencyExit example = new EmergencyExit();
        example.setLocation("3rd floor, Section P");
        return example;
    }

    public static EmergencyExit example2() {
        EmergencyExit example = new EmergencyExit();
        example.setLocation("2nd floor, Section P");
        return example;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_EMERGENCYEXIT");

        definition.addPrimaryKeyField("EXIT_ID", String.class, 15);
        definition.addField("LOCATION", String.class, 255);
        return definition;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition relationTableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("CUBICLE_EMERGENCYEXIT");

        definition.addPrimaryKeyField("EXIT_ID", String.class, 15);
        definition.addPrimaryKeyField("CUBICLE_LOCATION", String.class, 255);
        return definition;
    }
}
