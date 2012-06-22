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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     11/06/2009-2.0 Guy Pelletier 
 *       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 *     06/16/2010-2.2 Guy Pelletier 
 *       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
 *     10/15/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_3;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class AdvancedTableCreator_3 extends TogglingFastTableCreator {

    public AdvancedTableCreator_3() {
        setName("xml.composite.member_3");
        this.ignoreDatabaseException = true;
        // table names prefixed with "XML_MBR3_"

// 1        addTableDefinition(buildADDRESSTable());
        addTableDefinition(buildCREDITCARDSTable());
        addTableDefinition(buildCREDITLINESTable());
// 1        addTableDefinition(buildCUSTOMERTable());
        addTableDefinition(buildDEALERTable());
// 2        addTableDefinition(buildEMPLOYEETable());
        addTableDefinition(buildLARGEPROJECTTable());
//         addTableDefinition(buildMANTable());
//        addTableDefinition(buildPARTNERLINKTable());
        addTableDefinition(buildPHONENUMBERTable());
        addTableDefinition(buildPROJECT_EMPTable());
        addTableDefinition(buildPROJECTTable());
//        addTableDefinition(buildREADONLYCLASSTable());
        addTableDefinition(buildRESPONSTable());
// 2        addTableDefinition(buildSALARYTable());
/*        addTableDefinition(buildWOMANTable());
        
        // Tables used only in extended test model
        addTableDefinition(buildLONERTable());
        addTableDefinition(buildLONERCHARACTERISTICSTable());
        addTableDefinition(buildCONFIDANTTable());
        
        addTableDefinition(buildSHOVELTable());
        addTableDefinition(buildSHOVELDIGGERTable());
        addTableDefinition(buildSHOVELOWNERTable());
        addTableDefinition(buildSHOVELPROJECTTable());
        addTableDefinition(buildSHOVELPROJECTSTable());

        addTableDefinition(buildVIOLATIONTable());
        addTableDefinition(buildVIOLATIONCODETable());
        addTableDefinition(buildVIOLATIONCODESTable());

		addTableDefinition(buildSTUDENTTable());
        addTableDefinition(buildSCHOOLTable());
        addTableDefinition(buildBOLTTable());
        addTableDefinition(buildNUTTable());*/
    }
    
    public static TableDefinition buildCREDITCARDSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MBR3_EMP_CREDITCARDS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("EMP_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
//        fieldID.setForeignKeyFieldName("XML_MBR2_EMPLOYEE2.EMP_ID");
        table.addField(fieldID);
    
        FieldDefinition fieldCARD = new FieldDefinition();
        fieldCARD.setName("CARD");
        fieldCARD.setTypeName("VARCHAR");
        fieldCARD.setSize(2);
        fieldCARD.setShouldAllowNull(false);
        fieldCARD.setIsPrimaryKey(false);
        fieldCARD.setUnique(true);
        fieldCARD.setIsIdentity(false);
        table.addField(fieldCARD);
        
        FieldDefinition fieldNUMB = new FieldDefinition();
        fieldNUMB.setName("NUMB");
        fieldNUMB.setTypeName("VARCHAR");
        fieldNUMB.setSize(10);
        fieldNUMB.setShouldAllowNull(false);
        fieldNUMB.setIsPrimaryKey(false);
        fieldNUMB.setUnique(false);
        fieldNUMB.setIsIdentity(false);
        table.addField(fieldNUMB);
    
        return table;
    }
    
    public static TableDefinition buildCREDITLINESTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MBR3_EMP_CREDITLINES");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("EMP_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
//        fieldID.setForeignKeyFieldName("XML_MBR2_EMPLOYEE.EMP_ID");
        table.addField(fieldID);
    
        FieldDefinition fieldBANK = new FieldDefinition();
        fieldBANK.setName("BANK");
        fieldBANK.setTypeName("VARCHAR");
        fieldBANK.setSize(4);
        fieldBANK.setShouldAllowNull(false);
        fieldBANK.setIsPrimaryKey(false);
        fieldBANK.setUnique(true);
        fieldBANK.setIsIdentity(false);
        table.addField(fieldBANK);
        
        FieldDefinition fieldACCOUNT = new FieldDefinition();
        fieldACCOUNT.setName("ACCOUNT");
        fieldACCOUNT.setTypeName("VARCHAR");
        fieldACCOUNT.setSize(10);
        fieldACCOUNT.setShouldAllowNull(false);
        fieldACCOUNT.setIsPrimaryKey(false);
        fieldACCOUNT.setUnique(false);
        fieldACCOUNT.setIsIdentity(false);
        table.addField(fieldACCOUNT);
    
        return table;
    }
    
    public static TableDefinition buildDEALERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MBR3_DEALER");

        FieldDefinition field = new FieldDefinition();
        field.setName("DEALER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field0 = new FieldDefinition();
        field0.setName("FK_EMP_ID");
        field0.setTypeName("NUMERIC");
        field0.setSize(15);
        field0.setShouldAllowNull(true);
        field0.setIsPrimaryKey(false);
        field0.setUnique(false);
        field0.setIsIdentity(false);
//        field0.setForeignKeyFieldName("XML_MBR2_EMPLOYEE.EMP_ID");
        table.addField(field0);
    
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("F_NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
    
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("L_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(40);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        table.addField(field2);
        
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("STATUS");
        field3.setTypeName("VARCHAR");
        field3.setSize(40);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);
        
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);
        
        return table;
    }
    
    public static TableDefinition buildLARGEPROJECTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MBR3_LPROJECT");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("XML_MBR3_PROJECT.PROJ_ID");
        table.addField(field);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("BUDGET");
        field1.setTypeName("DOUBLE PRECIS");
        field1.setSize(18);
        field1.setShouldAllowNull(true );
        field1.setIsPrimaryKey(false );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        table.addField(field1);
    
        return table;
    }

    public static TableDefinition buildPHONENUMBERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MBR3_PHONENUMBER");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("OWNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
