/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/19/2010-2.1 Guy Pelletier
//       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
//     01/16/2016-2.7 Mythily Parthasarathy
//         485984: Added SHELF table and reference to SHELF in HELMET
package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;

public class AdvancedFetchGroupTableCreator extends TogglingFastTableCreator {
    public AdvancedFetchGroupTableCreator() {
        setName("AdvancedFetchGroupTableCreator");

        addTableDefinition(buildHOCKEYGEARTable());
        addTableDefinition(buildPADSTable());
        addTableDefinition(buildCHESTPROTECTORTable());
        addTableDefinition(buildHELMETTable());
        addTableDefinition(buildHELMET_PROPERTIESTable());
        addTableDefinition(buildSHELFTable());
    }

    public static TableDefinition buildHOCKEYGEARTable(){
        TableDefinition table = new TableDefinition();
        table.setName("JPA_HOCKEY_GEAR");

        FieldDefinition fieldSERIAL_NUMBER = new FieldDefinition();
        fieldSERIAL_NUMBER.setName("SERIAL_NUMBER");
        fieldSERIAL_NUMBER.setTypeName("NUMERIC");
        fieldSERIAL_NUMBER.setSize(15);
        fieldSERIAL_NUMBER.setShouldAllowNull(false);
        fieldSERIAL_NUMBER.setIsPrimaryKey(true);
        fieldSERIAL_NUMBER.setUnique(false);
        fieldSERIAL_NUMBER.setIsIdentity(false);
        table.addField(fieldSERIAL_NUMBER);

        FieldDefinition fieldMSRP = new FieldDefinition();
        fieldMSRP.setName("MSRP");
        fieldMSRP.setTypeName("DOUBLE PRECIS");
        fieldMSRP.setSize(18);
        fieldMSRP.setShouldAllowNull(true);
        fieldMSRP.setIsPrimaryKey(false);
        fieldMSRP.setUnique(false);
        fieldMSRP.setIsIdentity(false);
        table.addField(fieldMSRP);

        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("DESCRIP");
        fieldDESCRIPTION.setTypeName("VARCHAR");
        fieldDESCRIPTION.setSize(40);
        fieldDESCRIPTION.setShouldAllowNull(true);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setIsIdentity(false);
        table.addField(fieldDESCRIPTION);

        FieldDefinition fieldGEARTYPE = new FieldDefinition();
        fieldGEARTYPE.setName("GEAR_TYPE");
        fieldGEARTYPE.setTypeName("VARCHAR");
        fieldGEARTYPE.setSize(10);
        fieldGEARTYPE.setShouldAllowNull(true);
        fieldGEARTYPE.setIsPrimaryKey(false);
        fieldGEARTYPE.setUnique(false);
        fieldGEARTYPE.setIsIdentity(false);
        table.addField(fieldGEARTYPE);

        return table;
    }

    public static TableDefinition buildPADSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_PADS");

        FieldDefinition fieldSERIAL_NUMBER = new FieldDefinition();
        fieldSERIAL_NUMBER.setName("SERIAL_NUMBER");
        fieldSERIAL_NUMBER.setTypeName("NUMERIC");
        fieldSERIAL_NUMBER.setSize(15);
        fieldSERIAL_NUMBER.setShouldAllowNull(false);
        fieldSERIAL_NUMBER.setIsPrimaryKey(true);
        fieldSERIAL_NUMBER.setUnique(false);
        fieldSERIAL_NUMBER.setIsIdentity(false);
        table.addField(fieldSERIAL_NUMBER);

        FieldDefinition fieldWEIGHT = new FieldDefinition();
        fieldWEIGHT.setName("WEIGHT");
        fieldWEIGHT.setTypeName("DOUBLE PRECIS");
        fieldWEIGHT.setSize(10);
        fieldWEIGHT.setShouldAllowNull(true);
        fieldWEIGHT.setIsPrimaryKey(false);
        fieldWEIGHT.setUnique(false);
        fieldWEIGHT.setIsIdentity(false);
        table.addField(fieldWEIGHT);

        FieldDefinition fieldHEIGHT = new FieldDefinition();
        fieldHEIGHT.setName("HEIGHT");
        fieldHEIGHT.setTypeName("DOUBLE PRECIS");
        fieldHEIGHT.setSize(10);
        fieldHEIGHT.setShouldAllowNull(true);
        fieldHEIGHT.setIsPrimaryKey(false);
        fieldHEIGHT.setUnique(false);
        fieldHEIGHT.setIsIdentity(false);
        table.addField(fieldHEIGHT);

        FieldDefinition fieldWIDTH = new FieldDefinition();
        fieldWIDTH.setName("WIDTH");
        fieldWIDTH.setTypeName("DOUBLE PRECIS");
        fieldWIDTH.setSize(10);
        fieldWIDTH.setShouldAllowNull(true);
        fieldWIDTH.setIsPrimaryKey(false);
        fieldWIDTH.setUnique(false);
        fieldWIDTH.setIsIdentity(false);
        table.addField(fieldWIDTH);

