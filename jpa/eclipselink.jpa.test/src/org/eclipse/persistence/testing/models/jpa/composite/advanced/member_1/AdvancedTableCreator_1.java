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
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     06/16/2010-2.2 Guy Pelletier 
 *       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
 *     10/15/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 *     10/27/2010-2.2 Guy Pelletier 
 *       - 328114: @AttributeOverride does not work with nested embeddables having attributes of the same name
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_1;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class AdvancedTableCreator_1 extends TogglingFastTableCreator {
    public AdvancedTableCreator_1() {
        setName("CompositeAdvanced_1");
        this.ignoreDatabaseException = true;

        addTableDefinition(buildADDRESSTable());
/*        addTableDefinition(buildBUYERTable());
        addTableDefinition(buildCREDITCARDSTable());
        addTableDefinition(buildCREDITLINESTable());*/
        addTableDefinition(buildCUSTOMERTable());
/*        addTableDefinition(buildDEALERTable());*/
        addTableDefinition(buildDEPTTable());
/*        addTableDefinition(buildDEPT_EMPTable());
        addTableDefinition(buildEMPLOYEETable());
        addTableDefinition(buildEQUIPMENTTable());*/
        addTableDefinition(buildEQUIPMENTCODETable());
/*        addTableDefinition(buildGOLFERTable());
        addTableDefinition(buildHUGEPROJECTTable());
        addTableDefinition(buildLARGEPROJECTTable());
        addTableDefinition(buildMANTable());
        addTableDefinition(buildPARTNERLINKTable());
        addTableDefinition(buildPHONENUMBERTable());*/
        addTableDefinition(buildPHONENUMBERSTATUSTable());
/*        addTableDefinition(buildPLATINUMBUYERTable());
        addTableDefinition(buildPROJECT_EMPTable());*/
        addTableDefinition(buildPROJECT_PROPSTable());
/*        addTableDefinition(buildPROJECTTable());*/
        addTableDefinition(buildRESPONSTable());
/*        addTableDefinition(buildSALARYTable());
        addTableDefinition(buildVEGETABLETable());
        addTableDefinition(buildWOMANTable());*/
        addTableDefinition(buildWORKWEEKTable());
/*        addTableDefinition(buildWORLDRANKTable());
        addTableDefinition(buildCONCURRENCYATable());
        addTableDefinition(buildCONCURRENCYBTable());
        addTableDefinition(buildCONCURRENCYCTable());*/
        addTableDefinition(buildREADONLYISOLATED());
/*        addTableDefinition(buildENTITYBTable());
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
    
    public TableDefinition buildADDRESSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MBR1_ADDRESS");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ADDRESS_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldSTREET = new FieldDefinition();
        fieldSTREET.setName("STREET");
        fieldSTREET.setTypeName("VARCHAR2");
        fieldSTREET.setSize(60);
        fieldSTREET.setSubSize(0);
        fieldSTREET.setIsPrimaryKey(false);
        fieldSTREET.setIsIdentity(false);
        fieldSTREET.setUnique(false);
        fieldSTREET.setShouldAllowNull(true);
        table.addField(fieldSTREET);

        FieldDefinition fieldCITY = new FieldDefinition();
        fieldCITY.setName("CITY");
        fieldCITY.setTypeName("VARCHAR2");
        fieldCITY.setSize(60);
        fieldCITY.setSubSize(0);
        fieldCITY.setIsPrimaryKey(false);
        fieldCITY.setIsIdentity(false);
        fieldCITY.setUnique(false);
        fieldCITY.setShouldAllowNull(true);
        table.addField(fieldCITY);

        FieldDefinition fieldPROVINCE = new FieldDefinition();
        fieldPROVINCE.setName("PROVINCE");
        fieldPROVINCE.setTypeName("VARCHAR2");
        fieldPROVINCE.setSize(60);
        fieldPROVINCE.setSubSize(0);
        fieldPROVINCE.setIsPrimaryKey(false);
        fieldPROVINCE.setIsIdentity(false);
        fieldPROVINCE.setUnique(false);
        fieldPROVINCE.setShouldAllowNull(true);
        table.addField(fieldPROVINCE);

        FieldDefinition fieldPOSTALCODE = new FieldDefinition();
        fieldPOSTALCODE.setName("P_CODE");
        fieldPOSTALCODE.setTypeName("VARCHAR2");
        fieldPOSTALCODE.setSize(67);
        fieldPOSTALCODE.setSubSize(0);
        fieldPOSTALCODE.setIsPrimaryKey(false);
        fieldPOSTALCODE.setIsIdentity(false);
        fieldPOSTALCODE.setUnique(false);
        fieldPOSTALCODE.setShouldAllowNull(true);
        table.addField(fieldPOSTALCODE);

        FieldDefinition fieldCOUNTRY = new FieldDefinition();
        fieldCOUNTRY.setName("COUNTRY");
        fieldCOUNTRY.setTypeName("VARCHAR2");
        fieldCOUNTRY.setSize(60);
        fieldCOUNTRY.setSubSize(0);
        fieldCOUNTRY.setIsPrimaryKey(false);
        fieldCOUNTRY.setIsIdentity(false);
        fieldCOUNTRY.setUnique(false);
        fieldCOUNTRY.setShouldAllowNull(true);
        table.addField(fieldCOUNTRY);

        FieldDefinition fieldType = new FieldDefinition();
        fieldType.setName("TYPE");
        fieldType.setTypeName("VARCHAR2");
        fieldType.setSize(150);
        fieldType.setSubSize(0);
        fieldType.setIsPrimaryKey(false);
        fieldType.setIsIdentity(false);
        fieldType.setUnique(false);
        fieldType.setShouldAllowNull(true);
        table.addField(fieldType);
        
        FieldDefinition fieldVERSION = new FieldDefinition();
        fieldVERSION.setName("VERSION");
        fieldVERSION.setTypeName("NUMERIC");
        fieldVERSION.setSize(15);
        fieldVERSION.setShouldAllowNull(true);
        fieldVERSION.setIsPrimaryKey(false);
        fieldVERSION.setUnique(false);
        fieldVERSION.setIsIdentity(false);
        table.addField(fieldVERSION);
        
        return table;
    }

    public TableDefinition buildCUSTOMERTable() {
         TableDefinition table = new TableDefinition();
         table.setName("MBR1_ADV_CUSTOMER");

         FieldDefinition field = new FieldDefinition();
         field.setName("CUSTOMER_ID");
         field.setTypeName("NUMERIC");
         field.setSize(15);
         field.setShouldAllowNull(false);
         field.setIsPrimaryKey(true);
         field.setUnique(false);
         field.setIsIdentity(true);
         table.addField(field);
     
         FieldDefinition field0 = new FieldDefinition();
         field0.setName("FK_DEALER_ID");
         field0.setTypeName("NUMERIC");
         field0.setSize(15);
         field0.setShouldAllowNull(true);
         field0.setIsPrimaryKey(false);
         field0.setUnique(false);
         field0.setIsIdentity(false);
//         field0.setForeignKeyFieldName("MBR3_DEALER.DEALER_ID");
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
         field3.setName("BUDGET");
         field3.setTypeName("NUMERIC");
         field3.setSize(15);
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

    
    public TableDefinition buildDEPTTable() {
       TableDefinition table = new TableDefinition();
       table.setName("MBR1_DEPT");

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
       fieldNAME.setName("NAME");
       fieldNAME.setTypeName("VARCHAR2");
       fieldNAME.setSize(60);
       fieldNAME.setSubSize(0);
       fieldNAME.setIsPrimaryKey(false);
       fieldNAME.setIsIdentity(false);
       fieldNAME.setUnique(false);
       fieldNAME.setShouldAllowNull(true);
       table.addField(fieldNAME);

       FieldDefinition fieldHEAD = new FieldDefinition();
       fieldHEAD.setName("DEPT_HEAD");
       fieldHEAD.setTypeName("NUMERIC");
       fieldHEAD.setSize(15);
       fieldHEAD.setSubSize(0);
       fieldHEAD.setIsPrimaryKey(false);
       fieldHEAD.setIsIdentity(false);
       fieldHEAD.setUnique(false);
       fieldHEAD.setShouldAllowNull(true);
       table.addField(fieldHEAD);
       
       return table;
   }
   
    public TableDefinition buildRESPONSTable() {
        TableDefinition table = new TableDefinition();
        // SECTION: TABLE
        table.setName("MBR1_RESPONS");
    
        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
//        field.setForeignKeyFieldName("MBR2_EMPLOYEE.EMP_ID");
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
    
    public TableDefinition buildEQUIPMENTCODETable() {
        TableDefinition table = new TableDefinition();
        table.setName("MBR1_ADV_EQUIP_CODE");

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

        FieldDefinition fieldCODE = new FieldDefinition();
        fieldCODE.setName("CODE");
        fieldCODE.setTypeName("VARCHAR2");
        fieldCODE.setSize(1);
        fieldCODE.setSubSize(0);
        fieldCODE.setIsPrimaryKey(false);
        fieldCODE.setIsIdentity(false);
        fieldCODE.setUnique(false);
        fieldCODE.setShouldAllowNull(false);
        table.addField(fieldCODE);

        return table;
    }

    public TableDefinition buildPHONENUMBERSTATUSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MBR1_PHONE_STATUS");
    
        FieldDefinition fieldOWNERID = new FieldDefinition();
        fieldOWNERID.setName("OWNER_ID");
        fieldOWNERID.setTypeName("NUMERIC");
        fieldOWNERID.setSize(15);
        fieldOWNERID.setShouldAllowNull(false);
        fieldOWNERID.setIsPrimaryKey(false);
        fieldOWNERID.setUnique(false);
        fieldOWNERID.setIsIdentity(false);
        table.addField(fieldOWNERID);
        
        FieldDefinition fieldTYPE = new FieldDefinition();
        fieldTYPE.setName("TYPE");
        fieldTYPE.setTypeName("VARCHAR");
        fieldTYPE.setSize(15);
        fieldTYPE.setShouldAllowNull(false);
        fieldTYPE.setIsPrimaryKey(false);
        fieldTYPE.setUnique(false);
        fieldTYPE.setIsIdentity(false);
        table.addField(fieldTYPE);
    
        FieldDefinition fieldSTATUS = new FieldDefinition();
        fieldSTATUS.setName("STATUS");
        fieldSTATUS.setTypeName("VARCHAR");
        fieldSTATUS.setSize(20);
        fieldSTATUS.setShouldAllowNull(false);
        fieldSTATUS.setIsPrimaryKey(false);
        fieldSTATUS.setUnique(false);
        fieldSTATUS.setIsIdentity(false);
        table.addField(fieldSTATUS);
    
        return table;
    }    

    public TableDefinition buildPROJECT_PROPSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MBR1_PROJ_PROPS");

        FieldDefinition projectIdField = new FieldDefinition();
        projectIdField.setName("PROJ_ID");
        projectIdField.setTypeName("NUMERIC");
        projectIdField.setSize(15);
        projectIdField.setShouldAllowNull(false);
        projectIdField.setIsPrimaryKey(false);
        projectIdField.setUnique(false);
        projectIdField.setIsIdentity(false);
//        projectIdField.setForeignKeyFieldName("MBR3_PROJECT.PROJ_ID");
        table.addField(projectIdField);
    
        FieldDefinition propertiesField = new FieldDefinition();
        propertiesField.setName("PROPS");
        propertiesField.setTypeName("VARCHAR");
        propertiesField.setSize(45);
        propertiesField.setShouldAllowNull(true);
        propertiesField.setIsPrimaryKey(false);
        propertiesField.setUnique(false);
        propertiesField.setIsIdentity(false);
        table.addField(propertiesField);

        return table;
    }    

    public TableDefinition buildWORKWEEKTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MBR1_WORKWEEK");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("EMP_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
//        fieldID.setForeignKeyFieldName("MBR2_EMPLOYEE.EMP_ID");
        table.addField(fieldID);
    
        FieldDefinition fieldWORKWEEK = new FieldDefinition();
        fieldWORKWEEK.setName("WORKWEEK");
        fieldWORKWEEK.setTypeName("NUMERIC");
        fieldWORKWEEK.setSize(1);
        fieldWORKWEEK.setIsPrimaryKey(false);
        fieldWORKWEEK.setUnique(false);
        fieldWORKWEEK.setIsIdentity(false);
        fieldWORKWEEK.setShouldAllowNull(false);
        table.addField(fieldWORKWEEK);
    
        return table;
    }
    
	public TableDefinition buildREADONLYISOLATED() {
        TableDefinition table = new TableDefinition();
        table.setName("MBR1_READONLY_ISOLATED");

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

        FieldDefinition fieldCODE = new FieldDefinition();
        fieldCODE.setName("CODE");
        fieldCODE.setTypeName("VARCHAR2");
        fieldCODE.setSize(10);
        fieldCODE.setSubSize(0);
        fieldCODE.setIsPrimaryKey(false);
        fieldCODE.setIsIdentity(false);
        fieldCODE.setUnique(false);
        fieldCODE.setShouldAllowNull(false);
        table.addField(fieldCODE);

        return table;
    }
}
