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
package org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_2;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class AdvancedTableCreator_2 extends TogglingFastTableCreator {

    public AdvancedTableCreator_2() {
        setName("xml.composite.member_2");
        this.ignoreDatabaseException = true;
        // table names prefixed with "XML_MBR2_"

// 1       addTableDefinition(buildADDRESSTable());
// 3        addTableDefinition(buildCREDITCARDSTable());
// 3        addTableDefinition(buildCREDITLINESTable());
// 1        addTableDefinition(buildCUSTOMERTable());
// 3       addTableDefinition(buildDEALERTable());
        addTableDefinition(buildEMPLOYEETable());
// 3        addTableDefinition(buildLARGEPROJECTTable());
//         addTableDefinition(buildMANTable());
//        addTableDefinition(buildPARTNERLINKTable());
// 3        addTableDefinition(buildPHONENUMBERTable());
// 3        addTableDefinition(buildPROJECT_EMPTable());
// 3        addTableDefinition(buildPROJECTTable());
//        addTableDefinition(buildREADONLYCLASSTable());
// 3        addTableDefinition(buildRESPONSTable());
        addTableDefinition(buildSALARYTable());
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
    
    public static TableDefinition buildEMPLOYEETable() {
        TableDefinition table = new TableDefinition();
        // SECTION: TABLE
        table.setName("XML_MBR2_EMPLOYEE");
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(true );
        table.addField(field);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("F_NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true );
        field1.setIsPrimaryKey(false );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        table.addField(field1);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field2 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field2.setName("L_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(40);
        field2.setShouldAllowNull(true );
        field2.setIsPrimaryKey(false );
        field2.setUnique(false );
        field2.setIsIdentity(false );
        table.addField(field2);
        
        org.eclipse.persistence.tools.schemaframework.FieldDefinition sin = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        sin.setName("SIN");
        sin.setTypeName("VARCHAR");
        sin.setSize(10);
        sin.setShouldAllowNull(true );
        sin.setIsPrimaryKey(false );
        sin.setUnique(false );
        sin.setIsIdentity(false );
        table.addField(sin);
    
        FieldDefinition fieldGender = new FieldDefinition();
        fieldGender.setName("GENDER");
        fieldGender.setTypeName("VARCHAR");
        fieldGender.setSize(1);
        fieldGender.setShouldAllowNull(true);
        fieldGender.setIsPrimaryKey(false);
        fieldGender.setUnique(false);
        fieldGender.setIsIdentity(false);
        table.addField(fieldGender);
        
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field3 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field3.setName("START_DATE");
        field3.setTypeName("DATE");
        field3.setSize(23);
        field3.setShouldAllowNull(true );
        field3.setIsPrimaryKey(false );
        field3.setUnique(false );
        field3.setIsIdentity(false );
        table.addField(field3);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field4 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field4.setName("END_DATE");
        field4.setTypeName("DATE");
        field4.setSize(23);
        field4.setShouldAllowNull(true );
        field4.setIsPrimaryKey(false );
        field4.setUnique(false );
        field4.setIsIdentity(false );
        table.addField(field4);
    
        FieldDefinition field5 = new FieldDefinition();
        field5.setName("START_TIME");
        field5.setTypeName("TIME");
        field5.setSize(23);
        field5.setShouldAllowNull(true);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        table.addField(field5);

        FieldDefinition field6 = new FieldDefinition();
        field6.setName("END_TIME");
        field6.setTypeName("TIME");
        field6.setSize(23);
        field6.setShouldAllowNull(true);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        table.addField(field6);

        FieldDefinition fieldOvertimeStart = new FieldDefinition();
        fieldOvertimeStart.setName("START_OVERTIME");
        fieldOvertimeStart.setTypeName("TIME");
        fieldOvertimeStart.setSize(23);
        fieldOvertimeStart.setShouldAllowNull(true);
        fieldOvertimeStart.setIsPrimaryKey(false);
        fieldOvertimeStart.setUnique(false);
        fieldOvertimeStart.setIsIdentity(false);
        table.addField(fieldOvertimeStart);

        FieldDefinition fieldOvertimeEnd = new FieldDefinition();
        fieldOvertimeEnd.setName("END_OVERTIME");
        fieldOvertimeEnd.setTypeName("TIME");
        fieldOvertimeEnd.setSize(23);
        fieldOvertimeEnd.setShouldAllowNull(true);
        fieldOvertimeEnd.setIsPrimaryKey(false);
        fieldOvertimeEnd.setUnique(false);
        fieldOvertimeEnd.setIsIdentity(false);
        table.addField(fieldOvertimeEnd);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field8 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field8.setName("ADDR_ID");
        field8.setTypeName("NUMERIC");
        field8.setSize(15);
        field8.setShouldAllowNull(true );
        field8.setIsPrimaryKey(false );
        field8.setUnique(false );
        field8.setIsIdentity(false );
//        field8.setForeignKeyFieldName("XML_MBR1_ADDRESS.ADDRESS_ID");
        table.addField(field8);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field9 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field9.setName("MANAGER_EMP_ID");
        field9.setTypeName("NUMERIC");
        field9.setSize(15);
        field9.setShouldAllowNull(true );
        field9.setIsPrimaryKey(false );
        field9.setUnique(false );
        field9.setIsIdentity(false );
        field9.setForeignKeyFieldName("XML_MBR2_EMPLOYEE.EMP_ID");
        table.addField(field9);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field10 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field10.setName("VERSION");
        field10.setTypeName("NUMERIC");
        field10.setSize(15);
        field10.setShouldAllowNull(true );
        field10.setIsPrimaryKey(false );
        field10.setUnique(false );
        field10.setIsIdentity(false );
        table.addField(field10);
        
        FieldDefinition fieldPayScale = new FieldDefinition();
        fieldPayScale.setName("PAY_SCALE");
        fieldPayScale.setTypeName("VARCHAR");
        fieldPayScale.setSize(40);
        fieldPayScale.setIsPrimaryKey(false);
        fieldPayScale.setUnique(false);
        fieldPayScale.setIsIdentity(false);
        fieldPayScale.setShouldAllowNull(true);
        table.addField(fieldPayScale);
        
        return table;
    }

    public static TableDefinition buildSALARYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MBR2_SALARY");

        FieldDefinition fieldEMP_ID = new FieldDefinition();
        fieldEMP_ID.setName("E_ID");
        fieldEMP_ID.setTypeName("NUMERIC");
        fieldEMP_ID.setSize(15);
        fieldEMP_ID.setSubSize(0);
        fieldEMP_ID.setIsPrimaryKey(true);
        fieldEMP_ID.setIsIdentity(false);
        fieldEMP_ID.setUnique(false);
        fieldEMP_ID.setShouldAllowNull(false);
        fieldEMP_ID.setForeignKeyFieldName("XML_MBR2_EMPLOYEE.EMP_ID");
        table.addField(fieldEMP_ID);

        FieldDefinition fieldSALARY = new FieldDefinition();
        fieldSALARY.setName("SALARY");
        fieldSALARY.setTypeName("NUMBER");
        fieldSALARY.setSize(15);
        fieldSALARY.setSubSize(0);
        fieldSALARY.setIsPrimaryKey(false);
        fieldSALARY.setIsIdentity(false);
        fieldSALARY.setUnique(false);
        fieldSALARY.setShouldAllowNull(true);
        table.addField(fieldSALARY);

        return table;
    }
}
