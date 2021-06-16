/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
