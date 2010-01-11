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
public class GolfClub {
    public BigDecimal id;
    public GolfClubShaft shaft;
    public Manufacturer manufacturer;

    public GolfClub() {
        super();
        this.shaft = 
            /* The instance variable "shaft" is mapped as an aggregate that does not allow null.  It must be initialized here. */
            new org.eclipse.persistence.testing.models.aggregate.GolfClubShaft();
    }

    public BigDecimal getId() {
        return this.id;
    }

    public Manufacturer getManufacturer() {
        return this.manufacturer;
    }

    public GolfClubShaft getShaft() {
        return this.shaft;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setShaft(GolfClubShaft shaft) {
        this.shaft = shaft;
    }

    public static TableDefinition buildGOLF_CLUBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("GOLF_CLUB");

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

        FieldDefinition fieldMANUFACTURER_ID = new FieldDefinition();
        fieldMANUFACTURER_ID.setName("MANUFACTURER_ID");
        fieldMANUFACTURER_ID.setTypeName("NUMBER");
        fieldMANUFACTURER_ID.setSize(15);
        fieldMANUFACTURER_ID.setSubSize(0);
        fieldMANUFACTURER_ID.setIsPrimaryKey(false);
        fieldMANUFACTURER_ID.setIsIdentity(false);
        fieldMANUFACTURER_ID.setUnique(false);
        fieldMANUFACTURER_ID.setShouldAllowNull(true);
        table.addField(fieldMANUFACTURER_ID);

        FieldDefinition fieldSHAFT_STIFFNESS = new FieldDefinition();
        fieldSHAFT_STIFFNESS.setName("SHAFT_STIFFNESS");
        fieldSHAFT_STIFFNESS.setTypeName("VARCHAR2");
        fieldSHAFT_STIFFNESS.setSize(20);
        fieldSHAFT_STIFFNESS.setSubSize(0);
        fieldSHAFT_STIFFNESS.setIsPrimaryKey(false);
        fieldSHAFT_STIFFNESS.setIsIdentity(false);
        fieldSHAFT_STIFFNESS.setUnique(false);
        fieldSHAFT_STIFFNESS.setShouldAllowNull(true);
        table.addField(fieldSHAFT_STIFFNESS);

        FieldDefinition fieldSHAFT_MANUFACTURER_ID = new FieldDefinition();
        fieldSHAFT_MANUFACTURER_ID.setName("SHAFT_MANUFACTURER_ID");
        fieldSHAFT_MANUFACTURER_ID.setTypeName("NUMBER");
        fieldSHAFT_MANUFACTURER_ID.setSize(15);
        fieldSHAFT_MANUFACTURER_ID.setSubSize(0);
        fieldSHAFT_MANUFACTURER_ID.setIsPrimaryKey(false);
        fieldSHAFT_MANUFACTURER_ID.setIsIdentity(false);
        fieldSHAFT_MANUFACTURER_ID.setUnique(false);
        fieldSHAFT_MANUFACTURER_ID.setShouldAllowNull(true);
        table.addField(fieldSHAFT_MANUFACTURER_ID);

        return table;
    }
}
