/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
package org.eclipse.persistence.testing.models.jpa.xml.merge.relationships;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class RelationshipsTableManager extends TableCreator {

    public static TableCreator tableCreator;

    public RelationshipsTableManager() {
        setName("Relationships");
        addTableDefinition(buildCMP3_CUSTOMERTable());
        addTableDefinition(buildCMP3_ITEMTable());
        addTableDefinition(buildCMP3_ORDERTable());
        addTableDefinition(buildCMP3_PARTSLISTTable());
        addTableDefinition(buildCMP3_PARTSLIST_ITEMTable());
    }

    public static TableCreator getCreator(){
        if (RelationshipsTableManager.tableCreator == null){
            RelationshipsTableManager.tableCreator = new RelationshipsTableManager();
        }
        return RelationshipsTableManager.tableCreator;
    }

    public static TableDefinition buildCMP3_CUSTOMERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_XML_MERGE_CUSTOMER");

        FieldDefinition fieldCITY = new FieldDefinition();
        fieldCITY.setName("CITY");
        fieldCITY.setTypeName("VARCHAR2");
        fieldCITY.setSize(80);
        fieldCITY.setSubSize(0);
        fieldCITY.setIsPrimaryKey(false);
        fieldCITY.setIsIdentity(false);
        fieldCITY.setUnique(false);
        fieldCITY.setShouldAllowNull(true);
        table.addField(fieldCITY);

        FieldDefinition fieldCUST_ID = new FieldDefinition();
        fieldCUST_ID.setName("CUST_ID");
        fieldCUST_ID.setTypeName("NUMBER");
        fieldCUST_ID.setSize(15);
        fieldCUST_ID.setSubSize(0);
        fieldCUST_ID.setIsPrimaryKey(true);
        fieldCUST_ID.setIsIdentity(false);
        fieldCUST_ID.setUnique(false);
        fieldCUST_ID.setShouldAllowNull(false);
        table.addField(fieldCUST_ID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(80);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);

        FieldDefinition field10 = new FieldDefinition();
        field10.setName("CUST_VERSION");
        field10.setTypeName("NUMERIC");
        field10.setSize(15);
        field10.setShouldAllowNull(true );
        field10.setIsPrimaryKey(false );
        field10.setUnique(false );
        field10.setIsIdentity(false );
        table.addField(field10);

        return table;
    }

    public static TableDefinition buildCMP3_ITEMTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_XML_MERGE_ITEM");

        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("DESCRIPTION");
        fieldDESCRIPTION.setTypeName("VARCHAR2");
        fieldDESCRIPTION.setSize(80);
        fieldDESCRIPTION.setSubSize(0);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setIsIdentity(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setShouldAllowNull(true);
        table.addField(fieldDESCRIPTION);

        FieldDefinition fieldITEM_ID = new FieldDefinition();
        fieldITEM_ID.setName("ID");
        fieldITEM_ID.setTypeName("NUMBER");
        fieldITEM_ID.setSize(15);
        fieldITEM_ID.setSubSize(0);
        fieldITEM_ID.setIsPrimaryKey(true);
        fieldITEM_ID.setIsIdentity(false);
        fieldITEM_ID.setUnique(false);
        fieldITEM_ID.setShouldAllowNull(false);
        table.addField(fieldITEM_ID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(80);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);

        FieldDefinition field10 = new FieldDefinition();
        field10.setName("ITEM_VERSION");
        field10.setTypeName("NUMERIC");
        field10.setSize(15);
        field10.setShouldAllowNull(true );
        field10.setIsPrimaryKey(false );
        field10.setUnique(false );
        field10.setIsIdentity(false );
        table.addField(field10);

        FieldDefinition fieldIMAGE = new FieldDefinition();
        fieldIMAGE.setName("IMAGE");
        fieldIMAGE.setTypeName("BLOB");
        fieldIMAGE.setSize(1280);
        fieldIMAGE.setShouldAllowNull(true);
        table.addField(fieldIMAGE);

        return table;
    }

    public static TableDefinition buildCMP3_ORDERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_XML_MERGE_ORDER");

        FieldDefinition fieldCUST_ID = new FieldDefinition();
        fieldCUST_ID.setName("CUSTOMER_CUST_ID");
        fieldCUST_ID.setTypeName("NUMBER");
        fieldCUST_ID.setSize(15);
        fieldCUST_ID.setSubSize(0);
        fieldCUST_ID.setIsPrimaryKey(false);
        fieldCUST_ID.setIsIdentity(false);
        fieldCUST_ID.setUnique(false);
        fieldCUST_ID.setShouldAllowNull(true);
        table.addField(fieldCUST_ID);

        FieldDefinition fieldITEM_ID = new FieldDefinition();
        fieldITEM_ID.setName("ITEM_ID");
        fieldITEM_ID.setTypeName("NUMBER");
        fieldITEM_ID.setSize(15);
        fieldITEM_ID.setSubSize(0);
        fieldITEM_ID.setIsPrimaryKey(false);
        fieldITEM_ID.setIsIdentity(false);
        fieldITEM_ID.setUnique(false);
        fieldITEM_ID.setShouldAllowNull(true);
        fieldITEM_ID.setForeignKeyFieldName("CMP3_XML_MERGE_ITEM.ID");
        table.addField(fieldITEM_ID);

        FieldDefinition fieldORDER_ID = new FieldDefinition();
        fieldORDER_ID.setName("ORDER_ID");
        fieldORDER_ID.setTypeName("NUMBER");
        fieldORDER_ID.setSize(15);
        fieldORDER_ID.setSubSize(0);
        fieldORDER_ID.setIsPrimaryKey(true);
        fieldORDER_ID.setIsIdentity(false);
        fieldORDER_ID.setUnique(false);
        fieldORDER_ID.setShouldAllowNull(false);
        table.addField(fieldORDER_ID);

        FieldDefinition fieldQUANTITY = new FieldDefinition();
        fieldQUANTITY.setName("QUANTITY");
        fieldQUANTITY.setTypeName("NUMBER");
        fieldQUANTITY.setSize(15);
        fieldQUANTITY.setSubSize(0);
        fieldQUANTITY.setIsPrimaryKey(false);
        fieldQUANTITY.setIsIdentity(false);
        fieldQUANTITY.setUnique(false);
        fieldQUANTITY.setShouldAllowNull(false);
        table.addField(fieldQUANTITY);

        FieldDefinition fieldSHIP_ADDR = new FieldDefinition();
        fieldSHIP_ADDR.setName("SHIP_ADDR");
        fieldSHIP_ADDR.setTypeName("VARCHAR2");
        fieldSHIP_ADDR.setSize(80);
        fieldSHIP_ADDR.setSubSize(0);
        fieldSHIP_ADDR.setIsPrimaryKey(false);
        fieldSHIP_ADDR.setIsIdentity(false);
        fieldSHIP_ADDR.setUnique(false);
        fieldSHIP_ADDR.setShouldAllowNull(true);
        table.addField(fieldSHIP_ADDR);

        FieldDefinition field10 = new FieldDefinition();
        field10.setName("ORDER_VERSION");
        field10.setTypeName("NUMERIC");
        field10.setSize(15);
        field10.setShouldAllowNull(true );
        field10.setIsPrimaryKey(false );
        field10.setUnique(false );
        field10.setIsIdentity(false );
        table.addField(field10);

       return table;
    }

    public static TableDefinition buildCMP3_PARTSLISTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_XML_MERGE_PARTSLIST");

        FieldDefinition fieldPARTSLIST_ID = new FieldDefinition();
        fieldPARTSLIST_ID.setName("ID");
        fieldPARTSLIST_ID.setTypeName("NUMBER");
        fieldPARTSLIST_ID.setSize(15);
        fieldPARTSLIST_ID.setSubSize(0);
        fieldPARTSLIST_ID.setIsPrimaryKey(true);
        fieldPARTSLIST_ID.setIsIdentity(false);
        fieldPARTSLIST_ID.setUnique(false);
        fieldPARTSLIST_ID.setShouldAllowNull(false);
        table.addField(fieldPARTSLIST_ID);

        FieldDefinition field10 = new FieldDefinition();
        field10.setName("VERSION");
        field10.setTypeName("NUMERIC");
        field10.setSize(15);
        field10.setShouldAllowNull(true );
        field10.setIsPrimaryKey(false );
        field10.setUnique(false );
        field10.setIsIdentity(false );
        table.addField(field10);

        return table;
    }

    public static TableDefinition buildCMP3_PARTSLIST_ITEMTable() {
        TableDefinition table = new TableDefinition();

        table.setName("CMP3_XML_MERGE_PARTSLIST_ITEM");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("PARTSLIST_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("CMP3_XML_MERGE_ITEM.ID");
        table.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("ITEM_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false );
        field1.setIsPrimaryKey(true );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        field1.setForeignKeyFieldName("CMP3_XML_MERGE_PARTSLIST.ID");
        table.addField(field1);

        return table;
    }
}