        FieldDefinition fieldAGEGROUP = new FieldDefinition();
        fieldAGEGROUP.setName("AGEGROUP");
        fieldAGEGROUP.setTypeName("NUMERIC");
        fieldAGEGROUP.setSize(15);
        fieldAGEGROUP.setIsPrimaryKey(false);
        fieldAGEGROUP.setUnique(false);
        fieldAGEGROUP.setIsIdentity(false);
        fieldAGEGROUP.setShouldAllowNull(true);
        table.addField(fieldAGEGROUP);

        return table;
    }

    public static TableDefinition buildCHESTPROTECTORTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CHEST_PROTECT");

        FieldDefinition fieldSERIAL_NUMBER = new FieldDefinition();
        fieldSERIAL_NUMBER.setName("SERIAL_NUMBER");
        fieldSERIAL_NUMBER.setTypeName("NUMERIC");
        fieldSERIAL_NUMBER.setSize(15);
        fieldSERIAL_NUMBER.setShouldAllowNull(false);
        fieldSERIAL_NUMBER.setIsPrimaryKey(true);
        fieldSERIAL_NUMBER.setUnique(false);
        fieldSERIAL_NUMBER.setIsIdentity(false);
        table.addField(fieldSERIAL_NUMBER);

        FieldDefinition fieldSIZE = new FieldDefinition();
        fieldSIZE.setName("C_SIZE");
        fieldSIZE.setTypeName("VARCHAR");
        fieldSIZE.setSize(40);
        fieldSIZE.setShouldAllowNull(true);
        fieldSIZE.setIsPrimaryKey(false);
        fieldSIZE.setUnique(false);
        fieldSIZE.setIsIdentity(false);
        table.addField(fieldSIZE);

        FieldDefinition fieldAGEGROUP = new FieldDefinition();
        fieldAGEGROUP.setName("AGEGROUP");
        fieldAGEGROUP.setTypeName("NUMERIC");
        fieldAGEGROUP.setSize(15);
        fieldAGEGROUP.setIsPrimaryKey(false);
        fieldAGEGROUP.setUnique(false);
        fieldAGEGROUP.setIsIdentity(false);
        fieldAGEGROUP.setShouldAllowNull(true);
        table.addField(fieldAGEGROUP);

        return table;
    }

    public static TableDefinition buildHELMETTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_HELMET");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);

        FieldDefinition fieldCOLOR = new FieldDefinition();
        fieldCOLOR.setName("COLOR");
        fieldCOLOR.setTypeName("VARCHAR");
        fieldCOLOR.setSize(42);
        fieldCOLOR.setShouldAllowNull(true);
        fieldCOLOR.setIsPrimaryKey(false);
        fieldCOLOR.setUnique(false);
        fieldCOLOR.setIsIdentity(false);
        table.addField(fieldCOLOR);
        
        FieldDefinition shelfFK = new FieldDefinition();
        shelfFK.setName("SHELF_ID");
        shelfFK.setTypeName("NUMERIC");
        shelfFK.setSize(15);
        shelfFK.setShouldAllowNull(true);
        shelfFK.setIsPrimaryKey(false);
        shelfFK.setUnique(false);
        shelfFK.setIsIdentity(false);
        table.addField(shelfFK);

        return table;
    }

    public static TableDefinition buildHELMET_PROPERTIESTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_HELMET_PROPERTIES");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("HELMET_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);

        FieldDefinition fieldPROPERTY_NAME = new FieldDefinition();
        fieldPROPERTY_NAME.setName("PROPERTY_NAME");
        fieldPROPERTY_NAME.setTypeName("VARCHAR");
        fieldPROPERTY_NAME.setSize(42);
        fieldPROPERTY_NAME.setShouldAllowNull(true);
        fieldPROPERTY_NAME.setIsPrimaryKey(false);
        fieldPROPERTY_NAME.setUnique(false);
        fieldPROPERTY_NAME.setIsIdentity(false);
        table.addField(fieldPROPERTY_NAME);

        FieldDefinition fieldPROPERTY_VALUE = new FieldDefinition();
        fieldPROPERTY_VALUE.setName("PROPERTY_VALUE");
        fieldPROPERTY_VALUE.setTypeName("VARCHAR");
        fieldPROPERTY_VALUE.setSize(42);
        fieldPROPERTY_VALUE.setShouldAllowNull(true);
        fieldPROPERTY_VALUE.setIsPrimaryKey(false);
        fieldPROPERTY_VALUE.setUnique(false);
        fieldPROPERTY_VALUE.setIsIdentity(false);
        table.addField(fieldPROPERTY_VALUE);

        return table;
    }
    
    public static TableDefinition buildSHELFTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_SHELF");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(42);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);

        return table;
    }

}

