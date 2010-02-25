/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/02/2008-1.0M6 Guy Pelletier 
 *       - 224155: embeddable-attributes should be extended in the EclipseLink ORM.XML schema
 *     08/27/2008-1.1 Guy Pelletier 
 *       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
 *     08/28/2008-1.1 Guy Pelletier 
 *       - 245120: unidir one-to-many within embeddable fails to deploy for missing primary key field  
 *     02/08/2010-2.1 Michael O'Brien
 *       - 260271:  for the "NUMERIC" ID field - increase from 15 to 18 to match XML_OWNERSHIP_GROUP.ID as it is the default precision size     
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.complexaggregate;

import java.util.Iterator;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.tools.schemaframework.*;

public class ComplexAggregateTableCreator extends org.eclipse.persistence.tools.schemaframework.TableCreator {
    public ComplexAggregateTableCreator() {
        setName("ComplexAggregateProject");

        addTableDefinition(buildCITYSLICKERTable());
        addTableDefinition(buildCOUNTRYDWELLERTable());
        addTableDefinition(buildWORLDTable());
        
        addTableDefinition(buildHOCKEYPLAYERTable());
        addTableDefinition(buildHOCKEYTEAMTable());
        
        addTableDefinition(buildOWNERSHIPGROUPTable());
        addTableDefinition(buildPRIVILEGESTable());
        
        addTableDefinition(buildROLETable());
        addTableDefinition(buildPLAYERROLESTable());
    }
    
    public TableDefinition buildCITYSLICKERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CITYSLICKER");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(18);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldFNAME = new FieldDefinition();
        fieldFNAME.setName("FNAME");
        fieldFNAME.setTypeName("VARCHAR2");
        fieldFNAME.setSize(20);
        fieldFNAME.setSubSize(0);
        fieldFNAME.setIsPrimaryKey(true);
        fieldFNAME.setIsIdentity(false);
        fieldFNAME.setUnique(true);
        fieldFNAME.setShouldAllowNull(false);
        table.addField(fieldFNAME);
        
        FieldDefinition fieldLNAME = new FieldDefinition();
        fieldLNAME.setName("LNAME");
        fieldLNAME.setTypeName("VARCHAR2");
        fieldLNAME.setSize(20);
        fieldLNAME.setSubSize(0);
        fieldLNAME.setIsPrimaryKey(true);
        fieldLNAME.setIsIdentity(false);
        fieldLNAME.setUnique(true);
        fieldLNAME.setShouldAllowNull(false);
        table.addField(fieldLNAME);
        
        FieldDefinition fieldAGE = new FieldDefinition();
        fieldAGE.setName("AGE");
        fieldAGE.setTypeName("NUMBER");
        fieldAGE.setSize(15);
        fieldAGE.setSubSize(0);
        fieldAGE.setIsPrimaryKey(false);
        fieldAGE.setIsIdentity(false);
        fieldAGE.setUnique(false);
        fieldAGE.setShouldAllowNull(false);
        table.addField(fieldAGE);
        
        FieldDefinition fieldGENDER = new FieldDefinition();
        fieldGENDER.setName("GENDER");
        fieldGENDER.setTypeName("VARCHAR2");
        fieldGENDER.setSize(6);
        fieldGENDER.setSubSize(0);
        fieldGENDER.setIsPrimaryKey(false);
        fieldGENDER.setIsIdentity(false);
        fieldGENDER.setUnique(false);
        fieldGENDER.setShouldAllowNull(true);
        table.addField(fieldGENDER);

        FieldDefinition fieldWORLDID = new FieldDefinition();
        fieldWORLDID.setName("WORLD_ID");
        fieldWORLDID.setTypeName("NUMBER");
        fieldWORLDID.setSize(18);
        fieldWORLDID.setSubSize(0);
        fieldWORLDID.setIsPrimaryKey(false);
        fieldWORLDID.setIsIdentity(false);
        fieldWORLDID.setUnique(false);
        fieldWORLDID.setShouldAllowNull(true);
        fieldWORLDID.setForeignKeyFieldName("XML_WORLD.ID");
        table.addField(fieldWORLDID);
        
