/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/30/2009-2.0 Michael O'Brien 
 *       - 266912: JPA 2.0 Metamodel API (part of Criteria API) 
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.metamodel;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class MetamodelTableCreator extends TableCreator {
    
    /**
     * 
     * DDL schema cleanup order
DROP TABLE CMP3_MM_HIST_EMPLOY
DROP TABLE CMP3_MM_MANUF_MM_HWDES_MAP
DROP TABLE CMP3_MM_MANUF_MM_CORPCOMPUTER
DROP TABLE CMP3_MM_MANUF_MM_COMPUTER
DROP TABLE CMP3_MM_MANUF_MM_HWDESIGNER
DROP TABLE CMP3_MM_MANUF_MM_HWDES_MAP
DROP TABLE CMP3_MM_BOARD_MM_MEMORY
DROP TABLE CMP3_MM_BOARD_MM_PROC
DROP TABLE CMP3_MM_BOARD_MM_COMPUTER
DROP TABLE CMP3_MM_COMPUTER_MM_USER
DROP TABLE CMP3_MM_COMPUTER_MM_BOARD
DROP TABLE CMP3_MM_BOARD_SEQ
DROP TABLE CMP3_MM_PERSON_SEQ
DROP TABLE CMP3_MM_PROC_SEQ

DROP TABLE CMP3_MM_COMPUTER
DROP TABLE CMP3_MM_USER
DROP TABLE CMP3_MM_HWDESIGNER
DROP TABLE CMP3_MM_MEMORY
DROP TABLE CMP3_MM_PROC
DROP TABLE CMP3_MM_VECTPROC
DROP TABLE CMP3_MM_ARRAYPROC
DROP TABLE CMP3_MM_LOCATION
DROP TABLE CMP3_MM_GALACTIC
DROP TABLE CMP3_MM_BOARD
DROP TABLE CMP3_MM_SWDESIGNER
DROP TABLE CMP3_MM_MANUF
     */
    
    public MetamodelTableCreator() {
        setName("MetamodelProject");

        addTableDefinition(buildMANUFACTURERTable());
        addTableDefinition(buildHARDWAREDESIGNERTable());
        addTableDefinition(buildSOFTWAREDESIGNERTable());
        addTableDefinition(buildUSERTable());
        addTableDefinition(buildCOMPUTERTable());
        addTableDefinition(buildBOARDTable());
        addTableDefinition(buildMEMORYTable());        
        addTableDefinition(buildPROCESSORTable());        
        //addTableDefinition(buildVECTORPROCESSORTable());
        //addTableDefinition(buildARRAYPROCESSORTable());
        //addTableDefinition(buildLOCATIONTable());
        addTableDefinition(buildGALACTICPOSITIONTable());
        // 1:n
        addTableDefinition(buildMANUFACTURER_COMPUTER_JOINTable());
        addTableDefinition(buildMANUFACTURER_CORPCOMPUTER_JOINTable());
        addTableDefinition(buildMANUFACTURER_HARDWAREDESIGNER_JOINTable());
        addTableDefinition(buildMANUFACTURER_HARDWAREDESIGNER_MAP_JOINTable());
        addTableDefinition(buildMANUFACTURER_HARDWAREDESIGNER_HISTORICAL_JOINTable());        
        addTableDefinition(buildBOARD_MEMORY_JOINTable());
        addTableDefinition(buildBOARD_PROCESSOR_JOINTable());
        addTableDefinition(buildCOMPUTER_BOARD_JOINTable());
        // n:n
        addTableDefinition(buildCOMPUTER_USER_JOINTable());        
    }
 	

    public static TableDefinition buildMANUFACTURERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_MANUF");

        FieldDefinition field = new FieldDefinition();
        field.setName("PERSON_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("MANUF_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

        FieldDefinition field3 = new FieldDefinition();
        field3.setName("NAME");
        field3.setTypeName("VARCHAR");
        field3.setSize(80);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);

/*        // 1:m does not require a JoinTable - only a JoinColumn
        FieldDefinition field6 = new FieldDefinition();
        field6.setName("EMPLOYER_PERSON_ID");
        field6.setTypeName("NUMERIC");
        field6.setSize(15);
        field6.setShouldAllowNull(false);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        field6.setForeignKeyFieldName("CMP3_MM_HWDESIGNER.PERSON_ID");
        table.addField(field6);        
*/        
        return table;
    }

    public static TableDefinition buildHARDWAREDESIGNERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_HWDESIGNER");

        FieldDefinition field = new FieldDefinition();
        field.setName("PERSON_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("HWDESIGNER_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

        FieldDefinition field3 = new FieldDefinition();
        field3.setName("NAME");
        field3.setTypeName("VARCHAR");
        field3.setSize(80);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);

        // from MappedSuperclass
        // m:1 does not require a JoinTable - only a JoinColumn
        FieldDefinition field6 = new FieldDefinition();
        field6.setName("EMPLOYER_PERSON_ID");
        field6.setTypeName("NUMERIC");
        field6.setSize(15);
        field6.setShouldAllowNull(false);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        field6.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field6);        

        // from MappedSuperclass
        // m:1 does not require a JoinTable - only a JoinColumn
        FieldDefinition field9 = new FieldDefinition();
        field9.setName("MAPPEDEMPLOYER_PERSON_ID");
        field9.setTypeName("NUMERIC");
        field9.setSize(15);
        field9.setShouldAllowNull(false);
        field9.setIsPrimaryKey(false);
        field9.setUnique(false);
        field9.setIsIdentity(false);
        field9.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field9);        

        // from MappedSuperclass
        // 1:1 unidirectional
        FieldDefinition field7 = new FieldDefinition();
        field7.setName("PRIME_EMPLOYER_PERSON_ID");
        field7.setTypeName("NUMERIC");
        field7.setSize(15);
        field7.setShouldAllowNull(false);
        field7.setIsPrimaryKey(false);
        field7.setUnique(false);
        field7.setIsIdentity(false);
        field7.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field7);        

        // m:1 does not require a JoinTable - only a JoinColumn
        FieldDefinition field8 = new FieldDefinition();
        field8.setName("SEC_EMPLOYER_PERSON_ID");
        field8.setTypeName("NUMERIC");
        field8.setSize(15);
        field8.setShouldAllowNull(false);
        field8.setIsPrimaryKey(false);
        field8.setUnique(false);
        field8.setIsIdentity(false);
        field8.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field8);        

        
        return table;
    }

    public static TableDefinition buildSOFTWAREDESIGNERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_SWDESIGNER");

        FieldDefinition field = new FieldDefinition();
        field.setName("PERSON_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("SWDESIGNER_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

        FieldDefinition field3 = new FieldDefinition();
        field3.setName("NAME");
        field3.setTypeName("VARCHAR");
        field3.setSize(80);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);
        
        // from MappedSuperclass
