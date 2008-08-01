/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/30/2008-1.0M8 Guy Pelletier 
 *       - 230213: ValidationException when mapping to attribute in MappedSuperClass
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

public class InheritedTableManager extends TableCreator {
    public static TableCreator tableCreator;

    public InheritedTableManager() {
        setName("EJB3BeerProject");

        addTableDefinition(build_BEER_CONSUMER_Table());
        
        addTableDefinition(build_EXPERT_BEER_CONSUMER_Table());
        addTableDefinition(build_EXPERT_BEER_CONSUMER_AWARDS_Table());
        addTableDefinition(build_EXPERT_BEER_CONSUMER_ACCLAIMS_Table());
        
        addTableDefinition(build_NOVICE_BEER_CONSUMER_Table());
        addTableDefinition(build_NOVICE_BEER_CONSUMER_AWARDS_Table());
        addTableDefinition(build_NOVICE_BEER_CONSUMER_ACCLAIMS_Table());
        
        addTableDefinition(build_ALPINE_Table());
        addTableDefinition(build_BLUE_Table());
        addTableDefinition(build_CANADIAN_Table());
        
        addTableDefinition(build_CERTIFICATION_Table());
        addTableDefinition(build_SERIALNUMBER_Table());
        addTableDefinition(build_TELEPHONE_NUMBER_Table());
    }
    
    public static TableDefinition build_ALPINE_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_ALPINE");
    
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(false);
        ID_field.setForeignKeyFieldName("CMP3_SERIAL_NUMBER.S_NUMBER"); 
        table.addField(ID_field);
    
        FieldDefinition ALCOHOL_CONTENT_field = new FieldDefinition();
        ALCOHOL_CONTENT_field.setName("ALCOHOL_CONTENT");
        ALCOHOL_CONTENT_field.setTypeName("DOUBLE PRECIS");
        ALCOHOL_CONTENT_field.setSize(15);
        ALCOHOL_CONTENT_field.setIsPrimaryKey(false);
        ALCOHOL_CONTENT_field.setUnique(false);
        ALCOHOL_CONTENT_field.setIsIdentity(false);
        ALCOHOL_CONTENT_field.setShouldAllowNull(true);
        table.addField(ALCOHOL_CONTENT_field);
        
        FieldDefinition BEST_BEFORE_DATE_field = new FieldDefinition();
        BEST_BEFORE_DATE_field.setName("BB_DATE");
        BEST_BEFORE_DATE_field.setTypeName("DATETIME");
        BEST_BEFORE_DATE_field.setSize(23);
        BEST_BEFORE_DATE_field.setIsPrimaryKey(false);
        BEST_BEFORE_DATE_field.setUnique(false);
        BEST_BEFORE_DATE_field.setIsIdentity(false);
        BEST_BEFORE_DATE_field.setShouldAllowNull(true);
        table.addField(BEST_BEFORE_DATE_field);
        
        FieldDefinition FLAVOR_field = new FieldDefinition();
        FLAVOR_field.setName("CLASSIFICATION");
        FLAVOR_field.setTypeName("NUMERIC");
        FLAVOR_field.setSize(15);
        FLAVOR_field.setIsPrimaryKey(false);
        FLAVOR_field.setUnique(false);
        FLAVOR_field.setIsIdentity(false);
        FLAVOR_field.setShouldAllowNull(true);
        table.addField(FLAVOR_field);
        
        FieldDefinition BEER_CONSUMER_ID_field = new FieldDefinition();
        BEER_CONSUMER_ID_field.setName("C_ID");
        BEER_CONSUMER_ID_field.setTypeName("NUMERIC");
        BEER_CONSUMER_ID_field.setSize(15);
        BEER_CONSUMER_ID_field.setIsPrimaryKey(false);
        BEER_CONSUMER_ID_field.setUnique(false);
        BEER_CONSUMER_ID_field.setIsIdentity(false);
        BEER_CONSUMER_ID_field.setShouldAllowNull(true);
        BEER_CONSUMER_ID_field.setForeignKeyFieldName("CMP3_CONSUMER.ID");
        table.addField(BEER_CONSUMER_ID_field);
        
