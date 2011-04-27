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
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_3;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class AdvancedTableCreator_3 extends TogglingFastTableCreator {
    public AdvancedTableCreator_3() {
        setName("EJB3EmployeeProject");
        this.ignoreDatabaseException = true;

/*        addTableDefinition(buildADDRESSTable());
        addTableDefinition(buildBUYERTable());
        addTableDefinition(buildCREDITCARDSTable());
        addTableDefinition(buildCREDITLINESTable());
        addTableDefinition(buildCUSTOMERTable());*/
        addTableDefinition(buildDEALERTable());
/*        addTableDefinition(buildDEPTTable());
        addTableDefinition(buildDEPT_EMPTable());
        addTableDefinition(buildEMPLOYEETable());*/
        addTableDefinition(buildEQUIPMENTTable());
/*        addTableDefinition(buildEQUIPMENTCODETable());
        addTableDefinition(buildGOLFERTable());*/
        addTableDefinition(buildHUGEPROJECTTable());
        addTableDefinition(buildLARGEPROJECTTable());
/*        addTableDefinition(buildMANTable());
        addTableDefinition(buildPARTNERLINKTable());*/
        addTableDefinition(buildPHONENUMBERTable());
/*        addTableDefinition(buildPHONENUMBERSTATUSTable());
        addTableDefinition(buildPLATINUMBUYERTable());*/
        addTableDefinition(buildPROJECT_EMPTable());
/*        addTableDefinition(buildPROJECT_PROPSTable());*/
        addTableDefinition(buildPROJECTTable());
/*        addTableDefinition(buildRESPONSTable());
        addTableDefinition(buildSALARYTable());
        addTableDefinition(buildVEGETABLETable());
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
        addTableDefinition(buildLOOTTable())*/;
    }
     
     public TableDefinition buildDEALERTable() {
         TableDefinition table = new TableDefinition();
         table.setName("MBR3_DEALER");

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
//         field0.setForeignKeyFieldName("MBR2_EMPLOYEE.EMP_ID");
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
     
     public TableDefinition buildEQUIPMENTTable() {
         TableDefinition table = new TableDefinition();
         table.setName("MBR3_ADV_EQUIP");

         FieldDefinition fieldID = new FieldDefinition();
         fieldID.setName("ID");
         fieldID.setTypeName("NUMERIC");
         fieldID.setSize(15);
         fieldID.setSubSize(0);
         fieldID.setIsPrimaryKey(true);
         fieldID.setIsIdentity(true);
         fieldID.setUnique(false);
         fieldID.setShouldAllowNull(false);
         table.addField(fieldID);

         FieldDefinition fieldNAME = new FieldDefinition();
         fieldNAME.setName("DESCRIP");
         fieldNAME.setTypeName("VARCHAR2");
         fieldNAME.setSize(100);
         fieldNAME.setSubSize(0);
         fieldNAME.setIsPrimaryKey(false);
         fieldNAME.setIsIdentity(false);
         fieldNAME.setUnique(false);
         fieldNAME.setShouldAllowNull(true);
         table.addField(fieldNAME);
         
         FieldDefinition fieldDEPTID = new FieldDefinition();
         fieldDEPTID.setName("DEPT_ID");
         fieldDEPTID.setTypeName("NUMERIC");
         fieldDEPTID.setSize(15);
         fieldDEPTID.setShouldAllowNull(true);
         fieldDEPTID.setIsPrimaryKey(false);
         fieldDEPTID.setUnique(false);
         fieldDEPTID.setIsIdentity(false);
//         fieldDEPTID.setForeignKeyFieldName("MBR1_DEPT.ID");
         table.addField(fieldDEPTID);
         
         FieldDefinition fieldCODEID = new FieldDefinition();
         fieldCODEID.setName("CODE_ID");
         fieldCODEID.setTypeName("NUMERIC");
         fieldCODEID.setSize(15);
         fieldCODEID.setShouldAllowNull(true);
         fieldCODEID.setIsPrimaryKey(false);
         fieldCODEID.setUnique(false);
         fieldCODEID.setIsIdentity(false);
//         fieldCODEID.setForeignKeyFieldName("MBR1_ADV_EQUIP_CODE.ID");
         table.addField(fieldCODEID);

         return table;
     }
     
    public TableDefinition buildHUGEPROJECTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MBR3_HPROJECT");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("MBR3_PROJECT.PROJ_ID");
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("EVANGELIST_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
//        field1.setForeignKeyFieldName("MBR2_EMPLOYEE.EMP_ID");
        table.addField(field1);
    
        return table;
    }
    
    public TableDefinition buildLARGEPROJECTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MBR3_LPROJECT");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("MBR3_PROJECT.PROJ_ID");
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
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
    
    public TableDefinition buildPHONENUMBERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MBR3_PHONENUMBER");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("OWNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
//        field.setForeignKeyFieldName("MBR2_EMPLOYEE.EMP_ID");
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(15);
        field1.setShouldAllowNull(false );
        field1.setIsPrimaryKey(true );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        table.addField(field1);
    
        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("AREA_CODE");
        field2.setTypeName("VARCHAR");
        field2.setSize(3);
        field2.setShouldAllowNull(true );
        field2.setIsPrimaryKey(false );
        field2.setUnique(false );
        field2.setIsIdentity(false );
        table.addField(field2);
    
        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
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
    
    public TableDefinition buildPROJECT_EMPTable() {
        TableDefinition table = new TableDefinition();

        table.setName("MBR3_EMP_PROJ");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMPLOYEES_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
//        field.setForeignKeyFieldName("MBR2_EMPLOYEE.EMP_ID");
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("projects_PROJ_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false );
        field1.setIsPrimaryKey(true );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        field1.setForeignKeyFieldName("MBR3_PROJECT.PROJ_ID");
        table.addField(field1);

        return table;
    }

    public TableDefinition buildPROJECTTable() {
        TableDefinition table = new TableDefinition();

        table.setName("MBR3_PROJECT");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(true );
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("PROJ_TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(1);
        field1.setShouldAllowNull(true );
        field1.setIsPrimaryKey(false );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        table.addField(field1);
    
        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("PROJ_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(30);
        field2.setShouldAllowNull(true );
        field2.setIsPrimaryKey(false );
        field2.setUnique(false );
        field2.setIsIdentity(false );
        table.addField(field2);
    
        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("DESCRIP");
        field3.setTypeName("VARCHAR");
        field3.setSize(200);
        field3.setShouldAllowNull(true );
        field3.setIsPrimaryKey(false );
        field3.setUnique(false );
        field3.setIsIdentity(false );
        table.addField(field3);
    
        // SECTION: FIELD
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("LEADER_ID");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true );
        field4.setIsPrimaryKey(false );
        field4.setUnique(false );
        field4.setIsIdentity(false );
//        field4.setForeignKeyFieldName("MBR2_EMPLOYEE.EMP_ID");
        table.addField(field4);
    
        // SECTION: FIELD
        FieldDefinition field5 = new FieldDefinition();
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
}