/*        // 1:m does not require a JoinTable - only a JoinColumn
        FieldDefinition field6 = new FieldDefinition();
        field6.setName("EMPLOYER_PERSON_ID");
        field6.setTypeName("NUMERIC");
        field6.setSize(15);
        field6.setShouldAllowNull(false);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        field6.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field6);
*/        
        // from MappedSuperclass
        // 1:1 unidirectional
        FieldDefinition field7 = new FieldDefinition();
        field7.setName("PRIME_EMPLOYER_PERSON_ID");
        field7.setTypeName("NUMERIC");
        field7.setSize(15);
        field7.setShouldAllowNull(false);
        field7.setIsPrimaryKey(false);
        field7.setUnique(false);
        field7.setIsIdentity(false);
        field7.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field7);        

        // m:1 does not require a JoinTable - only a JoinColumn
        FieldDefinition field8 = new FieldDefinition();
        field8.setName("SEC_EMPLOYER_PERSON_ID");
        field8.setTypeName("NUMERIC");
        field8.setSize(15);
        field8.setShouldAllowNull(false);
        field8.setIsPrimaryKey(false);
        field8.setUnique(false);
        field8.setIsIdentity(false);
        field8.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field8);        

        // 1:m with JoinTable - by default
/*        FieldDefinition field9 = new FieldDefinition();
        field9.setName("HIST_EMPLOYER_PERSON_ID");
        field9.setTypeName("NUMERIC");
        field9.setSize(15);
        field9.setShouldAllowNull(false);
        field9.setIsPrimaryKey(false);
        field9.setUnique(false);
        field9.setIsIdentity(false);
        field9.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field9);*/        
        
        
        return table;
    }

    public static TableDefinition buildUSERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_USER");

        FieldDefinition field = new FieldDefinition();
        field.setName("PERSON_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("USER_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

        FieldDefinition field3 = new FieldDefinition();
        field3.setName("NAME");
        field3.setTypeName("VARCHAR");
        field3.setSize(80);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);
        
        return table;
    }

    public static TableDefinition buildCOMPUTERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_COMPUTER");

        FieldDefinition field = new FieldDefinition();
        field.setName("COMPUTER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("COMPUTER_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

        FieldDefinition field3 = new FieldDefinition();
        field3.setName("NAME");
        field3.setTypeName("VARCHAR");
        field3.setSize(80);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);

        // Bidirectional OneToOne with Location.computer
        FieldDefinition field5 = new FieldDefinition();
        field5.setName("GALACTIC_GALACTIC_ID");
        field5.setTypeName("NUMERIC");
        field5.setSize(15);
        field5.setShouldAllowNull(false);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        field5.setForeignKeyFieldName("CMP3_MM_GALACTIC.GALACTIC_ID");
        //field5.setForeignKeyFieldName("CMP3_MM_LOCATION.PK_PART1");
        table.addField(field5);
        
        // 1:m does not require a JoinTable - only a JoinColumn
        FieldDefinition field6 = new FieldDefinition();
        field6.setName("MANUFACTURER_PERSON_ID");
        field6.setTypeName("NUMERIC");
        field6.setSize(15);
        field6.setShouldAllowNull(false);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        field6.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field6);        
        
        return table;
    }

    public static TableDefinition buildBOARDTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_BOARD");

        FieldDefinition field = new FieldDefinition();
        field.setName("BOARD_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("BOARD_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

        // 1:m does not require a JoinTable - only a JoinColumn
        FieldDefinition field6 = new FieldDefinition();
        field6.setName("COMPUTER_COMPUTER_ID");
        field6.setTypeName("NUMERIC");
        field6.setSize(15);
        field6.setShouldAllowNull(false);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        field6.setForeignKeyFieldName("CMP3_MM_COMPUTER.COMPUTER_ID");
        table.addField(field6);        
        
        return table;
    }

    public static TableDefinition buildMEMORYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_MEMORY");

        FieldDefinition field = new FieldDefinition();
        field.setName("MEMORY_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("MEMORY_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

        FieldDefinition field3 = new FieldDefinition();
        field3.setName("NAME");
        field3.setTypeName("VARCHAR");
        field3.setSize(80);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);
        
        // 1:m does not require a JoinTable - only a JoinColumn
        FieldDefinition field6 = new FieldDefinition();
        field6.setName("BOARD_BOARD_ID");
        field6.setTypeName("NUMERIC");
        field6.setSize(15);
        field6.setShouldAllowNull(false);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        field6.setForeignKeyFieldName("CMP3_MM_BOARD.BOARD_ID");
        table.addField(field6);        
        
        return table;
    }

    // Entity --> Entity inheritance
    public static TableDefinition buildPROCESSORTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_PROC");

        FieldDefinition field = new FieldDefinition();
        field.setName("PROC_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("PROC_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

        // OVERRIDE @Inheritance from SINGLE_TABLE to JOINED
        // http://wiki.eclipse.org/Introduction_to_EclipseLink_JPA_%28ELUG%29#.40Inheritance
        // discriminator column (for inheritance)
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("DTYPE");
        field3.setTypeName("VARCHAR");
        field3.setSize(80);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);
        
        // 1:m does not require a JoinTable - only a JoinColumn
        FieldDefinition field6 = new FieldDefinition();
        field6.setName("BOARD_BOARD_ID");
        field6.setTypeName("NUMERIC");
        field6.setSize(15);
        field6.setShouldAllowNull(false);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        field6.setForeignKeyFieldName("CMP3_MM_BOARD.BOARD_ID");
        table.addField(field6);        
        
        return table;
    }

    
    public static TableDefinition buildVECTORPROCESSORTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_PROC");

        // JOINED @Inheritance still requires a PK field on the sub entity table
        FieldDefinition field = new FieldDefinition();
        field.setName("PROC_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
/*        FieldDefinition field4 = new FieldDefinition();
        field4.setName("VECTPROC_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);
*/
        // 1:m does not require a JoinTable - only a JoinColumn
/*        FieldDefinition field6 = new FieldDefinition();
        field6.setName("BOARD_BOARD_ID");
        field6.setTypeName("NUMERIC");
        field6.setSize(15);
        field6.setShouldAllowNull(false);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        field6.setForeignKeyFieldName("CMP3_MM_BOARD.BOARD_ID");
        table.addField(field6);        
*/        
        return table;
    }

    public static TableDefinition buildARRAYPROCESSORTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_PROC");

        // JOINED @Inheritance still requires a PK field on the sub entity table
        FieldDefinition field = new FieldDefinition();
        field.setName("PROC_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
/*        FieldDefinition field4 = new FieldDefinition();
        field4.setName("ARRAYPROC_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);
*/
        // 1:m does not require a JoinTable - only a JoinColumn
/*        FieldDefinition field6 = new FieldDefinition();
        field6.setName("BOARD_BOARD_ID");
        field6.setTypeName("NUMERIC");
        field6.setSize(15);
        field6.setShouldAllowNull(false);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        field6.setForeignKeyFieldName("CMP3_MM_BOARD.BOARD_ID");
        table.addField(field6);        
*/        
        return table;
    }
    
    public static TableDefinition buildGALACTICPOSITIONTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_GALACTIC");