        FieldDefinition VERSION_field = new FieldDefinition();
        VERSION_field.setName("VERSION");
        VERSION_field.setTypeName("DATETIME");
        VERSION_field.setSize(23);
        VERSION_field.setIsPrimaryKey(false);
        VERSION_field.setUnique(false);
        VERSION_field.setIsIdentity(false);
        VERSION_field.setShouldAllowNull(true);
        table.addField(VERSION_field);
        
        FieldDefinition fieldINSPECTIONDATES = new FieldDefinition();
        fieldINSPECTIONDATES.setName("I_DATES");
        fieldINSPECTIONDATES.setTypeName("LONG RAW");
        fieldINSPECTIONDATES.setSize(100);
        fieldINSPECTIONDATES.setSubSize(0);
        fieldINSPECTIONDATES.setIsPrimaryKey(false);
        fieldINSPECTIONDATES.setIsIdentity(false);
        fieldINSPECTIONDATES.setUnique(false);
        fieldINSPECTIONDATES.setShouldAllowNull(true);
        table.addField(fieldINSPECTIONDATES);

        return table;
    }
    
    public static TableDefinition build_BEER_CONSUMER_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_CONSUMER");
    
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(false);
        table.addField(ID_field);

        FieldDefinition NAME_field = new FieldDefinition();
        NAME_field.setName("NAME");
        NAME_field.setTypeName("VARCHAR");
        NAME_field.setSize(40);
        NAME_field.setShouldAllowNull(true);
        NAME_field.setIsPrimaryKey(false);
        NAME_field.setUnique(false);
        NAME_field.setIsIdentity(false);
        table.addField(NAME_field);
        
        FieldDefinition VERSION_field = new FieldDefinition();
        VERSION_field.setName("VERSION");
        VERSION_field.setTypeName("NUMERIC");
        VERSION_field.setSize(15);
        VERSION_field.setShouldAllowNull(true);
        VERSION_field.setIsPrimaryKey(false);
        VERSION_field.setUnique(false);
        VERSION_field.setIsIdentity(false);
        table.addField(VERSION_field);
        
        FieldDefinition DTYPE_field = new FieldDefinition();
        DTYPE_field.setName("DTYPE");
        DTYPE_field.setTypeName("VARCHAR2");
        DTYPE_field.setSize(3);
        DTYPE_field.setSubSize(0);
        DTYPE_field.setIsPrimaryKey(false);
        DTYPE_field.setIsIdentity(false);
        DTYPE_field.setUnique(false);
        DTYPE_field.setShouldAllowNull(true);
        table.addField(DTYPE_field);

        return table;
    }
    
    public static TableDefinition build_BLUE_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_BLUE");
    
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(false);
        table.addField(ID_field);
    
        FieldDefinition ALCOHOL_CONTENT_field = new FieldDefinition();
        ALCOHOL_CONTENT_field.setName("ALCOHOL_CONTENT");
        ALCOHOL_CONTENT_field.setTypeName("DOUBLE PRECIS");
        ALCOHOL_CONTENT_field.setSize(15);
        ALCOHOL_CONTENT_field.setIsPrimaryKey(false);
        ALCOHOL_CONTENT_field.setUnique(false);
        ALCOHOL_CONTENT_field.setIsIdentity(false);
        ALCOHOL_CONTENT_field.setShouldAllowNull(true);
        table.addField(ALCOHOL_CONTENT_field);
        
        FieldDefinition BEER_CONSUMER_ID_field = new FieldDefinition();
        BEER_CONSUMER_ID_field.setName("C_ID");
        BEER_CONSUMER_ID_field.setTypeName("NUMERIC");
        BEER_CONSUMER_ID_field.setSize(15);
        BEER_CONSUMER_ID_field.setIsPrimaryKey(false);
        BEER_CONSUMER_ID_field.setUnique(false);
        BEER_CONSUMER_ID_field.setIsIdentity(false);
        BEER_CONSUMER_ID_field.setShouldAllowNull(true);
        BEER_CONSUMER_ID_field.setForeignKeyFieldName("CMP3_CONSUMER.ID");
        table.addField(BEER_CONSUMER_ID_field);
        
        FieldDefinition VERSION_field = new FieldDefinition();
        VERSION_field.setName("VERSION");
        VERSION_field.setTypeName("DATETIME");
        VERSION_field.setSize(23);
        VERSION_field.setIsPrimaryKey(false);
        VERSION_field.setUnique(false);
        VERSION_field.setIsIdentity(false);
        VERSION_field.setShouldAllowNull(true);
        table.addField(VERSION_field);
        
        FieldDefinition UNIQUE_KEY_field = new FieldDefinition();
        UNIQUE_KEY_field.setName("UNIQUEKEY");
        UNIQUE_KEY_field.setTypeName("NUMERIC");
        UNIQUE_KEY_field.setSize(15);
        UNIQUE_KEY_field.setIsPrimaryKey(false);
        UNIQUE_KEY_field.setUnique(true);
        UNIQUE_KEY_field.setIsIdentity(false);
        UNIQUE_KEY_field.setShouldAllowNull(false);
        table.addField(UNIQUE_KEY_field);
        
        FieldDefinition DTYPE_field = new FieldDefinition();
        DTYPE_field.setName("DTYPE");
        DTYPE_field.setTypeName("VARCHAR2");
        DTYPE_field.setSize(20);
        DTYPE_field.setSubSize(0);
        DTYPE_field.setIsPrimaryKey(false);
        DTYPE_field.setIsIdentity(false);
        DTYPE_field.setUnique(false);
        DTYPE_field.setShouldAllowNull(true);
        table.addField(DTYPE_field);

        return table;
    }
    
    public static TableDefinition build_CANADIAN_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_CANADIAN");
    
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(false);
        table.addField(ID_field);
    
        FieldDefinition ALCOHOL_CONTENT_field = new FieldDefinition();
        ALCOHOL_CONTENT_field.setName("ALCOHOL_CONTENT");
        ALCOHOL_CONTENT_field.setTypeName("DOUBLE PRECIS");
        ALCOHOL_CONTENT_field.setSize(15);
        ALCOHOL_CONTENT_field.setIsPrimaryKey(false);
        ALCOHOL_CONTENT_field.setUnique(false);
        ALCOHOL_CONTENT_field.setIsIdentity(false);
        ALCOHOL_CONTENT_field.setShouldAllowNull(true);
        table.addField(ALCOHOL_CONTENT_field);
        
        FieldDefinition BORN_ON_DATE_field = new FieldDefinition();
        BORN_ON_DATE_field.setName("BORN");
        BORN_ON_DATE_field.setTypeName("DATETIME");
        BORN_ON_DATE_field.setSize(23);
        BORN_ON_DATE_field.setIsPrimaryKey(false);
        BORN_ON_DATE_field.setUnique(false);
        BORN_ON_DATE_field.setIsIdentity(false);
        BORN_ON_DATE_field.setShouldAllowNull(true);
        table.addField(BORN_ON_DATE_field);
        
        FieldDefinition FLAVOR_field = new FieldDefinition();
        FLAVOR_field.setName("FLAVOR");
        FLAVOR_field.setTypeName("INTEGER");
        FLAVOR_field.setSize(23);
        FLAVOR_field.setIsPrimaryKey(false);
        FLAVOR_field.setUnique(false);
        FLAVOR_field.setIsIdentity(false);
        FLAVOR_field.setShouldAllowNull(true);
        table.addField(FLAVOR_field);

        FieldDefinition BEER_CONSUMER_ID_field = new FieldDefinition();
        BEER_CONSUMER_ID_field.setName("CONSUMER_ID");
        BEER_CONSUMER_ID_field.setTypeName("NUMERIC");
        BEER_CONSUMER_ID_field.setSize(15);
        BEER_CONSUMER_ID_field.setIsPrimaryKey(false);
        BEER_CONSUMER_ID_field.setUnique(false);
        BEER_CONSUMER_ID_field.setIsIdentity(false);
        BEER_CONSUMER_ID_field.setShouldAllowNull(true);
        BEER_CONSUMER_ID_field.setForeignKeyFieldName("CMP3_CONSUMER.ID");
        table.addField(BEER_CONSUMER_ID_field);
        
        FieldDefinition fieldPROPERTIES = new FieldDefinition();
        fieldPROPERTIES.setName("PROPERTIES");
        fieldPROPERTIES.setTypeName("LONG RAW");
        fieldPROPERTIES.setSize(200);
        fieldPROPERTIES.setSubSize(0);
        fieldPROPERTIES.setIsPrimaryKey(false);
        fieldPROPERTIES.setIsIdentity(false);
        fieldPROPERTIES.setUnique(false);
        fieldPROPERTIES.setShouldAllowNull(true);
        table.addField(fieldPROPERTIES);

        FieldDefinition VERSION_field = new FieldDefinition();
        VERSION_field.setName("VERSION");
        VERSION_field.setTypeName("DATETIME");
        VERSION_field.setSize(23);
        VERSION_field.setIsPrimaryKey(false);
        VERSION_field.setUnique(false);
        VERSION_field.setIsIdentity(false);
        VERSION_field.setShouldAllowNull(true);
        table.addField(VERSION_field);
        
        return table;
    }
    
    public static TableDefinition build_CERTIFICATION_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_CERTIFICATION");
    
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(false);
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
        
        FieldDefinition BEER_CONSUMER_ID_field = new FieldDefinition();
        BEER_CONSUMER_ID_field.setName("CONSUMER_ID");
        BEER_CONSUMER_ID_field.setTypeName("NUMERIC");
        BEER_CONSUMER_ID_field.setSize(15);
        BEER_CONSUMER_ID_field.setIsPrimaryKey(false);
        BEER_CONSUMER_ID_field.setUnique(false);
        BEER_CONSUMER_ID_field.setIsIdentity(false);
        BEER_CONSUMER_ID_field.setShouldAllowNull(true);
        BEER_CONSUMER_ID_field.setForeignKeyFieldName("CMP3_CONSUMER.ID");
        table.addField(BEER_CONSUMER_ID_field);

        return table;
    }
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("EXPERT_CONSUMER");
    
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(false);
        ID_field.setForeignKeyFieldName("CMP3_CONSUMER.ID");
        table.addField(ID_field);
        
        return table;
    }
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_ACCLAIMS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("EXPERTBEERCONSUMER_ACCLAIMS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("EXPERT_CONSUMER.ID");
        table.addField(fieldID);
    
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("ACCLAIM");
        field1.setTypeName("VARCHAR");
        field1.setSize(20);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
    
        return table;
    }
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_AWARDS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("EXPERTBEERCONSUMER_AWARDS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("EXPERT_CONSUMER.ID");
        table.addField(fieldID);
    
        FieldDefinition fieldAWARDS_KEY = new FieldDefinition();
        fieldAWARDS_KEY.setName("AWARDS_KEY");
        fieldAWARDS_KEY.setTypeName("VARCHAR");
        fieldAWARDS_KEY.setSize(20);
        fieldAWARDS_KEY.setShouldAllowNull(false);
        fieldAWARDS_KEY.setIsPrimaryKey(false);
        fieldAWARDS_KEY.setUnique(true);
        fieldAWARDS_KEY.setIsIdentity(false);
        table.addField(fieldAWARDS_KEY);
        
        FieldDefinition fieldAWARD_CODE = new FieldDefinition();
        fieldAWARD_CODE.setName("AWARD_CODE");
        fieldAWARD_CODE.setTypeName("VARCHAR");
        fieldAWARD_CODE.setSize(20);
        fieldAWARD_CODE.setShouldAllowNull(false);
        fieldAWARD_CODE.setIsPrimaryKey(false);
        fieldAWARD_CODE.setUnique(false);
        fieldAWARD_CODE.setIsIdentity(false);
        table.addField(fieldAWARD_CODE);
    
        return table;
    }
    
    public static TableDefinition build_NOVICE_BEER_CONSUMER_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("NOVICE_CONSUMER");
    
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(false);
        ID_field.setForeignKeyFieldName("CMP3_CONSUMER.ID");
        table.addField(ID_field);
        
        return table;
    }
    
    public static TableDefinition build_NOVICE_BEER_CONSUMER_ACCLAIMS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("NOVICEBEERCONSUMER_ACCLAIMS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("NOVICE_CONSUMER.ID");
        table.addField(fieldID);
    
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("ACCLAIM");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
    
        return table;
    }
    
    public static TableDefinition build_NOVICE_BEER_CONSUMER_AWARDS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("NOVICEBEERCONSUMER_AWARDS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("NOVICE_CONSUMER.ID");
        table.addField(fieldID);
    
        FieldDefinition fieldAWARDS_KEY = new FieldDefinition();
        fieldAWARDS_KEY.setName("AWARDS_KEY");
        fieldAWARDS_KEY.setTypeName("NUMERIC");
        fieldAWARDS_KEY.setSize(10);
        fieldAWARDS_KEY.setShouldAllowNull(false);
        fieldAWARDS_KEY.setIsPrimaryKey(false);
        fieldAWARDS_KEY.setUnique(true);
        fieldAWARDS_KEY.setIsIdentity(false);
        table.addField(fieldAWARDS_KEY);
        
        FieldDefinition fieldAWARD_CODE = new FieldDefinition();
        fieldAWARD_CODE.setName("AWARD_CODE");
        fieldAWARD_CODE.setTypeName("NUMERIC");
        fieldAWARD_CODE.setSize(10);
        fieldAWARD_CODE.setShouldAllowNull(false);
        fieldAWARD_CODE.setIsPrimaryKey(false);
        fieldAWARD_CODE.setUnique(false);
        fieldAWARD_CODE.setIsIdentity(false);
        table.addField(fieldAWARD_CODE);
    
        return table;
    }
    
    public static TableDefinition build_SERIALNUMBER_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_SERIAL_NUMBER");
    
        FieldDefinition NUMBER_field = new FieldDefinition();
        NUMBER_field.setName("S_NUMBER");
        NUMBER_field.setTypeName("NUMERIC");
        NUMBER_field.setSize(15);
        NUMBER_field.setIsPrimaryKey(true);
        NUMBER_field.setUnique(false);
        NUMBER_field.setIsIdentity(false);
        NUMBER_field.setShouldAllowNull(false);
        table.addField(NUMBER_field);
        
        return table;
    }
    
    public static TableDefinition build_TELEPHONE_NUMBER_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_TELEPHONE");

        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("CONSUMER_ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(false);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(true);
        ID_field.setForeignKeyFieldName("CMP3_CONSUMER.ID");
        table.addField(ID_field);
    
        FieldDefinition TYPE_field = new FieldDefinition();
        TYPE_field.setName("TYPE");
        TYPE_field.setTypeName("VARCHAR");
        TYPE_field.setSize(15);
        TYPE_field.setIsPrimaryKey(true);
        TYPE_field.setUnique(false);
        TYPE_field.setIsIdentity(false);
        TYPE_field.setShouldAllowNull(false);
        table.addField(TYPE_field);
    
        FieldDefinition AREA_CODE_field = new FieldDefinition();
        AREA_CODE_field.setName("AREA_CODE");
        AREA_CODE_field.setTypeName("VARCHAR");
        AREA_CODE_field.setSize(3);
        AREA_CODE_field.setIsPrimaryKey(true);
        AREA_CODE_field.setUnique(false);
        AREA_CODE_field.setIsIdentity(false);
        AREA_CODE_field.setShouldAllowNull(false);
        table.addField(AREA_CODE_field);
    
        FieldDefinition NUMBER_field = new FieldDefinition();
        NUMBER_field.setName("TNUMBER");
        NUMBER_field.setTypeName("VARCHAR");
        NUMBER_field.setSize(8);
        NUMBER_field.setIsPrimaryKey(true);
        NUMBER_field.setUnique(false);
        NUMBER_field.setIsIdentity(false);
        NUMBER_field.setShouldAllowNull(false);
        table.addField(NUMBER_field);

        return table;
    }
    
    public static void createTables(Session session) {
        InheritedTableManager.getCreator().createTables((DatabaseSession) session);
    }
        
    public static void dropTables(Session session) {
        InheritedTableManager.getCreator().dropTables((DatabaseSession) session);
    }
        
    public static TableCreator getCreator(){
        if (InheritedTableManager.tableCreator == null) {
            InheritedTableManager.tableCreator = new InheritedTableManager();
        }
        
        return InheritedTableManager.tableCreator;
    }
}
