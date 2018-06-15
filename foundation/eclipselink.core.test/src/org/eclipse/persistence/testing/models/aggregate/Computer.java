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
package org.eclipse.persistence.testing.models.aggregate;

import java.io.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Computer implements Serializable {
    public Oid id;
    public String description;

    public Computer() {
        super();
        setOid(new Oid());
    }

    public static Computer example1() {
        Computer example = new Computer();

        example.setDescription("Sun Workstation");
        ;
        return example;
    }

    public static Computer example2() {
        Computer example = new Computer();

        example.setDescription("PC 486");
        ;
        return example;
    }

    public static Computer example3() {
        Computer example = new Computer();

        example.setDescription("IBM Main Frame");
        ;
        return example;
    }

    public static Computer example4() {
        Computer example = new Computer();

        example.setDescription("Computer Description changed");
        ;
        return example;
    }

    public String getDescription() {
        return description;
    }

    public Oid getOid() {
        return id;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }

    public void setOid(Oid i) {
        id = i;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("AGG_COM");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("DESCRIP", String.class, 30);

        return definition;
    }
}
