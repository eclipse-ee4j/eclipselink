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
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     10/21/2009-2.0 Guy Pelletier 
 *       - 290567: mappedbyid support incomplete
 *     11/23/2009-2.0 Guy Pelletier 
 *       - 295790: JPA 2.0 adding @MapsId to one entity causes initialization errors in other entities
 *     05/31/2010-2.1 Guy Pelletier 
 *       - 314941: multiple joinColumns without referenced column names defined, no error
 *     08/13/2010-2.2 Guy Pelletier 
 *       - 296078: JPA 2.0 with @MapsId, em.persist generates Internal Exception IllegalArgumentException
 *     02/02/2011-2.3 Chris Delahunt 
 *       - 336122: ValidationException thrown for JoinColumns on OneToMany with composite primary key
 ******************************************************************************/  

package org.eclipse.persistence.testing.models.jpa.advanced.compositepk;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class CompositePKTableCreator extends TogglingFastTableCreator {
    public CompositePKTableCreator() {
        setName("EJB3CompositePKProject");

        addTableDefinition(buildADMIN_CONTRACTTable());
        addTableDefinition(buildADMINTable());
        addTableDefinition(buildSCIENTISTTable());
        addTableDefinition(buildDEPARTMENTTable());
        addTableDefinition(buildDEPT_ADMINTable());
        addTableDefinition(buildDEPT_COMPETENCYTable());
        addTableDefinition(buildCUBICLETable());
        addTableDefinition(buildSARGEANTTable());
        addTableDefinition(buildMASTERCORPORALTable());
        addTableDefinition(buildMASTERCORPORALCLONETable());
        addTableDefinition(buildMAJORTable());
        addTableDefinition(buildMAJORGENERALTable());
        addTableDefinition(buildCAPTAINTable());
        addTableDefinition(buildBRIGADIERGENERALTable());
        addTableDefinition(buildCORPORALTable());
        addTableDefinition(buildPRIVATETable());
        addTableDefinition(buildGENERALTable());
        addTableDefinition(buildLIEUTENANTGENERALTable());
        addTableDefinition(buildLIEUTENANTTable());
        addTableDefinition(buildSECONDLIEUTENANTTable());
        addTableDefinition(buildOFFICERCADETTable());
        addTableDefinition(buildLACKEYTable());
        addTableDefinition(buildLACKEYCREWTable());
        addTableDefinition(buildOFFICETable());
        addTableDefinition(buildBOOKIETable());
        addTableDefinition(buildCELLNUMBERTable());
        addTableDefinition(buildADMINPOOLTable());
        addTableDefinition(buildGOLFCLUBHEADTable());
        addTableDefinition(buildGOLFCLUBSHAFTTable());
        addTableDefinition(buildGOLFCLUBTable());
        addTableDefinition(buildGOLFCLUBORDERTable());
	}

    public static TableDefinition buildBOOKIETable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_BOOKIE");
        
        FieldDefinition fieldBOOKIE_ID = new FieldDefinition();
        fieldBOOKIE_ID.setName("BOOKIE_ID");
        fieldBOOKIE_ID.setTypeName("NUMERIC");
        fieldBOOKIE_ID.setSize(15);
        fieldBOOKIE_ID.setShouldAllowNull(false);
        fieldBOOKIE_ID.setIsPrimaryKey(true);
        fieldBOOKIE_ID.setUnique(false);
        fieldBOOKIE_ID.setIsIdentity(true);
        table.addField(fieldBOOKIE_ID);
    
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(40);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);

        return table;
    }
    
    public static TableDefinition buildCELLNUMBERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CELLNUMBER");

        FieldDefinition field = new FieldDefinition();
        field.setName("BOOKIE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA_BOOKIE.BOOKIE_ID");
        table.addField(field);
    
        FieldDefinition fieldNUMBER = new FieldDefinition();
        fieldNUMBER.setName("NUMB");
        fieldNUMBER.setTypeName("VARCHAR");
        fieldNUMBER.setSize(15);
        fieldNUMBER.setShouldAllowNull(false);
        fieldNUMBER.setIsPrimaryKey(true);
        fieldNUMBER.setUnique(false);
        fieldNUMBER.setIsIdentity(false);
        table.addField(fieldNUMBER);
        
        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("DESCRIP");
        fieldDESCRIPTION.setTypeName("VARCHAR");
        fieldDESCRIPTION.setSize(40);
        fieldDESCRIPTION.setShouldAllowNull(true);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setIsIdentity(false);
        table.addField(fieldDESCRIPTION);

        return table;
    }
    
    public static TableDefinition buildADMIN_CONTRACTTable(){
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_ADMIN_CONTRACT");
        
        FieldDefinition field = new FieldDefinition();
        field.setName("EMPLOYEE_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("END_DATE");
        field4.setTypeName("DATE");
        field4.setSize(23);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

        return table;
    }
    
    public static TableDefinition buildADMINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_ADMIN");
        
        FieldDefinition field = new FieldDefinition();
        field.setName("EMPLOYEE_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("CONTRACT_COMPANY");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
        
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

    public static TableDefinition buildBRIGADIERGENERALTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_BRIGADIER_GENERAL");
        
        FieldDefinition fieldF_NAME = new FieldDefinition();
        fieldF_NAME.setName("FIRST_NAME");
        fieldF_NAME.setTypeName("VARCHAR");
        fieldF_NAME.setSize(40);
        fieldF_NAME.setShouldAllowNull(false);
        fieldF_NAME.setIsPrimaryKey(true);
        fieldF_NAME.setUnique(false);
        fieldF_NAME.setIsIdentity(false);
        table.addField(fieldF_NAME);
        
        FieldDefinition fieldL_NAME = new FieldDefinition();
        fieldL_NAME.setName("LAST_NAME");
        fieldL_NAME.setTypeName("VARCHAR");
        fieldL_NAME.setSize(40);
        fieldL_NAME.setShouldAllowNull(false);
        fieldL_NAME.setIsPrimaryKey(true);
        fieldL_NAME.setUnique(false);
        fieldL_NAME.setIsIdentity(false);
        table.addField(fieldL_NAME);
        
        return table;
    }
    
    public static TableDefinition buildCAPTAINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CAPTAIN");
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("someOtherName");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(40);
        fieldNAME.setShouldAllowNull(false);
        fieldNAME.setIsPrimaryKey(true);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);
        
        FieldDefinition fieldF_NAME = new FieldDefinition();
        fieldF_NAME.setName("FK1");
        fieldF_NAME.setTypeName("VARCHAR");
        fieldF_NAME.setSize(40);
        fieldF_NAME.setShouldAllowNull(false);
        fieldF_NAME.setIsPrimaryKey(true);
        fieldF_NAME.setUnique(false);
        fieldF_NAME.setIsIdentity(false);
        table.addField(fieldF_NAME);
        
        FieldDefinition fieldL_NAME = new FieldDefinition();
        fieldL_NAME.setName("FK2");
        fieldL_NAME.setTypeName("VARCHAR");
        fieldL_NAME.setSize(40);
        fieldL_NAME.setShouldAllowNull(false);
        fieldL_NAME.setIsPrimaryKey(true);
        fieldL_NAME.setUnique(false);
        fieldL_NAME.setIsIdentity(false);
        table.addField(fieldL_NAME);
        
        return table;
    }
    
    public static TableDefinition buildCORPORALTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CORPORAL");
        
        FieldDefinition fieldF_NAME = new FieldDefinition();
        fieldF_NAME.setName("F_NAME");
        fieldF_NAME.setTypeName("VARCHAR");
        fieldF_NAME.setSize(40);
        fieldF_NAME.setShouldAllowNull(false);
        fieldF_NAME.setIsPrimaryKey(true);
        fieldF_NAME.setUnique(false);
        fieldF_NAME.setIsIdentity(false);
        table.addField(fieldF_NAME);
        
        FieldDefinition fieldL_NAME = new FieldDefinition();
        fieldL_NAME.setName("L_NAME");
        fieldL_NAME.setTypeName("VARCHAR");
        fieldL_NAME.setSize(40);
        fieldL_NAME.setShouldAllowNull(false);
        fieldL_NAME.setIsPrimaryKey(true);
        fieldL_NAME.setUnique(false);
        fieldL_NAME.setIsIdentity(false);
        table.addField(fieldL_NAME);
        
        return table;
    }

    public static TableDefinition buildCUBICLETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_CUBICLE");

        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setShouldAllowNull(false);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(true);
        table.addField(ID_field);

        FieldDefinition CODE_field = new FieldDefinition();
        CODE_field.setName("CODE");
        CODE_field.setTypeName("VARCHAR");
        CODE_field.setSize(1);
        CODE_field.setShouldAllowNull(false);
        CODE_field.setIsPrimaryKey(true);
        CODE_field.setUnique(false);
        CODE_field.setIsIdentity(true);
        table.addField(CODE_field);

        FieldDefinition OFFICE_ID_field = new FieldDefinition();
        OFFICE_ID_field.setName("OFFICE_ID");
        OFFICE_ID_field.setTypeName("NUMERIC");
        OFFICE_ID_field.setSize(15);
        OFFICE_ID_field.setShouldAllowNull(true);
        OFFICE_ID_field.setIsPrimaryKey(false);
        OFFICE_ID_field.setUnique(false);
        OFFICE_ID_field.setIsIdentity(true);
        table.addField(OFFICE_ID_field);

        FieldDefinition LOCATION_field = new FieldDefinition();
        LOCATION_field.setName("OFFICE_LOC");
        LOCATION_field.setTypeName("VARCHAR");
        LOCATION_field.setSize(40);
        LOCATION_field.setShouldAllowNull(true);
        LOCATION_field.setIsPrimaryKey(false);
        LOCATION_field.setUnique(false);
        LOCATION_field.setIsIdentity(true);
        table.addField(LOCATION_field);

        return table;
    }
    
    public static TableDefinition buildDEPARTMENTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_DEPARTMENT");

        FieldDefinition NAME_field = new FieldDefinition();
        NAME_field.setName("NAME");
        NAME_field.setTypeName("VARCHAR");
        NAME_field.setSize(40);
        NAME_field.setShouldAllowNull(false);
        NAME_field.setIsPrimaryKey(true);
        NAME_field.setUnique(false);
        NAME_field.setIsIdentity(true);
        table.addField(NAME_field);
    
        FieldDefinition ROLE_field = new FieldDefinition();
        ROLE_field.setName("DROLE");
        ROLE_field.setTypeName("VARCHAR");
        ROLE_field.setSize(40);
        ROLE_field.setShouldAllowNull(false);
        ROLE_field.setIsPrimaryKey(true);
        ROLE_field.setUnique(false);
        ROLE_field.setIsIdentity(true);
        table.addField(ROLE_field);
    
        FieldDefinition LOCATION_field = new FieldDefinition();
        LOCATION_field.setName("LOCATION");
        LOCATION_field.setTypeName("VARCHAR");
        LOCATION_field.setSize(40);
        LOCATION_field.setShouldAllowNull(false);
        LOCATION_field.setIsPrimaryKey(true);
        LOCATION_field.setUnique(false);
        LOCATION_field.setIsIdentity(true);
        table.addField(LOCATION_field);

        return table;
    }
    
    public static TableDefinition buildDEPT_ADMINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_DEPT_ADMIN");

        // SECTION: FIELD
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("DEPT_NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(40);
        fieldName.setShouldAllowNull(false);
        fieldName.setIsPrimaryKey(true);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        //fieldName.setForeignKeyFieldName("CMP3_DEPARTMENT.NAME");
        table.addField(fieldName);
    
        FieldDefinition ROLE_field = new FieldDefinition();
        ROLE_field.setName("DEPT_ROLE");
        ROLE_field.setTypeName("VARCHAR");
        ROLE_field.setSize(40);
        ROLE_field.setShouldAllowNull(false);
        ROLE_field.setIsPrimaryKey(true);
        ROLE_field.setUnique(false);
        ROLE_field.setIsIdentity(false);
        //ROLE_field.setForeignKeyFieldName("CMP3_DEPARTMENT.ROLE");
        table.addField(ROLE_field);
    
        FieldDefinition LOCATION_field = new FieldDefinition();
        LOCATION_field.setName("DEPT_LOCATION");
        LOCATION_field.setTypeName("VARCHAR");
        LOCATION_field.setSize(40);
        LOCATION_field.setShouldAllowNull(false);
        LOCATION_field.setIsPrimaryKey(true);
        LOCATION_field.setUnique(false);
        LOCATION_field.setIsIdentity(false);
        //LOCATION_field.setForeignKeyFieldName("CMP3_DEPARTMENT.LOCATION");
        table.addField(LOCATION_field);
        
        // SECTION: FIELD
        FieldDefinition fieldEMP = new FieldDefinition();
        fieldEMP.setName("ADMIN_EMPLOYEE_EMP_ID");
        fieldEMP.setTypeName("NUMERIC");
        fieldEMP.setSize(15);
        fieldEMP.setShouldAllowNull(true);
        fieldEMP.setIsPrimaryKey(true);
        fieldEMP.setUnique(false);
        fieldEMP.setIsIdentity(false);
        //fieldEMP.setForeignKeyFieldName("CMP3_EMPLOYEE.EMP_ID");
        table.addField(fieldEMP);
        
        FieldDefinition fieldPOOL_ID = new FieldDefinition();
        fieldPOOL_ID.setName("POOL_ID");
        fieldPOOL_ID.setTypeName("NUMERIC");
        fieldPOOL_ID.setSize(15);
        fieldPOOL_ID.setShouldAllowNull(true);
        fieldPOOL_ID.setIsPrimaryKey(false);
        fieldPOOL_ID.setUnique(false);
        fieldPOOL_ID.setIsIdentity(false);
        table.addField(fieldPOOL_ID);

        return table;   
    }
    
    public static TableDefinition buildDEPT_COMPETENCYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DEPT_COMPETENCIES");

        FieldDefinition NAME_field = new FieldDefinition();
        NAME_field.setName("NAME");
        NAME_field.setTypeName("VARCHAR");
        NAME_field.setSize(40);
        NAME_field.setShouldAllowNull(false);
        NAME_field.setIsPrimaryKey(true);
        NAME_field.setUnique(false);
        NAME_field.setIsIdentity(false);
        table.addField(NAME_field);
    
        FieldDefinition ROLE_field = new FieldDefinition();
        ROLE_field.setName("DROLE");
        ROLE_field.setTypeName("VARCHAR");
        ROLE_field.setSize(40);
        ROLE_field.setShouldAllowNull(false);
        ROLE_field.setIsPrimaryKey(true);
        ROLE_field.setUnique(false);
        ROLE_field.setIsIdentity(false);
        table.addField(ROLE_field);
    
        FieldDefinition LOCATION_field = new FieldDefinition();
        LOCATION_field.setName("LOCATION");
        LOCATION_field.setTypeName("VARCHAR");
        LOCATION_field.setSize(40);
        LOCATION_field.setShouldAllowNull(false);
        LOCATION_field.setIsPrimaryKey(true);
        LOCATION_field.setUnique(false);
        LOCATION_field.setIsIdentity(false);
        table.addField(LOCATION_field);
        
        FieldDefinition DESCRIP_field = new FieldDefinition();
        DESCRIP_field.setName("DESCRIP");
        DESCRIP_field.setTypeName("VARCHAR");
        DESCRIP_field.setSize(40);
        DESCRIP_field.setShouldAllowNull(true);
        DESCRIP_field.setIsPrimaryKey(false);
        DESCRIP_field.setUnique(false);
        DESCRIP_field.setIsIdentity(false);
        table.addField(DESCRIP_field);
        
        FieldDefinition RATING_field = new FieldDefinition();
        RATING_field.setName("RATING");
        RATING_field.setTypeName("NUMERIC");
        RATING_field.setSize(10);
        RATING_field.setShouldAllowNull(true);
        RATING_field.setIsPrimaryKey(false);
        RATING_field.setUnique(false);
        RATING_field.setIsIdentity(false);
        table.addField(RATING_field);

        return table;   
    }
    
    public static TableDefinition buildGENERALTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_GENERAL");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("GENERAL_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
        
        return table;
    }
    
    public static TableDefinition buildGOLFCLUBHEADTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_GOLF_CLUB_HEAD");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);

        return table;
    }
    
    public static TableDefinition buildGOLFCLUBORDERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_GOLF_CLUB_ORDER");
        
        FieldDefinition fieldHEAD_ID = new FieldDefinition();
        fieldHEAD_ID.setName("HEAD_ID");
        fieldHEAD_ID.setTypeName("NUMERIC");
        fieldHEAD_ID.setSize(15);
        fieldHEAD_ID.setShouldAllowNull(false);
        fieldHEAD_ID.setIsPrimaryKey(true);
        fieldHEAD_ID.setUnique(false);
        fieldHEAD_ID.setIsIdentity(false);
        table.addField(fieldHEAD_ID);

        FieldDefinition fieldSHAFT_ID = new FieldDefinition();
        fieldSHAFT_ID.setName("SHAFT_ID");
        fieldSHAFT_ID.setTypeName("NUMERIC");
        fieldSHAFT_ID.setSize(15);
        fieldSHAFT_ID.setShouldAllowNull(false);
        fieldSHAFT_ID.setIsPrimaryKey(true);
        fieldSHAFT_ID.setUnique(false);
        fieldSHAFT_ID.setIsIdentity(false);
        table.addField(fieldSHAFT_ID);
        
        return table;
    }
    
    public static TableDefinition buildGOLFCLUBSHAFTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_GOLF_CLUB_SHAFT");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);

        return table;
    }
    
    public static TableDefinition buildGOLFCLUBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_GOLF_CLUB");
        
        FieldDefinition fieldHEAD_ID = new FieldDefinition();
        fieldHEAD_ID.setName("HEAD_ID");
        fieldHEAD_ID.setTypeName("NUMERIC");
        fieldHEAD_ID.setSize(15);
        fieldHEAD_ID.setShouldAllowNull(false);
        fieldHEAD_ID.setIsPrimaryKey(true);
        fieldHEAD_ID.setUnique(false);
        fieldHEAD_ID.setIsIdentity(false);
        table.addField(fieldHEAD_ID);

        FieldDefinition fieldSHAFT_ID = new FieldDefinition();
        fieldSHAFT_ID.setName("SHAFT_ID");
        fieldSHAFT_ID.setTypeName("NUMERIC");
        fieldSHAFT_ID.setSize(15);
        fieldSHAFT_ID.setShouldAllowNull(false);
        fieldSHAFT_ID.setIsPrimaryKey(true);
        fieldSHAFT_ID.setUnique(false);
        fieldSHAFT_ID.setIsIdentity(false);
        table.addField(fieldSHAFT_ID);
        
        return table;
    }
    
    public static TableDefinition buildLIEUTENANTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_LIEUTENANT");
        
        FieldDefinition fieldF_NAME = new FieldDefinition();
        fieldF_NAME.setName("F_NAME");
        fieldF_NAME.setTypeName("VARCHAR");
        fieldF_NAME.setSize(40);
        fieldF_NAME.setShouldAllowNull(false);
        fieldF_NAME.setIsPrimaryKey(true);
        fieldF_NAME.setUnique(false);
        fieldF_NAME.setIsIdentity(false);
        table.addField(fieldF_NAME);
        
        FieldDefinition fieldL_NAME = new FieldDefinition();
        fieldL_NAME.setName("L_NAME");
        fieldL_NAME.setTypeName("VARCHAR");
        fieldL_NAME.setSize(40);
        fieldL_NAME.setShouldAllowNull(false);
        fieldL_NAME.setIsPrimaryKey(true);
        fieldL_NAME.setUnique(false);
        fieldL_NAME.setIsIdentity(false);
        table.addField(fieldL_NAME);
        
        return table;
    }
    
    public static TableDefinition buildLACKEYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_LACKEY");
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(40);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);
        
        FieldDefinition fieldF_NAME = new FieldDefinition();
        fieldF_NAME.setName("FIRSTNAME");
        fieldF_NAME.setTypeName("VARCHAR");
        fieldF_NAME.setSize(40);
        fieldF_NAME.setShouldAllowNull(false);
        fieldF_NAME.setIsPrimaryKey(true);
        fieldF_NAME.setUnique(false);
        fieldF_NAME.setIsIdentity(false);
        table.addField(fieldF_NAME);
        
        FieldDefinition fieldL_NAME = new FieldDefinition();
        fieldL_NAME.setName("LASTNAME");
        fieldL_NAME.setTypeName("VARCHAR");
        fieldL_NAME.setSize(40);
        fieldL_NAME.setShouldAllowNull(false);
        fieldL_NAME.setIsPrimaryKey(true);
        fieldL_NAME.setUnique(false);
        fieldL_NAME.setIsIdentity(false);
        table.addField(fieldL_NAME);
        
        return table;
    }
    
    public static TableDefinition buildLACKEYCREWTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_LACKEYCREW");
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("RANK");
        fieldNAME.setTypeName("NUMERIC");
        fieldNAME.setSize(15);
        fieldNAME.setShouldAllowNull(false);
        fieldNAME.setIsPrimaryKey(true);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);
        
        FieldDefinition fieldF_NAME = new FieldDefinition();
        fieldF_NAME.setName("FIRSTNAME");
        fieldF_NAME.setTypeName("VARCHAR");
        fieldF_NAME.setSize(40);
        fieldF_NAME.setShouldAllowNull(false);
        fieldF_NAME.setIsPrimaryKey(true);
        fieldF_NAME.setUnique(false);
        fieldF_NAME.setIsIdentity(false);
        table.addField(fieldF_NAME);
        
        FieldDefinition fieldL_NAME = new FieldDefinition();
        fieldL_NAME.setName("LASTNAME");
        fieldL_NAME.setTypeName("VARCHAR");
        fieldL_NAME.setSize(40);
        fieldL_NAME.setShouldAllowNull(false);
        fieldL_NAME.setIsPrimaryKey(true);
        fieldL_NAME.setUnique(false);
        fieldL_NAME.setIsIdentity(false);
        table.addField(fieldL_NAME);
        
        return table;
    }
    
    public static TableDefinition buildLIEUTENANTGENERALTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_LIEUTENANT_GENERAL");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("GENERAL_GENERAL_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        fieldID.setForeignKeyFieldName("JPA_GENERAL.GENERAL_ID");
        table.addField(fieldID);
        
        return table;
    }
    
    public static TableDefinition buildMAJORTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_MAJOR");
        
        FieldDefinition fieldF_NAME = new FieldDefinition();
        fieldF_NAME.setName("F_NAME");
        fieldF_NAME.setTypeName("VARCHAR");
        fieldF_NAME.setSize(40);
        fieldF_NAME.setShouldAllowNull(false);
        fieldF_NAME.setIsPrimaryKey(true);
        fieldF_NAME.setUnique(false);
        fieldF_NAME.setIsIdentity(false);
        table.addField(fieldF_NAME);
        
        FieldDefinition fieldL_NAME = new FieldDefinition();
        fieldL_NAME.setName("L_NAME");
        fieldL_NAME.setTypeName("VARCHAR");
        fieldL_NAME.setSize(40);
        fieldL_NAME.setShouldAllowNull(false);
        fieldL_NAME.setIsPrimaryKey(true);
        fieldL_NAME.setUnique(false);
        fieldL_NAME.setIsIdentity(false);
        table.addField(fieldL_NAME);
        
        return table;
    }
    
    public static TableDefinition buildMAJORGENERALTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_MAJOR_GENERAL");
        
        FieldDefinition fieldF_NAME = new FieldDefinition();
        fieldF_NAME.setName("F_NAME");
        fieldF_NAME.setTypeName("VARCHAR");
        fieldF_NAME.setSize(40);
        fieldF_NAME.setShouldAllowNull(false);
        fieldF_NAME.setIsPrimaryKey(true);
        fieldF_NAME.setUnique(false);
        fieldF_NAME.setIsIdentity(false);
        table.addField(fieldF_NAME);
        
        FieldDefinition fieldL_NAME = new FieldDefinition();
        fieldL_NAME.setName("L_NAME");
        fieldL_NAME.setTypeName("VARCHAR");
        fieldL_NAME.setSize(40);
        fieldL_NAME.setShouldAllowNull(false);
        fieldL_NAME.setIsPrimaryKey(true);
        fieldL_NAME.setUnique(false);
        fieldL_NAME.setIsIdentity(false);
        table.addField(fieldL_NAME);
        
        return table;
    }
    
    public static TableDefinition buildMASTERCORPORALTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_MASTER_CORPORAL");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("SARGEANT_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(40);
        fieldNAME.setShouldAllowNull(false);
        fieldNAME.setIsPrimaryKey(true);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);
        
        return table;
    }
    
    public static TableDefinition buildMASTERCORPORALCLONETable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_MASTER_CORPORAL_CLONE");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("SARGEANTPK");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(40);
        fieldNAME.setShouldAllowNull(false);
        fieldNAME.setIsPrimaryKey(true);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);
        
        return table;
    }
    
    public static TableDefinition buildPRIVATETable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_PRIVATE");
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("PRIVATE_NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(40);
        fieldNAME.setShouldAllowNull(false);
        fieldNAME.setIsPrimaryKey(true);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);
        
        FieldDefinition fieldF_NAME = new FieldDefinition();
        fieldF_NAME.setName("F_NAME");
        fieldF_NAME.setTypeName("VARCHAR");
        fieldF_NAME.setSize(40);
        fieldF_NAME.setShouldAllowNull(false);
        fieldF_NAME.setIsPrimaryKey(true);
        fieldF_NAME.setUnique(false);
        fieldF_NAME.setIsIdentity(false);
        table.addField(fieldF_NAME);
        
        FieldDefinition fieldL_NAME = new FieldDefinition();
        fieldL_NAME.setName("L_NAME");
        fieldL_NAME.setTypeName("VARCHAR");
        fieldL_NAME.setSize(40);
        fieldL_NAME.setShouldAllowNull(false);
        fieldL_NAME.setIsPrimaryKey(true);
        fieldL_NAME.setUnique(false);
        fieldL_NAME.setIsIdentity(false);
        table.addField(fieldL_NAME);
        
        return table;
    }
    
    public static TableDefinition buildSARGEANTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_SARGEANT");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(40);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);
        
        return table;
    }
    
    public static TableDefinition buildSCIENTISTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_SCIENTIST");
    
        FieldDefinition ID_NUMBER_field = new FieldDefinition();
        ID_NUMBER_field.setName("ID_NUMBER");
        ID_NUMBER_field.setTypeName("NUMERIC");
        ID_NUMBER_field.setSize(15);
        ID_NUMBER_field.setShouldAllowNull(false);
        ID_NUMBER_field.setIsPrimaryKey(true);
        ID_NUMBER_field.setUnique(false);
        ID_NUMBER_field.setIsIdentity(true);
        table.addField(ID_NUMBER_field);
    
        FieldDefinition F_NAME_field = new FieldDefinition();
        F_NAME_field.setName("F_NAME");
        F_NAME_field.setTypeName("VARCHAR");
        F_NAME_field.setSize(40);
        F_NAME_field.setShouldAllowNull(false);
        F_NAME_field.setIsPrimaryKey(true);
        F_NAME_field.setUnique(false);
        F_NAME_field.setIsIdentity(true);
        table.addField(F_NAME_field);
    
        FieldDefinition L_NAME_Field = new FieldDefinition();
        L_NAME_Field.setName("L_NAME");
        L_NAME_Field.setTypeName("VARCHAR");
        L_NAME_Field.setSize(40);
        L_NAME_Field.setShouldAllowNull(false);
        L_NAME_Field.setIsPrimaryKey(true);
        L_NAME_Field.setUnique(false);
        L_NAME_Field.setIsIdentity(true);
        table.addField(L_NAME_Field);
    
        FieldDefinition CUBE_ID_field = new FieldDefinition();
        CUBE_ID_field.setName("CUBE_ID");
        CUBE_ID_field.setTypeName("NUMERIC");
        CUBE_ID_field.setSize(15);
        CUBE_ID_field.setShouldAllowNull(true);
        CUBE_ID_field.setIsPrimaryKey(false);
        CUBE_ID_field.setUnique(false);
        CUBE_ID_field.setIsIdentity(false);
        table.addField(CUBE_ID_field);
    
        FieldDefinition CUBE_CODE_field = new FieldDefinition();
        CUBE_CODE_field.setName("CUBE_CODE");
        CUBE_CODE_field.setTypeName("VARCHAR");
        CUBE_CODE_field.setSize(1);
        CUBE_CODE_field.setShouldAllowNull(true);
        CUBE_CODE_field.setIsPrimaryKey(false);
        CUBE_CODE_field.setUnique(false);
        CUBE_CODE_field.setIsIdentity(false);
        table.addField(CUBE_CODE_field);
    
        FieldDefinition DEPT_NAME_field = new FieldDefinition();
        DEPT_NAME_field.setName("DEPT_NAME");
        DEPT_NAME_field.setTypeName("VARCHAR");
        DEPT_NAME_field.setSize(40);
        DEPT_NAME_field.setShouldAllowNull(true);
        DEPT_NAME_field.setIsPrimaryKey(false);
        DEPT_NAME_field.setUnique(false);
        DEPT_NAME_field.setIsIdentity(false);
        table.addField(DEPT_NAME_field);
    
        FieldDefinition DEPT_ROLE_field = new FieldDefinition();
        DEPT_ROLE_field.setName("DEPT_ROLE");
        DEPT_ROLE_field.setTypeName("VARCHAR");
        DEPT_ROLE_field.setSize(40);
        DEPT_ROLE_field.setShouldAllowNull(true);
        DEPT_ROLE_field.setIsPrimaryKey(false);
        DEPT_ROLE_field.setUnique(false);
        DEPT_ROLE_field.setIsIdentity(false);
        table.addField(DEPT_ROLE_field);
    
        FieldDefinition DEPT_LOCATION_field = new FieldDefinition();
        DEPT_LOCATION_field.setName("DEPT_LOCATION");
        DEPT_LOCATION_field.setTypeName("VARCHAR");
        DEPT_LOCATION_field.setSize(40);
        DEPT_LOCATION_field.setShouldAllowNull(true);
        DEPT_LOCATION_field.setIsPrimaryKey(false);
        DEPT_LOCATION_field.setUnique(false);
        DEPT_LOCATION_field.setIsIdentity(false);
        table.addField(DEPT_LOCATION_field);
        
        FieldDefinition fieldDTYPE = new FieldDefinition();
        fieldDTYPE.setName("DTYPE");
        fieldDTYPE.setTypeName("VARCHAR2");
        fieldDTYPE.setSize(20);
        fieldDTYPE.setSubSize(0);
        fieldDTYPE.setIsPrimaryKey(false);
        fieldDTYPE.setIsIdentity(false);
        fieldDTYPE.setUnique(false);
        fieldDTYPE.setShouldAllowNull(true);
        table.addField(fieldDTYPE);

        ForeignKeyConstraint fkConstraint1 = new ForeignKeyConstraint();
        fkConstraint1.setName("CMP3_SC_CUBICLE");
        fkConstraint1.addSourceField("CUBE_ID");
        fkConstraint1.addSourceField("CUBE_CODE");
        fkConstraint1.setTargetTable("CMP3_CUBICLE");
        fkConstraint1.addTargetField("ID");
        fkConstraint1.addTargetField("CODE");
        table.addForeignKeyConstraint(fkConstraint1);

        ForeignKeyConstraint fkConstraint2 = new ForeignKeyConstraint();
        fkConstraint2.setName("CMP3_SC_DEPT");
        fkConstraint2.addSourceField("DEPT_NAME");
        fkConstraint2.addSourceField("DEPT_ROLE");
        fkConstraint2.addSourceField("DEPT_LOCATION");
        fkConstraint2.setTargetTable("CMP3_DEPARTMENT");
        fkConstraint2.addTargetField("NAME");
        fkConstraint2.addTargetField("DROLE");
        fkConstraint2.addTargetField("LOCATION");
        table.addForeignKeyConstraint(fkConstraint2);
       
        return table;
    }
    
    public static TableDefinition buildSECONDLIEUTENANTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_SECOND_LIEUTENANT");
        
        FieldDefinition fieldF_NAME = new FieldDefinition();
        fieldF_NAME.setName("F_NAME");
        fieldF_NAME.setTypeName("VARCHAR");
        fieldF_NAME.setSize(40);
        fieldF_NAME.setShouldAllowNull(false);
        fieldF_NAME.setIsPrimaryKey(true);
        fieldF_NAME.setUnique(false);
        fieldF_NAME.setIsIdentity(false);
        table.addField(fieldF_NAME);
        
        FieldDefinition fieldL_NAME = new FieldDefinition();
        fieldL_NAME.setName("L_NAME");
        fieldL_NAME.setTypeName("VARCHAR");
        fieldL_NAME.setSize(40);
        fieldL_NAME.setShouldAllowNull(false);
        fieldL_NAME.setIsPrimaryKey(true);
        fieldL_NAME.setUnique(false);
        fieldL_NAME.setIsIdentity(false);
        table.addField(fieldL_NAME);
        
        return table;
    }
    
    public static TableDefinition buildOFFICERCADETTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_OFFICER_CADET");
        
        FieldDefinition fieldF_NAME = new FieldDefinition();
        fieldF_NAME.setName("F_NAME");
        fieldF_NAME.setTypeName("VARCHAR");
        fieldF_NAME.setSize(40);
        fieldF_NAME.setShouldAllowNull(false);
        fieldF_NAME.setIsPrimaryKey(true);
        fieldF_NAME.setUnique(false);
        fieldF_NAME.setIsIdentity(false);
        table.addField(fieldF_NAME);
        
        FieldDefinition fieldL_NAME = new FieldDefinition();
        fieldL_NAME.setName("L_NAME");
        fieldL_NAME.setTypeName("VARCHAR");
        fieldL_NAME.setSize(40);
        fieldL_NAME.setShouldAllowNull(false);
        fieldL_NAME.setIsPrimaryKey(true);
        fieldL_NAME.setUnique(false);
        fieldL_NAME.setIsIdentity(false);
        table.addField(fieldL_NAME);
        
        return table;
    }
    
    public static TableDefinition buildOFFICETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_OFFICE");
   
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setShouldAllowNull(true);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(true);
        table.addField(ID_field);
        
        FieldDefinition NAME_field = new FieldDefinition();
        NAME_field.setName("NAME");
        NAME_field.setTypeName("VARCHAR");
        NAME_field.setSize(40);
        NAME_field.setShouldAllowNull(false);
        NAME_field.setIsPrimaryKey(false);
        NAME_field.setUnique(false);
        NAME_field.setIsIdentity(false);
        table.addField(NAME_field);
        
        FieldDefinition LOCATION_field = new FieldDefinition();
        LOCATION_field.setName("LOCATION");
        LOCATION_field.setTypeName("VARCHAR");
        LOCATION_field.setSize(40);
        LOCATION_field.setShouldAllowNull(false);
        LOCATION_field.setIsPrimaryKey(true);
        LOCATION_field.setUnique(false);
        LOCATION_field.setIsIdentity(true);
        table.addField(LOCATION_field);
        
        FieldDefinition DROLE_field = new FieldDefinition();
        DROLE_field.setName("DROLE");
        DROLE_field.setTypeName("VARCHAR");
        DROLE_field.setSize(40);
        DROLE_field.setShouldAllowNull(false);
        DROLE_field.setIsPrimaryKey(false);
        DROLE_field.setUnique(false);
        DROLE_field.setIsIdentity(false);
        table.addField(DROLE_field);

        return table;
    }
    
    
    public static TableDefinition buildADMINPOOLTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_ADMIN_POOL");
   
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setShouldAllowNull(false);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(true);
        table.addField(ID_field);
        
        FieldDefinition DESCRIPTION_field = new FieldDefinition();
        DESCRIPTION_field.setName("DESCRIPTION");
        DESCRIPTION_field.setTypeName("VARCHAR");
        DESCRIPTION_field.setSize(40);
        DESCRIPTION_field.setShouldAllowNull(true);
        DESCRIPTION_field.setIsPrimaryKey(false);
        DESCRIPTION_field.setUnique(false);
        DESCRIPTION_field.setIsIdentity(false);
        table.addField(DESCRIPTION_field);

        return table;
    }

    /**
     * Dropping old foreign keys from schema change.
     */
    @Override
    public void replaceTables(DatabaseSession session) {
        try {
            if (session.getPlatform().supportsUniqueKeyConstraints()
                    && !session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
                session.executeNonSelectingSQL("Alter table CMP3_SCIENTIST drop constraint CMP3_SCIENTIST_CUBICLE");
                session.executeNonSelectingSQL("Alter table CMP3_SCIENTIST drop constraint CMP3_SCIENTIST_DEPT");
            }
        } catch (Exception ignore) {}
        super.replaceTables(session);
    }
}
