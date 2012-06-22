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
package org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_1;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class AdvancedTableCreator_1 extends TogglingFastTableCreator {

    public AdvancedTableCreator_1() {
        setName("xml.composite.member_1");
        this.ignoreDatabaseException = true;
        // table names prefixed with "XML_MBR1_"

        addTableDefinition(buildADDRESSTable());
// 3       addTableDefinition(buildCREDITCARDSTable());
// 3       addTableDefinition(buildCREDITLINESTable());
        addTableDefinition(buildCUSTOMERTable());
// 3       addTableDefinition(buildDEALERTable());
// 2        addTableDefinition(buildEMPLOYEETable());
// 3        addTableDefinition(buildLARGEPROJECTTable());
//         addTableDefinition(buildMANTable());
//        addTableDefinition(buildPARTNERLINKTable());
// 3        addTableDefinition(buildPHONENUMBERTable());
// 3        addTableDefinition(buildPROJECT_EMPTable());
// 3        addTableDefinition(buildPROJECTTable());
//        addTableDefinition(buildREADONLYCLASSTable());
// 3        addTableDefinition(buildRESPONSTable());
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
    
    public static TableDefinition buildADDRESSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MBR1_ADDRESS");

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

        return table;
    }

    public static TableDefinition buildCUSTOMERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MBR1_CUSTOMER");

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
//        field0.setForeignKeyFieldName("XML_MBR3_DEALER.DEALER_ID");
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
}
