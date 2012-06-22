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


package org.eclipse.persistence.testing.models.jpa.xml.relationships;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class RelationshipsTableManager extends TableCreator {

    public static TableCreator tableCreator;

    public RelationshipsTableManager() {
        setName("Relationships");
        addTableDefinition(buildCUSTOMERTable());
        addTableDefinition(buildITEMTable());
        addTableDefinition(buildORDERTable());
        addTableDefinition(buildORDERCARDTable());
        addTableDefinition(buildORDERLABELTable());
        addTableDefinition(buildAUDITORTable());
        addTableDefinition(buildORDER_AUDITORTable());
        addTableDefinition(buildORDER_ORDERCARDTable());
        addTableDefinition(buildORDER_ORDERLABELTable());
        addTableDefinition(buildMATTELTable());
        addTableDefinition(buildLEGOTable());
        addTableDefinition(buildMEGABRANDSTable());
        addTableDefinition(buildNAMCOTable());
        addTableDefinition(buildCEOOTable());
    }
        
    public static TableCreator getCreator(){
        if (RelationshipsTableManager.tableCreator == null){
            RelationshipsTableManager.tableCreator = new RelationshipsTableManager();
        }
        return RelationshipsTableManager.tableCreator;
    }
    
    public static TableDefinition buildORDER_AUDITORTable() {
        TableDefinition table = new TableDefinition();

        table.setName("XML_ORDER_AUDITOR");

        FieldDefinition fieldORDERID = new FieldDefinition();
        fieldORDERID.setName("ORDER_ID");
        fieldORDERID.setTypeName("NUMERIC");
        fieldORDERID.setSize(15);
        fieldORDERID.setShouldAllowNull(false);
        fieldORDERID.setIsPrimaryKey(false);
        fieldORDERID.setUnique(false);
        fieldORDERID.setIsIdentity(false);
        fieldORDERID.setForeignKeyFieldName("XML_ORDER.ORDER_ID");
        table.addField(fieldORDERID);
        
        FieldDefinition fieldAUDITORID = new FieldDefinition();
        fieldAUDITORID.setName("AUDITOR_ID");
        fieldAUDITORID.setTypeName("NUMERIC");
        fieldAUDITORID.setSize(15);
        fieldAUDITORID.setShouldAllowNull(false);
        fieldAUDITORID.setIsPrimaryKey(false);
        fieldAUDITORID.setUnique(false);
        fieldAUDITORID.setIsIdentity(false);
        fieldAUDITORID.setForeignKeyFieldName("XML_AUDITOR.ID");
        table.addField(fieldAUDITORID);
    
        return table;
    }
    
    public static TableDefinition buildORDER_ORDERCARDTable() {
        TableDefinition table = new TableDefinition();

        table.setName("XML_ORDER_CARD_XML_ORDER");

        FieldDefinition fieldORDERID = new FieldDefinition();
        fieldORDERID.setName("order_ORDER_ID");
        fieldORDERID.setTypeName("NUMERIC");
        fieldORDERID.setSize(15);
        fieldORDERID.setShouldAllowNull(false);
        fieldORDERID.setIsPrimaryKey(false);
        fieldORDERID.setUnique(false);
        fieldORDERID.setIsIdentity(false);
        fieldORDERID.setForeignKeyFieldName("XML_ORDER.ORDER_ID");
        table.addField(fieldORDERID);
        
        FieldDefinition fieldAUDITORID = new FieldDefinition();
        fieldAUDITORID.setName("XMLOrderCard_ID");
        fieldAUDITORID.setTypeName("NUMERIC");
        fieldAUDITORID.setSize(15);
        fieldAUDITORID.setShouldAllowNull(false);
        fieldAUDITORID.setIsPrimaryKey(false);
        fieldAUDITORID.setUnique(false);
        fieldAUDITORID.setIsIdentity(false);
        fieldAUDITORID.setForeignKeyFieldName("XML_ORDER_CARD.ID");
        table.addField(fieldAUDITORID);
    
        return table;
    }
    
    public static TableDefinition buildORDER_ORDERLABELTable() {
        TableDefinition table = new TableDefinition();

        table.setName("XML_ORDER_ORDER_LABEL");

        FieldDefinition fieldORDERID = new FieldDefinition();
        fieldORDERID.setName("ORDER_ID");
        fieldORDERID.setTypeName("NUMERIC");
        fieldORDERID.setSize(15);
        fieldORDERID.setShouldAllowNull(false);
        fieldORDERID.setIsPrimaryKey(false);
        fieldORDERID.setUnique(false);
        fieldORDERID.setIsIdentity(false);
        fieldORDERID.setForeignKeyFieldName("XML_ORDER.ORDER_ID");
        table.addField(fieldORDERID);
        
        FieldDefinition fieldAUDITORID = new FieldDefinition();
        fieldAUDITORID.setName("ORDER_LABEL_ID");
        fieldAUDITORID.setTypeName("NUMERIC");
        fieldAUDITORID.setSize(15);
        fieldAUDITORID.setShouldAllowNull(false);
        fieldAUDITORID.setIsPrimaryKey(false);
        fieldAUDITORID.setUnique(false);
        fieldAUDITORID.setIsIdentity(false);
        fieldAUDITORID.setForeignKeyFieldName("XML_ORDER_LABEL.ID");
        table.addField(fieldAUDITORID);
    
        return table;
    }
    
    public static TableDefinition buildORDERCARDTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_ORDER_CARD");

        FieldDefinition fieldORDERLABEL_ID = new FieldDefinition();
        fieldORDERLABEL_ID.setName("ID");
        fieldORDERLABEL_ID.setTypeName("NUMBER");
        fieldORDERLABEL_ID.setSize(15);
        fieldORDERLABEL_ID.setSubSize(0);
        fieldORDERLABEL_ID.setIsPrimaryKey(true);
        fieldORDERLABEL_ID.setIsIdentity(false);
        fieldORDERLABEL_ID.setUnique(false);
        fieldORDERLABEL_ID.setShouldAllowNull(false);
        table.addField(fieldORDERLABEL_ID);

        return table;
    }
    
    public static TableDefinition buildORDERLABELTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_ORDER_LABEL");

        FieldDefinition fieldORDERLABEL_ID = new FieldDefinition();
        fieldORDERLABEL_ID.setName("ID");
        fieldORDERLABEL_ID.setTypeName("NUMBER");
        fieldORDERLABEL_ID.setSize(15);
        fieldORDERLABEL_ID.setSubSize(0);
        fieldORDERLABEL_ID.setIsPrimaryKey(true);
        fieldORDERLABEL_ID.setIsIdentity(false);
        fieldORDERLABEL_ID.setUnique(false);
        fieldORDERLABEL_ID.setShouldAllowNull(false);
        table.addField(fieldORDERLABEL_ID);

        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("DESCRIP");
        fieldDESCRIPTION.setTypeName("VARCHAR2");
        fieldDESCRIPTION.setSize(80);
        fieldDESCRIPTION.setSubSize(0);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setIsIdentity(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setShouldAllowNull(true);
        table.addField(fieldDESCRIPTION);

        return table;
    }
    
    public static TableDefinition buildAUDITORTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_AUDITOR");

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
        fieldNAME.setSize(80);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);

        return table;
    }
    
    public static TableDefinition buildCUSTOMERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CUSTOMER");

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

    public static TableDefinition buildITEMTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_ITEM");

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
        fieldITEM_ID.setName("ITEM_ID");
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

        FieldDefinition fieldDTYPE = new FieldDefinition();
        fieldDTYPE.setName("DTYPE");
        fieldDTYPE.setTypeName("VARCHAR2");
        fieldDTYPE.setSize(80);
        fieldDTYPE.setSubSize(0);
        fieldDTYPE.setIsPrimaryKey(false);
        fieldDTYPE.setIsIdentity(false);
        fieldDTYPE.setUnique(false);
        fieldDTYPE.setShouldAllowNull(true);
        table.addField(fieldDTYPE);

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
        
        FieldDefinition fieldDISTRIBUTOR_TYPE = new FieldDefinition();
        fieldDISTRIBUTOR_TYPE.setName("DISTRIBUTOR_TYPE");
        fieldDISTRIBUTOR_TYPE.setTypeName("VARCHAR2");
        fieldDISTRIBUTOR_TYPE.setSize(2);
        fieldDISTRIBUTOR_TYPE.setSubSize(0);
        fieldDISTRIBUTOR_TYPE.setIsPrimaryKey(false);
        fieldDISTRIBUTOR_TYPE.setIsIdentity(false);
        fieldDISTRIBUTOR_TYPE.setUnique(false);
        fieldDISTRIBUTOR_TYPE.setShouldAllowNull(true);
        table.addField(fieldDISTRIBUTOR_TYPE);
        
        FieldDefinition fieldDISTRIBUTOR_ID = new FieldDefinition();
        fieldDISTRIBUTOR_ID.setName("DISTRIBUTOR_ID");
        fieldDISTRIBUTOR_ID.setTypeName("NUMBER");
        fieldDISTRIBUTOR_ID.setSize(15);
        fieldDISTRIBUTOR_ID.setSubSize(0);
        fieldDISTRIBUTOR_ID.setIsPrimaryKey(false);
        fieldDISTRIBUTOR_ID.setIsIdentity(false);
        fieldDISTRIBUTOR_ID.setUnique(false);
        fieldDISTRIBUTOR_ID.setShouldAllowNull(true);
        table.addField(fieldDISTRIBUTOR_ID);
        return table;
    }

    public static TableDefinition buildORDERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_ORDER");

        FieldDefinition fieldCUST_ID = new FieldDefinition();
        fieldCUST_ID.setName("CUST_ID");
        fieldCUST_ID.setTypeName("NUMBER");
        fieldCUST_ID.setSize(15);
        fieldCUST_ID.setSubSize(0);
        fieldCUST_ID.setIsPrimaryKey(false);
        fieldCUST_ID.setIsIdentity(false);
        fieldCUST_ID.setUnique(false);
        fieldCUST_ID.setShouldAllowNull(true);
        fieldCUST_ID.setForeignKeyFieldName("XML_CUSTOMER.CUST_ID");
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
        fieldITEM_ID.setForeignKeyFieldName("XML_ITEM.ITEM_ID");
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

    public static TableDefinition buildLEGOTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_LEGO");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("COMPANY_NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(80);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);
        
        FieldDefinition fieldCEOID = new FieldDefinition();
        fieldCEOID.setName("CEO_ID");
        fieldCEOID.setTypeName("NUMERIC");
        fieldCEOID.setSize(15);
        fieldCEOID.setShouldAllowNull(true);
        fieldCEOID.setIsPrimaryKey(false);
        fieldCEOID.setUnique(false);
        fieldCEOID.setIsIdentity(false);
        fieldCEOID.setForeignKeyFieldName("XML_CEO.ID");
        table.addField(fieldCEOID);

        return table;
    }
    public static TableDefinition buildMATTELTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MATTEL");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("COMPANY_NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(80);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);
        
        FieldDefinition fieldCEOID = new FieldDefinition();
        fieldCEOID.setName("CEO_ID");
        fieldCEOID.setTypeName("NUMERIC");
        fieldCEOID.setSize(15);
        fieldCEOID.setShouldAllowNull(true);
        fieldCEOID.setIsPrimaryKey(false);
        fieldCEOID.setUnique(false);
        fieldCEOID.setIsIdentity(false);
        fieldCEOID.setForeignKeyFieldName("XML_CEO.ID");
        table.addField(fieldCEOID);

        return table;
    }
    public static TableDefinition buildMEGABRANDSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MEGABRANDS");

        FieldDefinition fieldCUST_ID = new FieldDefinition();
        fieldCUST_ID.setName("DISTRIBUTORID");
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
        
        FieldDefinition fieldCHIEFID = new FieldDefinition();
        fieldCHIEFID.setName("CHIEF_ID");
        fieldCHIEFID.setTypeName("NUMERIC");
        fieldCHIEFID.setSize(15);
        fieldCHIEFID.setShouldAllowNull(true);
        fieldCHIEFID.setIsPrimaryKey(false);
        fieldCHIEFID.setUnique(false);
        fieldCHIEFID.setIsIdentity(false);
        fieldCHIEFID.setForeignKeyFieldName("XML_CEO.ID");
        table.addField(fieldCHIEFID);

        return table;
    }
    
    public static TableDefinition buildNAMCOTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_NAMCO");

        FieldDefinition fieldCUST_ID = new FieldDefinition();
        fieldCUST_ID.setName("DISTRIBUTORID");
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
        
        FieldDefinition fieldCHIEFID = new FieldDefinition();
        fieldCHIEFID.setName("CHIEF_ID");
        fieldCHIEFID.setTypeName("NUMERIC");
        fieldCHIEFID.setSize(15);
        fieldCHIEFID.setShouldAllowNull(true);
        fieldCHIEFID.setIsPrimaryKey(false);
        fieldCHIEFID.setUnique(false);
        fieldCHIEFID.setIsIdentity(false);
        fieldCHIEFID.setForeignKeyFieldName("XML_CEO.ID");
        table.addField(fieldCHIEFID);

        return table;
    }
    
    public static TableDefinition buildCEOOTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CEO");

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
        fieldNAME.setSize(80);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);

        return table;
    }
}