        return table;
    }
    
    public TableDefinition buildCOUNTRYDWELLERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_COUNTRY_DWELLER");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(18);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldFNAME = new FieldDefinition();
        fieldFNAME.setName("FNAME");
        fieldFNAME.setTypeName("VARCHAR2");
        fieldFNAME.setSize(20);
        fieldFNAME.setSubSize(0);
        fieldFNAME.setIsPrimaryKey(true);
        fieldFNAME.setIsIdentity(false);
        fieldFNAME.setUnique(true);
        fieldFNAME.setShouldAllowNull(false);
        table.addField(fieldFNAME);
        
        FieldDefinition fieldLNAME = new FieldDefinition();
        fieldLNAME.setName("LNAME");
        fieldLNAME.setTypeName("VARCHAR2");
        fieldLNAME.setSize(20);
        fieldLNAME.setSubSize(0);
        fieldLNAME.setIsPrimaryKey(true);
        fieldLNAME.setIsIdentity(false);
        fieldLNAME.setUnique(true);
        fieldLNAME.setShouldAllowNull(false);
        table.addField(fieldLNAME);
        
        FieldDefinition fieldAGE = new FieldDefinition();
        fieldAGE.setName("AGE");
        fieldAGE.setTypeName("NUMBER");
        fieldAGE.setSize(15);
        fieldAGE.setSubSize(0);
        fieldAGE.setIsPrimaryKey(false);
        fieldAGE.setIsIdentity(false);
        fieldAGE.setUnique(false);
        fieldAGE.setShouldAllowNull(false);
        table.addField(fieldAGE);

        FieldDefinition fieldGENDER = new FieldDefinition();
        fieldGENDER.setName("GENDER");
        fieldGENDER.setTypeName("VARCHAR2");
        fieldGENDER.setSize(6);
        fieldGENDER.setSubSize(0);
        fieldGENDER.setIsPrimaryKey(false);
        fieldGENDER.setIsIdentity(false);
        fieldGENDER.setUnique(false);
        fieldGENDER.setShouldAllowNull(true);
        table.addField(fieldGENDER);
        
        FieldDefinition fieldWORLDID = new FieldDefinition();
        fieldWORLDID.setName("WORLD_ID");
        fieldWORLDID.setTypeName("NUMBER");
        fieldWORLDID.setSize(18);
        fieldWORLDID.setSubSize(0);
        fieldWORLDID.setIsPrimaryKey(false);
        fieldWORLDID.setIsIdentity(false);
        fieldWORLDID.setUnique(false);
        fieldWORLDID.setShouldAllowNull(true);
        fieldWORLDID.setForeignKeyFieldName("XML_WORLD.ID");
        table.addField(fieldWORLDID);
        
        return table;
    }
    
    public TableDefinition buildHOCKEYPLAYERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_HOCKEY_PLAYER");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("PLAYERID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(18);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldFNAME = new FieldDefinition();
        fieldFNAME.setName("FNAME");
        fieldFNAME.setTypeName("VARCHAR2");
        fieldFNAME.setSize(20);
        fieldFNAME.setSubSize(0);
        fieldFNAME.setIsPrimaryKey(false);
        fieldFNAME.setIsIdentity(false);
        fieldFNAME.setUnique(false);
        fieldFNAME.setShouldAllowNull(false);
        table.addField(fieldFNAME);
        
        FieldDefinition fieldLNAME = new FieldDefinition();
        fieldLNAME.setName("LNAME");
        fieldLNAME.setTypeName("VARCHAR2");
        fieldLNAME.setSize(20);
        fieldLNAME.setSubSize(0);
        fieldLNAME.setIsPrimaryKey(false);
        fieldLNAME.setIsIdentity(false);
        fieldLNAME.setUnique(false);
        fieldLNAME.setShouldAllowNull(false);
        table.addField(fieldLNAME);
        
        FieldDefinition fieldAGE = new FieldDefinition();
        fieldAGE.setName("AGE");
        fieldAGE.setTypeName("NUMBER");
        fieldAGE.setSize(15);
        fieldAGE.setSubSize(0);
        fieldAGE.setIsPrimaryKey(false);
        fieldAGE.setIsIdentity(false);
        fieldAGE.setUnique(false);
        fieldAGE.setShouldAllowNull(true);
        table.addField(fieldAGE);
        
        FieldDefinition fieldHEIGHT = new FieldDefinition();
        fieldHEIGHT.setName("HEIGHT");
        fieldHEIGHT.setTypeName("DOUBLE PRECIS");
        fieldHEIGHT.setSize(15);
        fieldHEIGHT.setSubSize(0);
        fieldHEIGHT.setIsPrimaryKey(false);
        fieldHEIGHT.setIsIdentity(false);
        fieldHEIGHT.setUnique(false);
        fieldHEIGHT.setShouldAllowNull(true);
        table.addField(fieldHEIGHT);
        
        FieldDefinition fieldWEIGHT = new FieldDefinition();
        fieldWEIGHT.setName("WEIGHT");
        fieldWEIGHT.setTypeName("DOUBLE PRECIS");
        fieldWEIGHT.setSize(15);
        fieldWEIGHT.setSubSize(0);
        fieldWEIGHT.setIsPrimaryKey(false);
        fieldWEIGHT.setIsIdentity(false);
        fieldWEIGHT.setUnique(false);
        fieldWEIGHT.setShouldAllowNull(true);
        table.addField(fieldWEIGHT);
        
        FieldDefinition fieldJERSEYNUMBER = new FieldDefinition();
        fieldJERSEYNUMBER.setName("JERSEY_NUMBER");
        fieldJERSEYNUMBER.setTypeName("NUMBER");
        fieldJERSEYNUMBER.setSize(15);
        fieldJERSEYNUMBER.setSubSize(0);
        fieldJERSEYNUMBER.setIsPrimaryKey(false);
        fieldJERSEYNUMBER.setIsIdentity(false);
        fieldJERSEYNUMBER.setUnique(false);
        fieldJERSEYNUMBER.setShouldAllowNull(true);
        table.addField(fieldJERSEYNUMBER);
        
        FieldDefinition fieldPOSITION = new FieldDefinition();
        fieldPOSITION.setName("POS");
        fieldPOSITION.setTypeName("VARCHAR2");
        fieldPOSITION.setSize(20);
        fieldPOSITION.setSubSize(0);
        fieldPOSITION.setIsPrimaryKey(false);
        fieldPOSITION.setIsIdentity(false);
        fieldPOSITION.setUnique(false);
        fieldPOSITION.setShouldAllowNull(true);
        table.addField(fieldPOSITION);
        
        FieldDefinition fieldTEAMID = new FieldDefinition();
        fieldTEAMID.setName("TEAM_ID");
        fieldTEAMID.setTypeName("NUMBER");
        fieldTEAMID.setSize(18);
        fieldTEAMID.setSubSize(0);
        fieldTEAMID.setIsPrimaryKey(false);
        fieldTEAMID.setIsIdentity(false);
        fieldTEAMID.setUnique(false);
        fieldTEAMID.setShouldAllowNull(true);
        table.addField(fieldTEAMID);
        
        FieldDefinition fieldTEAMIDDESCRIPTION = new FieldDefinition();
        fieldTEAMIDDESCRIPTION.setName("TEAM_ID_DESC");
        fieldTEAMIDDESCRIPTION.setTypeName("VARCHAR2");
        fieldTEAMIDDESCRIPTION.setSize(50);
        fieldTEAMIDDESCRIPTION.setSubSize(0);
        fieldTEAMIDDESCRIPTION.setIsPrimaryKey(false);
        fieldTEAMIDDESCRIPTION.setIsIdentity(false);
        fieldTEAMIDDESCRIPTION.setUnique(false);
        fieldTEAMIDDESCRIPTION.setShouldAllowNull(true);
        table.addField(fieldTEAMIDDESCRIPTION);
        
        return table;
    }
    
    public TableDefinition buildHOCKEYTEAMTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_HOCKEY_TEAM");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(18);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldIDDESCRIPTION = new FieldDefinition();
        fieldIDDESCRIPTION.setName("ID_DESC");
        fieldIDDESCRIPTION.setTypeName("VARCHAR2");
        fieldIDDESCRIPTION.setSize(50);
        fieldIDDESCRIPTION.setSubSize(0);
        fieldIDDESCRIPTION.setIsPrimaryKey(true);
        fieldIDDESCRIPTION.setIsIdentity(false);
        fieldIDDESCRIPTION.setUnique(false);
        fieldIDDESCRIPTION.setShouldAllowNull(false);
        table.addField(fieldIDDESCRIPTION);
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(20);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(false);
        table.addField(fieldNAME);
        
        FieldDefinition fieldLEVEL = new FieldDefinition();
        fieldLEVEL.setName("TEAM_LEVEL");
        fieldLEVEL.setTypeName("VARCHAR2");
        fieldLEVEL.setSize(20);
        fieldLEVEL.setSubSize(0);
        fieldLEVEL.setIsPrimaryKey(false);
        fieldLEVEL.setIsIdentity(false);
        fieldLEVEL.setUnique(false);
        fieldLEVEL.setShouldAllowNull(false);
        table.addField(fieldLEVEL);

        FieldDefinition fieldHOMECOLOR = new FieldDefinition();
        fieldHOMECOLOR.setName("HOME_COLOR");
        fieldHOMECOLOR.setTypeName("VARCHAR2");
        fieldHOMECOLOR.setSize(20);
        fieldHOMECOLOR.setSubSize(0);
        fieldHOMECOLOR.setIsPrimaryKey(false);
        fieldHOMECOLOR.setIsIdentity(false);
        fieldHOMECOLOR.setUnique(false);
        fieldHOMECOLOR.setShouldAllowNull(true);
        table.addField(fieldHOMECOLOR);
        
        FieldDefinition fieldAWAYCOLOR = new FieldDefinition();
        fieldAWAYCOLOR.setName("AWAY_COLOR");
        fieldAWAYCOLOR.setTypeName("VARCHAR2");
        fieldAWAYCOLOR.setSize(20);
        fieldAWAYCOLOR.setSubSize(0);
        fieldAWAYCOLOR.setIsPrimaryKey(false);
        fieldAWAYCOLOR.setIsIdentity(false);
        fieldAWAYCOLOR.setUnique(false);
        fieldAWAYCOLOR.setShouldAllowNull(true);
        table.addField(fieldAWAYCOLOR);
        
        FieldDefinition fieldOWNERID = new FieldDefinition();
        fieldOWNERID.setName("OWNER_ID");
        fieldOWNERID.setTypeName("NUMBER");
        fieldOWNERID.setSize(18);
        fieldOWNERID.setSubSize(0);
        fieldOWNERID.setIsPrimaryKey(false);
        fieldOWNERID.setIsIdentity(false);
        fieldOWNERID.setUnique(false);
        fieldOWNERID.setShouldAllowNull(true);
        fieldOWNERID.setForeignKeyFieldName("XML_OWNERSHIP_GROUP.ID");
        table.addField(fieldOWNERID);
        
        return table;
    }
    
    public TableDefinition buildOWNERSHIPGROUPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_OWNERSHIP_GROUP");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(18);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(20);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(false);
        table.addField(fieldNAME);
        
        FieldDefinition fieldVERSION = new FieldDefinition();
        fieldVERSION.setName("VERSION");
        fieldVERSION.setTypeName("NUMERIC");
        fieldVERSION.setSize(15);
        fieldVERSION.setShouldAllowNull(true );
        fieldVERSION.setIsPrimaryKey(false );
        fieldVERSION.setUnique(false );
        fieldVERSION.setIsIdentity(false );
        table.addField(fieldVERSION);
        
        return table;
    }
    
    public TableDefinition buildPLAYERROLESTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_PLAYER_ROLES");

        FieldDefinition fieldPLAYERID = new FieldDefinition();
        fieldPLAYERID.setName("PLAYER_ID");
        fieldPLAYERID.setTypeName("NUMBER");
        fieldPLAYERID.setSize(18);
        fieldPLAYERID.setSubSize(0);
        fieldPLAYERID.setIsPrimaryKey(false);
        fieldPLAYERID.setIsIdentity(false);
        fieldPLAYERID.setUnique(false);
        fieldPLAYERID.setShouldAllowNull(false);
        fieldPLAYERID.setForeignKeyFieldName("XML_HOCKEY_PLAYER.PLAYERID");
        table.addField(fieldPLAYERID);
        
        FieldDefinition fieldROLEID = new FieldDefinition();
        fieldROLEID.setName("ROLE_ID");
        fieldROLEID.setTypeName("NUMBER");
        fieldROLEID.setSize(18);
        fieldROLEID.setSubSize(0);
        fieldROLEID.setIsPrimaryKey(false);
        fieldROLEID.setIsIdentity(false);
        fieldROLEID.setUnique(false);
        fieldROLEID.setShouldAllowNull(false);
        fieldROLEID.setForeignKeyFieldName("XML_ROLE.ID");
        table.addField(fieldROLEID);
        
        return table;
    }
    
    public TableDefinition buildROLETable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_ROLE");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(18);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("DESCRIP");
        fieldDESCRIPTION.setTypeName("VARCHAR2");
        fieldDESCRIPTION.setSize(50);
        fieldDESCRIPTION.setSubSize(0);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setIsIdentity(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setShouldAllowNull(false);
        table.addField(fieldDESCRIPTION);
        
        return table;
    }
    
    public static TableDefinition buildPRIVILEGESTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_OWNER_PRIVILEGES");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("OWNER_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(18); // 260271: increase from 15 to 18 to match XML_OWNERSHIP_GROUP.ID as it is the default precision for "NUMERIC" fields
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("XML_OWNERSHIP_GROUP.ID");
        table.addField(fieldID);
    
        FieldDefinition fieldPRIVILEGES = new FieldDefinition();
        fieldPRIVILEGES.setName("PRIVILEGE");
        fieldPRIVILEGES.setTypeName("VARCHAR");
        fieldPRIVILEGES.setSize(200);
        fieldPRIVILEGES.setShouldAllowNull(false);
        fieldPRIVILEGES.setIsPrimaryKey(false);
        fieldPRIVILEGES.setUnique(false);
        fieldPRIVILEGES.setIsIdentity(false);
        table.addField(fieldPRIVILEGES);
    
        return table;
    }

     public TableDefinition buildWORLDTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_WORLD");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(18);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        return table;
    }

    /**
     * Remove pk constraint as there is a identity field, and H2, HSQL do not allow this.
     */
    public void replaceTables(DatabaseSession session, SchemaManager schemaManager) {
        if (session.getPlatform().isH2() || session.getPlatform().isHSQL()) {
            for (Iterator iterator = getTableDefinitions().iterator(); iterator.hasNext(); ) {
                TableDefinition table = (TableDefinition)iterator.next();
                if (table.getName().equals("XML_CITYSLICKER") || table.getName().equals("XML_COUNTRY_DWELLER")) {
                    for (Iterator fields = table.getFields().iterator(); fields.hasNext(); ) {
                        ((FieldDefinition)fields.next()).setIsPrimaryKey(false);
                    }
                }
            }
        }
        super.replaceTables(session, schemaManager);
    }

}
