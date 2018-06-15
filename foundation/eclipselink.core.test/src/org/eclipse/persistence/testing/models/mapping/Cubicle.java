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
package org.eclipse.persistence.testing.models.mapping;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Cubicle implements Serializable {
    public Number id;
    public String location;
    public Vector emergencyExits;

    /**
     * This method was created by a SmartGuide.
     */
    public Cubicle() {
        this.emergencyExits = new Vector(1);
    }

    public static Cubicle example1() {
        Cubicle example = new Cubicle();
        example.setLocation("3rd floor, Section R, Third qubicle on left");
        return example;
    }

    public static Cubicle example2() {
        Cubicle example = new Cubicle();
        example.setLocation("2nd floor, Section P, Close to the Middle");
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

        definition.setName("MAP_CUB");

        definition.addPrimaryKeyField("C_ID", String.class, 15);
        definition.addField("LOCATION", String.class, 255);
        return definition;
    }
}