/*        FieldDefinition field = new FieldDefinition();
        field.setName("GALACTIC_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
*/    

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("GALACTIC_ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);//true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        /*FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("PK_PART1");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);//true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);*/

        /*FieldDefinition fieldID2 = new FieldDefinition();
        fieldID2.setName("PK_PART2");
        fieldID2.setTypeName("NUMBER");
        fieldID2.setSize(19);
        fieldID2.setSubSize(0);
        fieldID2.setIsPrimaryKey(true);
        fieldID2.setIsIdentity(true);
        fieldID2.setUnique(false);//true);
        fieldID2.setShouldAllowNull(false);
        table.addField(fieldID2);*/

        
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("GALACTIC_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

/*        FieldDefinition field3 = new FieldDefinition();
        field3.setName("NAME");
        field3.setTypeName("VARCHAR");
        field3.setSize(80);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);
*/
        // for MappedSuperclass
/*        // 1:1 unidirectional
        FieldDefinition field7 = new FieldDefinition();
        field7.setName("FUTURE_POS_GALACTIC_ID");
        field7.setTypeName("NUMERIC");
        field7.setSize(15);
        field7.setShouldAllowNull(false);
        field7.setIsPrimaryKey(false);
        field7.setUnique(false);
        field7.setIsIdentity(false);
        field7.setForeignKeyFieldName("CMP3_MM_GALACTIC.GALACTIC_ID");
        table.addField(field7);
*/        
        // OVERRIDE @Inheritance from SINGLE_TABLE to JOINED
        // http://wiki.eclipse.org/Introduction_to_EclipseLink_JPA_%28ELUG%29#.40Inheritance
        // discriminator column (for inheritance)
        FieldDefinition field13 = new FieldDefinition();
        field13.setName("DTYPE");
        field13.setTypeName("VARCHAR");
        field13.setSize(80);
        field13.setShouldAllowNull(true);
        field13.setIsPrimaryKey(false);
        field13.setUnique(false);
        field13.setIsIdentity(false);
        table.addField(field13);
        
        
        return table;
    }

    public static TableDefinition buildLOCATIONTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_LOCATION");

        /*
         * Commented 20090720 in favor of a the composite key EmbeddedPK
         */
/*        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("LOCATION_ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);//true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
*/        
        /*FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("PK_PART1");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);//true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);*/

        /*FieldDefinition fieldID2 = new FieldDefinition();
        fieldID2.setName("PK_PART2");
        fieldID2.setTypeName("NUMBER");
        fieldID2.setSize(19);
        fieldID2.setSubSize(0);
        fieldID2.setIsPrimaryKey(true);
        fieldID2.setIsIdentity(true);
        fieldID2.setUnique(false);//true);
        fieldID2.setShouldAllowNull(false);
        table.addField(fieldID2);*/

        
/*        FieldDefinition field4 = new FieldDefinition();
        field4.setName("LOCATION_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);
*/
/*        FieldDefinition field3 = new FieldDefinition();
        field3.setName("NAME");
        field3.setTypeName("VARCHAR");
        field3.setSize(80);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);
*/
        // Bidirectional OneToOne with Computer.location - unidirectional for now
/*        FieldDefinition field5 = new FieldDefinition();
        field5.setName("COMPUTER_COMPUTER_ID");
        field5.setTypeName("NUMERIC");
        field5.setSize(15);
        field5.setShouldAllowNull(false);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        field5.setForeignKeyFieldName("CMP3_MM_COMPUTER.COMPUTER_ID");
        table.addField(field5);
*/
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("GALACTIC_ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);//true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
/*        // from MappedSuperclass
        // 1:1 unidirectional
        FieldDefinition field7 = new FieldDefinition();
        field7.setName("FUTURE_POS_GALACTIC_ID");
        field7.setTypeName("NUMERIC");
        field7.setSize(15);
        field7.setShouldAllowNull(false);
        field7.setIsPrimaryKey(false);
        field7.setUnique(false);
        field7.setIsIdentity(false);
        field7.setForeignKeyFieldName("CMP3_MM_GALACTIC.GALACTIC_ID");
        table.addField(field7);        
*/        
        return table;
    }
    
    public static TableDefinition buildMANUFACTURER_COMPUTER_JOINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_MANUF_MM_COMPUTER");

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("MANUF_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field1);        

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("COMPUTER_ID");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        field2.setForeignKeyFieldName("CMP3_MM_COMPUTER.COMPUTER_ID");
        table.addField(field2);        

        return table;
    }

    public static TableDefinition buildMANUFACTURER_CORPCOMPUTER_JOINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_MANUF_MM_CORPCOMPUTER");

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("MANUF_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field1);        

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("COMPCOMPUTER_ID");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        field2.setForeignKeyFieldName("CMP3_MM_COMPUTER.COMPUTER_ID");
        table.addField(field2);        

        return table;
    }
    
    public static TableDefinition buildMANUFACTURER_HARDWAREDESIGNER_JOINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_MANUF_MM_HWDESIGNER");

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("MANUF_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field1);        

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("DESIGNER_ID");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        field2.setForeignKeyFieldName("CMP3_MM_HWDESIGNER.PERSON_ID");
        table.addField(field2);        

        return table;
    }

    public static TableDefinition buildMANUFACTURER_HARDWAREDESIGNER_HISTORICAL_JOINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_HIST_EMPLOY");

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("MANUF_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field1);        

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("DESIGNER_ID");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        field2.setForeignKeyFieldName("CMP3_MM_HWDESIGNER.PERSON_ID");
        table.addField(field2);        

        return table;
    }
    
    public static TableDefinition buildMANUFACTURER_HARDWAREDESIGNER_MAP_JOINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_MANUF_MM_HWDES_MAP");

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("MANUF_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("CMP3_MM_MANUF.PERSON_ID");
        table.addField(field1);        

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("DESIGNER_MAP_ID");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        field2.setForeignKeyFieldName("CMP3_MM_HWDESIGNER.PERSON_ID");
        table.addField(field2);        

        return table;
    }
    
    public static TableDefinition buildBOARD_MEMORY_JOINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_BOARD_MM_MEMORY");

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("BOARD_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("CMP3_MM_BOARD.BOARD_ID");
        table.addField(field1);        

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("MEMORY_ID");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        field2.setForeignKeyFieldName("CMP3_MM_MEMORY.MEMORY_ID");
        table.addField(field2);        

        return table;
    }

    public static TableDefinition buildBOARD_PROCESSOR_JOINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_BOARD_MM_PROC");

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("BOARD_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("CMP3_MM_BOARD.BOARD_ID");
        table.addField(field1);        

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("PROC_ID");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        field2.setForeignKeyFieldName("CMP3_MM_PROC.PROC_ID");
        table.addField(field2);        

        return table;
    }

    public static TableDefinition buildCOMPUTER_BOARD_JOINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_COMPUTER_MM_BOARD");

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("BOARD_ID");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        field2.setForeignKeyFieldName("CMP3_MM_BOARD.BOARD_ID");
        table.addField(field2);        

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("COMPUTER_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("CMP3_MM_COMPUTER.COMPUTER_ID");
        table.addField(field1);        
        
        return table;
    }
    
    public static TableDefinition buildCOMPUTER_USER_JOINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_COMPUTER_MM_USER");

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("COMPUTER_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("CMP3_MM_COMPUTER.COMPUTER_ID");
        table.addField(field1);        

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("USER_ID");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        field2.setForeignKeyFieldName("CMP3_MM_USER.PERSON_ID");
        table.addField(field2);        

        return table;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static TableDefinition buildMETAMODEL_CUSTOMERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_CUSTOMER");

        FieldDefinition field = new FieldDefinition();
        field.setName("LIFEFORM_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
/*        FieldDefinition field0 = new FieldDefinition();
        field0.setName("FK_DEALER_ID");
        field0.setTypeName("NUMERIC");
        field0.setSize(15);
        field0.setShouldAllowNull(true);
        field0.setIsPrimaryKey(false);
        field0.setUnique(false);
        field0.setIsIdentity(false);
        field0.setForeignKeyFieldName("CMP3_DEALER.DEALER_ID");
        table.addField(field0);
*/    
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(80);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        FieldDefinition fieldCITY = new FieldDefinition();
        fieldCITY.setName("CITY");
        fieldCITY.setTypeName("VARCHAR");
        fieldCITY.setSize(80);
        fieldCITY.setShouldAllowNull(true);
        fieldCITY.setIsPrimaryKey(false);
        fieldCITY.setUnique(false);
        fieldCITY.setIsIdentity(false);
        table.addField(fieldCITY);

        
/*        FieldDefinition field2 = new FieldDefinition();
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
*/        
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("CUSTOMER_VERSION");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

        FieldDefinition fieldADDRESS = new FieldDefinition();
        fieldADDRESS.setName("HOMEADDRESS_ADDRESS_ID");
        fieldADDRESS.setTypeName("NUMERIC");
        fieldADDRESS.setSize(15);
        fieldADDRESS.setShouldAllowNull(false);
        fieldADDRESS.setIsPrimaryKey(false);
        fieldADDRESS.setUnique(false);
        fieldADDRESS.setIsIdentity(false);
        fieldADDRESS.setForeignKeyFieldName("CMP3_MM_ADDRESS.ADDRESS_ID");
        table.addField(fieldADDRESS);
        
        // we are using a JoinTable  - so no need for a column here
/*        
        FieldDefinition fieldORDERS = new FieldDefinition();
        fieldORDERS.setName("ORDER_ID");
        fieldORDERS.setTypeName("NUMERIC");
        fieldORDERS.setSize(15);
        fieldORDERS.setShouldAllowNull(false);
        fieldORDERS.setIsPrimaryKey(false);
        fieldORDERS.setUnique(false);
        fieldORDERS.setIsIdentity(false);
        fieldORDERS.setForeignKeyFieldName("CMP3_MM_ORDER.ORDER_ID");
        table.addField(fieldORDERS);        
*/        
        return table;
    }

    public static TableDefinition buildMETAMODEL_ORDERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_ORDER");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ORDER_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldQUANTITY = new FieldDefinition();
        fieldQUANTITY.setName("QUANTITY");
        fieldQUANTITY.setTypeName("NUMERIC");
        fieldQUANTITY.setSize(16);
        fieldQUANTITY.setSubSize(0);
        fieldQUANTITY.setIsPrimaryKey(false);
        fieldQUANTITY.setIsIdentity(false);
        fieldQUANTITY.setUnique(false);
        fieldQUANTITY.setShouldAllowNull(true);
        table.addField(fieldQUANTITY);

        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("DESCRIPTION");
        fieldDESCRIPTION.setTypeName("VARCHAR2");
        fieldDESCRIPTION.setSize(60);
        fieldDESCRIPTION.setSubSize(0);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setIsIdentity(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setShouldAllowNull(true);
        table.addField(fieldDESCRIPTION);
        
        FieldDefinition fieldVERSION = new FieldDefinition();
        fieldVERSION.setName("ORDER_VERSION");
        fieldVERSION.setTypeName("NUMERIC");
        fieldVERSION.setSize(15);
        fieldVERSION.setShouldAllowNull(true);
        fieldVERSION.setIsPrimaryKey(false);
        fieldVERSION.setUnique(false);
        fieldVERSION.setIsIdentity(false);
        table.addField(fieldVERSION);
        
        // 1:m does not require a JoinTable - only a JoinColumn        
        FieldDefinition fieldITEM = new FieldDefinition();
        fieldITEM.setName("ITEM_ITEM_ID");
        fieldITEM.setTypeName("NUMERIC");
        fieldITEM.setSize(16);
        fieldITEM.setShouldAllowNull(false);
        fieldITEM.setIsPrimaryKey(false);
        fieldITEM.setUnique(false);
        fieldITEM.setIsIdentity(false);
        fieldITEM.setForeignKeyFieldName("CMP3_MM_ITEM.ITEM_ID");
        table.addField(fieldITEM);        

        // 1:m does not require a JoinTable - only a JoinColumn
        FieldDefinition fieldCUSTOMER = new FieldDefinition();
        fieldCUSTOMER.setName("CUSTOMER_LIFEFORM_ID");
        fieldCUSTOMER.setTypeName("NUMERIC");
        fieldCUSTOMER.setSize(15);
        fieldCUSTOMER.setShouldAllowNull(false);
        fieldCUSTOMER.setIsPrimaryKey(false);
        fieldCUSTOMER.setUnique(false);
        fieldCUSTOMER.setIsIdentity(false);
        fieldCUSTOMER.setForeignKeyFieldName("CMP3_MM_CUSTOMER.LIFEFORM_ID");
        table.addField(fieldCUSTOMER);        

        FieldDefinition fieldADDRESS = new FieldDefinition();
        fieldADDRESS.setName("BILLINGADDRESS_ADDRESS_ID");
        fieldADDRESS.setTypeName("NUMERIC");
        fieldADDRESS.setSize(15);
        fieldADDRESS.setShouldAllowNull(false);
        fieldADDRESS.setIsPrimaryKey(false);
        fieldADDRESS.setUnique(false);
        fieldADDRESS.setIsIdentity(false);
        fieldADDRESS.setForeignKeyFieldName("CMP3_MM_ADDRESS.ADDRESS_ID");
        table.addField(fieldADDRESS);

        FieldDefinition fieldADDRESS2 = new FieldDefinition();
        fieldADDRESS2.setName("SHIPPINGADDRESS_ADDRESS_ID");
        fieldADDRESS2.setTypeName("NUMERIC");
        fieldADDRESS2.setSize(15);
        fieldADDRESS2.setShouldAllowNull(false);
        fieldADDRESS2.setIsPrimaryKey(false);
        fieldADDRESS2.setUnique(false);
        fieldADDRESS2.setIsIdentity(false);
        fieldADDRESS2.setForeignKeyFieldName("CMP3_MM_ADDRESS.ADDRESS_ID");
        table.addField(fieldADDRESS2);

        return table;
    }
    
    public static TableDefinition buildMETAMODEL_CUSTOMER_ORDER_JOINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_CUSTOMER_MM_ORDER");

        FieldDefinition fieldORDER = new FieldDefinition();
        fieldORDER.setName("ORDER_ID");
        fieldORDER.setTypeName("NUMERIC");
        fieldORDER.setSize(15);
        fieldORDER.setShouldAllowNull(false);
        fieldORDER.setIsPrimaryKey(false);
        fieldORDER.setUnique(false);
        fieldORDER.setIsIdentity(false);
        fieldORDER.setForeignKeyFieldName("CMP3_MM_ORDER.ORDER_ID");
        table.addField(fieldORDER);        

        FieldDefinition fieldCUSTOMER = new FieldDefinition();
        fieldCUSTOMER.setName("CUSTOMER_ID");
        fieldCUSTOMER.setTypeName("NUMERIC");
        fieldCUSTOMER.setSize(15);
        fieldCUSTOMER.setShouldAllowNull(false);
        fieldCUSTOMER.setIsPrimaryKey(false);
        fieldCUSTOMER.setUnique(false);
        fieldCUSTOMER.setIsIdentity(false);
        fieldCUSTOMER.setForeignKeyFieldName("CMP3_MM_CUSTOMER.LIFEFORM_ID");
        table.addField(fieldCUSTOMER);        
        
        return table;
    }

/*    public static TableDefinition buildMETAMODEL_ORDER_ITEM_JOINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_ORDER_MM_ITEM");

        FieldDefinition fieldORDER = new FieldDefinition();
        fieldORDER.setName("ORDER_ID");
        fieldORDER.setTypeName("NUMERIC");
        fieldORDER.setSize(15);
        fieldORDER.setShouldAllowNull(false);
        fieldORDER.setIsPrimaryKey(false);
        fieldORDER.setUnique(false);
        fieldORDER.setIsIdentity(false);
        fieldORDER.setForeignKeyFieldName("CMP3_MM_ORDER.ORDER_ID");
        table.addField(fieldORDER);        

        FieldDefinition fieldCUSTOMER = new FieldDefinition();
        fieldCUSTOMER.setName("ITEM_ID");
        fieldCUSTOMER.setTypeName("NUMERIC");
        fieldCUSTOMER.setSize(15);
        fieldCUSTOMER.setShouldAllowNull(false);
        fieldCUSTOMER.setIsPrimaryKey(false);
        fieldCUSTOMER.setUnique(false);
        fieldCUSTOMER.setIsIdentity(false);
        fieldCUSTOMER.setForeignKeyFieldName("CMP3_MM_ITEM.ITEM_ID");
        table.addField(fieldCUSTOMER);        
        
        return table;
    }*/
    
    public static TableDefinition buildMETAMODEL_ADDRESSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_ADDRESS");

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

        FieldDefinition fieldVERSION = new FieldDefinition();
        fieldVERSION.setName("ADDRESS_VERSION");
        fieldVERSION.setTypeName("NUMERIC");
        fieldVERSION.setSize(15);
        fieldVERSION.setShouldAllowNull(true);
        fieldVERSION.setIsPrimaryKey(false);
        fieldVERSION.setUnique(false);
        fieldVERSION.setIsIdentity(false);
        table.addField(fieldVERSION);

        // default DTYPE field when we subclass a non-entity
        FieldDefinition fieldType = new FieldDefinition();
        fieldType.setName("DTYPE");
        fieldType.setTypeName("VARCHAR2");
        fieldType.setSize(150);
        fieldType.setSubSize(0);
        fieldType.setIsPrimaryKey(false);
        fieldType.setIsIdentity(false);
        fieldType.setUnique(false);
        fieldType.setShouldAllowNull(true);
        table.addField(fieldType);

        return table;
    }

    public static TableDefinition buildMETAMODEL_HOMEADDRESSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_ADDRESS");
        //HOMEADDRESS_ADDRESS_ID
        return table;
    }
    
    public static TableDefinition buildMETAMODEL_ITEMTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_MM_ITEM");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ITEM_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("DESCRIPTION");
        fieldDESCRIPTION.setTypeName("VARCHAR2");
        fieldDESCRIPTION.setSize(128);
        fieldDESCRIPTION.setSubSize(0);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setIsIdentity(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setShouldAllowNull(true);
        table.addField(fieldDESCRIPTION);

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
        
        FieldDefinition fieldVERSION = new FieldDefinition();
        fieldVERSION.setName("ITEM_VERSION");
        fieldVERSION.setTypeName("NUMERIC");
        fieldVERSION.setSize(15);
        fieldVERSION.setShouldAllowNull(true);
        fieldVERSION.setIsPrimaryKey(false);
        fieldVERSION.setUnique(false);
        fieldVERSION.setIsIdentity(false);
        table.addField(fieldVERSION);
        
        return table;
    }
        
}
