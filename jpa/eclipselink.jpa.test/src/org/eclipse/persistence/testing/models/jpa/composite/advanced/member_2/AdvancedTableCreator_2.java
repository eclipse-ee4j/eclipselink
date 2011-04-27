/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     06/16/2010-2.2 Guy Pelletier 
 *       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
 *     10/15/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 *     10/27/2010-2.2 Guy Pelletier 
 *       - 328114: @AttributeOverride does not work with nested embeddables having attributes of the same name
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_2;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class AdvancedTableCreator_2 extends TogglingFastTableCreator {
    public AdvancedTableCreator_2() {
        setName("CompositeAdvanced_2");
        this.ignoreDatabaseException = true;

/*        addTableDefinition(buildADDRESSTable());
        addTableDefinition(buildBUYERTable());
        addTableDefinition(buildCREDITCARDSTable());
        addTableDefinition(buildCREDITLINESTable());
        addTableDefinition(buildCUSTOMERTable());
        addTableDefinition(buildDEALERTable());
        addTableDefinition(buildDEPTTable());*/
        addTableDefinition(buildDEPT_EMPTable());
        addTableDefinition(buildEMPLOYEETable());
/*        addTableDefinition(buildEQUIPMENTTable());
        addTableDefinition(buildEQUIPMENTCODETable());
        addTableDefinition(buildGOLFERTable());
        addTableDefinition(buildHUGEPROJECTTable());
        addTableDefinition(buildLARGEPROJECTTable());
        addTableDefinition(buildMANTable());
        addTableDefinition(buildPARTNERLINKTable());
        addTableDefinition(buildPHONENUMBERTable());
        addTableDefinition(buildPHONENUMBERSTATUSTable());
        addTableDefinition(buildPLATINUMBUYERTable());*/
        addTableDefinition(buildPROJECT_EMPTable());
/*        addTableDefinition(buildPROJECT_PROPSTable());
        addTableDefinition(buildPROJECTTable());
        addTableDefinition(buildRESPONSTable());*/
        addTableDefinition(buildSALARYTable());
/*        addTableDefinition(buildVEGETABLETable());
        addTableDefinition(buildWOMANTable());
        addTableDefinition(buildWORKWEEKTable());
        addTableDefinition(buildWORLDRANKTable());
        addTableDefinition(buildCONCURRENCYATable());
        addTableDefinition(buildCONCURRENCYBTable());
        addTableDefinition(buildCONCURRENCYCTable());
        addTableDefinition(buildREADONLYISOLATED());
        addTableDefinition(buildENTITYBTable());
        addTableDefinition(buildENTITYCTable());
        addTableDefinition(buildENTITYATable());
        addTableDefinition(buildENTITYDTable());
        addTableDefinition(buildADVENTITYAENTITYDTable());
        addTableDefinition(buildENTITYETable());
        addTableDefinition(buildADVENTITYAENTITYETable());
        addTableDefinition(buildVIOLATIONTable());
        addTableDefinition(buildVIOLATIONCODETable());
        addTableDefinition(buildVIOLATIONCODESTable());
        addTableDefinition(buildSTUDENTTable());
        addTableDefinition(buildSCHOOLTable());
        addTableDefinition(buildBOLTTable());
        addTableDefinition(buildNUTTable());
        addTableDefinition(buildLOOTTable());*/
    }

    public TableDefinition buildDEPT_EMPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MBR2_DEPT_EMP");

        // SECTION: FIELD
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("Department_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
//        fieldID.setForeignKeyFieldName("MBR1_DEPT.ID");
        table.addField(fieldID);
        
        // SECTION: FIELD
        FieldDefinition fieldEMP = new FieldDefinition();
        fieldEMP.setName("managers_EMP_ID");
        fieldEMP.setTypeName("NUMERIC");
        fieldEMP.setSize(15);
        fieldEMP.setShouldAllowNull(false);
        fieldEMP.setIsPrimaryKey(true);
        fieldEMP.setUnique(false);
        fieldEMP.setIsIdentity(false);
        fieldEMP.setForeignKeyFieldName("MBR2_EMPLOYEE.EMP_ID");
        table.addField(fieldEMP);
        
        return table;   
    }

    public TableDefinition buildEMPLOYEETable() {
        TableDefinition table = new TableDefinition();
        table.setName("MBR2_EMPLOYEE");
    
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
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
        
        FieldDefinition fieldGender = new FieldDefinition();
        fieldGender.setName("GENDER");
        fieldGender.setTypeName("VARCHAR");
        fieldGender.setSize(1);
        fieldGender.setShouldAllowNull(true);
        fieldGender.setIsPrimaryKey(false);
        fieldGender.setUnique(false);
        fieldGender.setIsIdentity(false);
        table.addField(fieldGender);
    
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("START_DATE");
        field3.setTypeName("DATE");
        field3.setSize(23);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("END_DATE");
        field4.setTypeName("DATE");
        field4.setSize(23);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
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

        FieldDefinition field8 = new FieldDefinition();
        field8.setName("ADDR_ID");
        field8.setTypeName("NUMERIC");
        field8.setSize(15);
        field8.setShouldAllowNull(true);
        field8.setIsPrimaryKey(false);
        field8.setUnique(false);
        field8.setIsIdentity(false);
//        field8.setForeignKeyFieldName("MBR1_ADDRESS.ADDRESS_ID");
        table.addField(field8);
    
        FieldDefinition field9 = new FieldDefinition();
        field9.setName("MANAGER_EMP_ID");
        field9.setTypeName("NUMERIC");
        field9.setSize(15);
        field9.setShouldAllowNull(true);
        field9.setIsPrimaryKey(false);
        field9.setUnique(false);
        field9.setIsIdentity(false);
        field9.setForeignKeyFieldName("MBR2_EMPLOYEE.EMP_ID");
        table.addField(field9);
    
        FieldDefinition field10 = new FieldDefinition();
        field10.setName("VERSION");
        field10.setTypeName("NUMERIC");
        field10.setSize(15);
        field10.setShouldAllowNull(true);
        field10.setIsPrimaryKey(false);
        field10.setUnique(false);
        field10.setIsIdentity(false);
        table.addField(field10);
        
        FieldDefinition fieldDEPT = new FieldDefinition();
        fieldDEPT.setName("DEPT_ID");
        fieldDEPT.setTypeName("NUMERIC");
        fieldDEPT.setSize(15);
        fieldDEPT.setShouldAllowNull(true);
        fieldDEPT.setIsPrimaryKey(false);
        fieldDEPT.setUnique(false);
        fieldDEPT.setIsIdentity(false);
//        fieldDEPT.setForeignKeyFieldName("MBR1_DEPT.ID");
        table.addField(fieldDEPT);
        
        FieldDefinition fieldStatus = new FieldDefinition();
        fieldStatus.setName("STATUS");
        fieldStatus.setTypeName("NUMERIC");
        fieldStatus.setSize(15);
        fieldStatus.setIsPrimaryKey(false);
        fieldStatus.setUnique(false);
        fieldStatus.setIsIdentity(false);
        fieldStatus.setShouldAllowNull(true);
        table.addField(fieldStatus);
    
        FieldDefinition fieldPayScale = new FieldDefinition();
        fieldPayScale.setName("PAY_SCALE");
        fieldPayScale.setTypeName("VARCHAR");
        fieldPayScale.setSize(40);
        fieldPayScale.setIsPrimaryKey(false);
        fieldPayScale.setUnique(false);
        fieldPayScale.setIsIdentity(false);
        fieldPayScale.setShouldAllowNull(true);
        table.addField(fieldPayScale);
        
        FieldDefinition fieldRoomNumber = new FieldDefinition();
        fieldRoomNumber.setName("ROOM_NUM");
        fieldRoomNumber.setTypeName("NUMBER");
        fieldRoomNumber.setSize(15);
        fieldRoomNumber.setSubSize(0);
        fieldRoomNumber.setIsPrimaryKey(false);
        fieldRoomNumber.setIsIdentity(false);
        fieldRoomNumber.setUnique(false);
        fieldRoomNumber.setShouldAllowNull(true);
        table.addField(fieldRoomNumber);
        
        // SECTION: FIELD
        FieldDefinition fieldFormerCompany = new FieldDefinition();
        fieldFormerCompany.setName("FORMER_COMPANY");
        fieldFormerCompany.setTypeName("VARCHAR");
        fieldFormerCompany.setSize(40);
        fieldFormerCompany.setShouldAllowNull(true );
        fieldFormerCompany.setIsPrimaryKey(false );
        fieldFormerCompany.setUnique(false );
        fieldFormerCompany.setIsIdentity(false );
        table.addField(fieldFormerCompany);
    
        // SECTION: FIELD
        FieldDefinition fieldFormerStartDate = new FieldDefinition();
        fieldFormerStartDate.setName("FORMER_START_DATE");
        fieldFormerStartDate.setTypeName("DATE");
        fieldFormerStartDate.setSize(23);
        fieldFormerStartDate.setShouldAllowNull(true );
        fieldFormerStartDate.setIsPrimaryKey(false );
        fieldFormerStartDate.setUnique(false );
        fieldFormerStartDate.setIsIdentity(false );
        table.addField(fieldFormerStartDate);
    
        // SECTION: FIELD
        FieldDefinition fieldFormerEndDate = new FieldDefinition();
        fieldFormerEndDate.setName("FORMER_END_DATE");
        fieldFormerEndDate.setTypeName("DATE");
        fieldFormerEndDate.setSize(23);
        fieldFormerEndDate.setShouldAllowNull(true );
        fieldFormerEndDate.setIsPrimaryKey(false );
        fieldFormerEndDate.setUnique(false );
        fieldFormerEndDate.setIsIdentity(false );
        table.addField(fieldFormerEndDate);

        FieldDefinition fieldHugeProj = new FieldDefinition();
        fieldHugeProj.setName("HUGE_PROJ_ID");
        fieldHugeProj.setTypeName("NUMERIC");
        fieldHugeProj.setSize(15);
        fieldHugeProj.setShouldAllowNull(true);
        fieldHugeProj.setIsPrimaryKey(false);
        fieldHugeProj.setUnique(false);
        fieldHugeProj.setIsIdentity(false);
//        fieldHugeProj.setForeignKeyFieldName("MBR3_PROJECT.PROJ_ID");
        table.addField(fieldHugeProj);
        
        return table;
    }

    public TableDefinition buildSALARYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MBR2_SALARY");

        FieldDefinition fieldEMP_ID = new FieldDefinition();
        fieldEMP_ID.setName("EMP_ID");
        fieldEMP_ID.setTypeName("NUMERIC");
        fieldEMP_ID.setSize(15);
        fieldEMP_ID.setSubSize(0);
        fieldEMP_ID.setIsPrimaryKey(true);
        fieldEMP_ID.setIsIdentity(false);
        fieldEMP_ID.setUnique(false);
        fieldEMP_ID.setShouldAllowNull(false);
        fieldEMP_ID.setForeignKeyFieldName("MBR2_EMPLOYEE.EMP_ID");
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

    // this is for Project.teamMembers ManyToMany, which is independent from Employee.projects ManyToMany.
    public TableDefinition buildPROJECT_EMPTable() {
        TableDefinition table = new TableDefinition();

        table.setName("MBR2_PROJ_EMP");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("PROJECTS_PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
//        field.setForeignKeyFieldName("MBR3_PROJECT.PROJ_ID");
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("TEAMMEMBERS_EMP_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false );
        field1.setIsPrimaryKey(true );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        field1.setForeignKeyFieldName("MBR2_EMPLOYEE.EMP_ID");
        table.addField(field1);

        return table;
    }
}