//        field.setForeignKeyFieldName("XML_MBR2_EMPLOYEE.EMP_ID");
        table.addField(field);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(15);
        field1.setShouldAllowNull(false );
        field1.setIsPrimaryKey(true );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        table.addField(field1);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field2 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field2.setName("AREA_CODE");
        field2.setTypeName("VARCHAR");
        field2.setSize(3);
        field2.setShouldAllowNull(true );
        field2.setIsPrimaryKey(false );
        field2.setUnique(false );
        field2.setIsIdentity(false );
        table.addField(field2);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field3 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field3.setName("NUMB");
        field3.setTypeName("VARCHAR");
        field3.setSize(8);
        field3.setShouldAllowNull(true );
        field3.setIsPrimaryKey(false );
        field3.setUnique(false );
        field3.setIsIdentity(false );
        table.addField(field3);

        return table;
    }

    public static TableDefinition buildPROJECT_EMPTable() {
        TableDefinition table = new TableDefinition();

        table.setName("XML_MBR3_PROJ_EMP");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
//        field.setForeignKeyFieldName("XML_MBR2_EMPLOYEE.EMP_ID");
        table.addField(field);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("PROJ_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false );
        field1.setIsPrimaryKey(true );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        field1.setForeignKeyFieldName("XML_MBR3_PROJECT.PROJ_ID");
        table.addField(field1);

        return table;
    }

    public static TableDefinition buildPROJECTTable() {
        TableDefinition table = new TableDefinition();

        table.setName("XML_MBR3_PROJECT");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(true );
        table.addField(field);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("PROJ_TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(1);
        field1.setShouldAllowNull(true );
        field1.setIsPrimaryKey(false );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        table.addField(field1);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field2 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field2.setName("PROJ_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(30);
        field2.setShouldAllowNull(true );
        field2.setIsPrimaryKey(false );
        field2.setUnique(false );
        field2.setIsIdentity(false );
        table.addField(field2);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field3 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field3.setName("DESCRIP");
        field3.setTypeName("VARCHAR");
        field3.setSize(200);
        field3.setShouldAllowNull(true );
        field3.setIsPrimaryKey(false );
        field3.setUnique(false );
        field3.setIsIdentity(false );
        table.addField(field3);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field4 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field4.setName("LEADER_ID");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true );
        field4.setIsPrimaryKey(false );
        field4.setUnique(false );
        field4.setIsIdentity(false );
//        field4.setForeignKeyFieldName("XML_MBR2_EMPLOYEE.EMP_ID");
        table.addField(field4);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field5 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field5.setName("VERSION");
        field5.setTypeName("NUMERIC");
        field5.setSize(15);
        field5.setShouldAllowNull(true );
        field5.setIsPrimaryKey(false );
        field5.setUnique(false );
        field5.setIsIdentity(false );
        table.addField(field5);

        return table;
    }

    public static TableDefinition buildRESPONSTable() {
        TableDefinition table = new TableDefinition();
        // SECTION: TABLE
        table.setName("XML_MBR3_RESPONS");
    
        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
//        field.setForeignKeyFieldName("XML_MBR2_EMPLOYEE.EMP_ID");
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("DESCRIPTION");
        field1.setTypeName("VARCHAR");
        field1.setSize(200);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
    
        return table;
    }

}
