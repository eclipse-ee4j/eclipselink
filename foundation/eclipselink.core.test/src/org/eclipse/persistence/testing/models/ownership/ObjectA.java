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
package org.eclipse.persistence.testing.models.ownership;

import java.io.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.tools.schemaframework.*;

public class ObjectA implements Serializable {
    public Number id;
    public String name;
    public ValueHolderInterface oneToOne;

    public ObjectA() {
        oneToOne = new ValueHolder();
    }

    public static ObjectA example1() {
        ObjectA example = new ObjectA();

        example.getOneToOne().setValue(ObjectB.example1());
        example.setName("A1");
        return example;
    }

    public static ObjectA example2() {
        ObjectA example = new ObjectA();

        example.getOneToOne().setValue(ObjectB.example2());
        example.setName("A2");
        return example;
    }

    public static ObjectA example3() {
        ObjectA example = new ObjectA();

        example.getOneToOne().setValue(ObjectB.example3());
        example.setName("A3");
        return example;
    }

    public ValueHolderInterface getOneToOne() {
        return oneToOne;
    }

    public void setName(String aName) {
        name = aName;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("OWNER_A");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 10);
        definition.addField("ONE_TO_ONE_", java.math.BigDecimal.class, 15);
        definition.addForeignKeyConstraint("OWNER_A_ONE_TO_ONE", "ONE_TO_ONE_", "ID", "OWNER_B");

        return definition;
    }
}
