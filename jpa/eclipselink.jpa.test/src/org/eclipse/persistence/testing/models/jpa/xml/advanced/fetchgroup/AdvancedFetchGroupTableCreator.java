/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/19/2010-2.1 Guy Pelletier 
 *       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.advanced.fetchgroup;

import org.eclipse.persistence.tools.schemaframework.*;

public class AdvancedFetchGroupTableCreator extends TableCreator {
    public AdvancedFetchGroupTableCreator() {
        setName("XMLAdvancedFetchGroupTableCreator");

        addTableDefinition(buildHOCKEYGEARTable());
        addTableDefinition(buildPADSTable());
        addTableDefinition(buildCHESTPROTECTORTable());
    }
    
    public static TableDefinition buildHOCKEYGEARTable(){
        TableDefinition table = new TableDefinition();
        table.setName("XML_HOCKEY_GEAR");
        
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
        table.setName("XML_PADS");
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
        table.setName("XML_CHEST_PROTECT");

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
}

