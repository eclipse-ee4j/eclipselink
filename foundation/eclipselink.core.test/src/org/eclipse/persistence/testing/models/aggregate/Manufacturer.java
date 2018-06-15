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

import java.math.BigDecimal;
import org.eclipse.persistence.tools.schemaframework.*;

/**
 *  The following is the object structure of this model:
 *  GolfClub - AggregateObject -> GolfClubShaft
 *  GolfClub - One-To-One -> Manufacturer
 *  GolfClubShaft - One-To-One -> Manufacturer
 *  This structure is special because both GolfClub and GolfClubShaft have an attribute
 *  called manufacturer.  This is useful for joining tests.
 */
public class Manufacturer {
    public BigDecimal id;
    public String name;

    public Manufacturer() {
        super();
    }

    public BigDecimal getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static TableDefinition buildMANUFACTURERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MANUFACTURER");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(20);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);

        return table;
    }
}
