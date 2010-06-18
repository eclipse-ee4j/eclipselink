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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/30/2008-1.0M8 Guy Pelletier 
 *       - 230213: ValidationException when mapping to attribute in MappedSuperClass
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)   
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/03/2009-2.0 Guy Pelletier
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     06/09/2009-2.0 Guy Pelletier 
 *       - 249037: JPA 2.0 persisting list item index
 *     01/26/2010-2.0.1 Guy Pelletier 
 *       - 299893: @MapKeyClass does not work with ElementCollection
 *     02/18/2010-2.0.2 Guy Pelletier 
 *       - 294803: @Column(updatable=false) has no effect on @Basic mappings
 *     06/18/2010-2.2 Guy Pelletier 
 *       - 300458: EclispeLink should throw a more specific exception than NPE
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;

public class InheritedTableManager extends TogglingFastTableCreator {
    public static TableCreator tableCreator;

    public InheritedTableManager() {
        setName("EJB3BeerProject");

        addTableDefinition(build_BEER_CONSUMER_Table());        
        addTableDefinition(build_BEER_CONSUMER_REDSTRIPES_Table());
        addTableDefinition(build_BEER_CONSUMER_REDSTRIPE_CONTENT_Table());
        
        addTableDefinition(build_EXPERT_BEER_CONSUMER_Table());
        addTableDefinition(build_EXPERT_BEER_CONSUMER_AWARDS_Table());
        addTableDefinition(build_EXPERT_BEER_CONSUMER_COURSES_Table());
        addTableDefinition(build_EXPERT_BEER_CONSUMER_ACCLAIMS_Table());
        addTableDefinition(build_EXPERT_BEER_CONSUMER_AUDIO_Table());
        addTableDefinition(build_EXPERT_BEER_CONSUMER_CELEBRATIONS_Table());
        addTableDefinition(build_EXPERT_BEER_CONSUMER_COMMITTEE_Table());
        addTableDefinition(build_EXPERT_BEER_CONSUMER_DESIGNATIONS_Table());
        addTableDefinition(build_EXPERT_BEER_CONSUMER_QUOTES_Table());
        addTableDefinition(build_EXPERT_BEER_CONSUMER_RECORDS_Table());
        addTableDefinition(build_EXPERT_BEER_CONSUMER_ACCREDIDATION_WITNESS_Table());
        
        addTableDefinition(build_NOVICE_BEER_CONSUMER_Table());
        addTableDefinition(build_NOVICE_BEER_CONSUMER_AWARDS_Table());
        addTableDefinition(build_NOVICE_BEER_CONSUMER_ACCLAIMS_Table());
        addTableDefinition(build_NOVICE_BEER_CONSUMER_COMMITTEE_Table());
        addTableDefinition(build_NOVICE_BEER_CONSUMER_DESIGNATIONS_Table());
        addTableDefinition(build_NOVICE_BEER_CONSUMER_RECORDS_Table());
        addTableDefinition(build_NOVICE_BEER_CONSUMER_ACCREDIDATION_WITNESS_Table());
        
        addTableDefinition(build_ALPINE_Table());
        addTableDefinition(build_BECKS_Table());
        addTableDefinition(build_BECKS_TAG_Table());
        addTableDefinition(build_BLUE_Table());
        addTableDefinition(build_CANADIAN_Table());
        addTableDefinition(build_CORONA_Table());
        addTableDefinition(build_HEINEKEN_Table());
        
        addTableDefinition(build_OFFICIAL_Table());
        addTableDefinition(build_OFFICIAL_COMPENSATIONTable());
        addTableDefinition(build_OFFICIAL_ENTRY_Table());
        addTableDefinition(build_WITNESS_Table());
        addTableDefinition(build_CERTIFICATION_Table());
        addTableDefinition(build_COMMITTEE_Table());
        addTableDefinition(build_SERIALNUMBER_Table());
        addTableDefinition(build_TELEPHONE_NUMBER_Table());
        addTableDefinition(build_LOCATION_Table());
        
        addTableDefinition(build_BC_LOOKUP_Table());
        addTableDefinition(build_NOISE_BYLAW_Table());
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
        
        FieldDefinition CLASSIFICATION_field = new FieldDefinition();
        CLASSIFICATION_field.setName("CLASSIFICATION");
        CLASSIFICATION_field.setTypeName("NUMERIC");
        CLASSIFICATION_field.setSize(15);
        CLASSIFICATION_field.setIsPrimaryKey(false);
        CLASSIFICATION_field.setUnique(false);
        CLASSIFICATION_field.setIsIdentity(false);
        CLASSIFICATION_field.setShouldAllowNull(true);
        table.addField(CLASSIFICATION_field);
        
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
    
    
    public static TableDefinition build_BC_LOOKUP_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_ALPINE_LOOKUP");
        
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("S_NUMBER");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(false);
        table.addField(ID_field);

        FieldDefinition BEER_CONSUMER_ID_field = new FieldDefinition();
        BEER_CONSUMER_ID_field.setName("BeerConsumer_ID");
        BEER_CONSUMER_ID_field.setTypeName("NUMERIC");
        BEER_CONSUMER_ID_field.setSize(15);
        BEER_CONSUMER_ID_field.setIsPrimaryKey(true);
        BEER_CONSUMER_ID_field.setUnique(false);
        BEER_CONSUMER_ID_field.setIsIdentity(false);
        BEER_CONSUMER_ID_field.setShouldAllowNull(false);
        table.addField(BEER_CONSUMER_ID_field);

        FieldDefinition DATA_field = new FieldDefinition();
        DATA_field.setName("DATA");
        DATA_field.setTypeName("VARCHAR");
        DATA_field.setSize(40);
        DATA_field.setShouldAllowNull(true);
        DATA_field.setIsPrimaryKey(false);
        DATA_field.setUnique(false);
        DATA_field.setIsIdentity(false);

        table.addField(DATA_field);

        return table;
    }
    
    public static TableDefinition build_BECKS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_BECKS");
    
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

        FieldDefinition BECKS_TAG_field = new FieldDefinition();
        BECKS_TAG_field.setName("TAG_ID");
        BECKS_TAG_field.setTypeName("NUMERIC");
        BECKS_TAG_field.setSize(15);
        BECKS_TAG_field.setIsPrimaryKey(false);
        BECKS_TAG_field.setUnique(false);
        BECKS_TAG_field.setIsIdentity(false);
        BECKS_TAG_field.setShouldAllowNull(true);
        BECKS_TAG_field.setForeignKeyFieldName("CMP3_BECKS_TAG.ID");
        table.addField(BECKS_TAG_field);
        
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

        return table;
    }
    
    public static TableDefinition build_BECKS_TAG_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_BECKS_TAG");
    
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(false); 
        table.addField(ID_field);

        FieldDefinition CALL_NUMBER_field = new FieldDefinition();
        CALL_NUMBER_field.setName("CALL_NUMBER");
        CALL_NUMBER_field.setTypeName("VARCHAR");
        CALL_NUMBER_field.setSize(40);
        CALL_NUMBER_field.setShouldAllowNull(true);
        CALL_NUMBER_field.setIsPrimaryKey(false);
        CALL_NUMBER_field.setUnique(false);
        CALL_NUMBER_field.setIsIdentity(false);
        table.addField(CALL_NUMBER_field);
        
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
    
    public static TableDefinition build_BEER_CONSUMER_REDSTRIPES_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CONSUMER_REDSTRIPES");
    
        FieldDefinition ALCOHOL_CONTENT_field = new FieldDefinition();
        ALCOHOL_CONTENT_field.setName("ALCOHOLCONTENT");
        ALCOHOL_CONTENT_field.setTypeName("DOUBLE PRECIS");
        ALCOHOL_CONTENT_field.setSize(15);
        ALCOHOL_CONTENT_field.setIsPrimaryKey(false);
        ALCOHOL_CONTENT_field.setUnique(false);
        ALCOHOL_CONTENT_field.setIsIdentity(false);
        ALCOHOL_CONTENT_field.setShouldAllowNull(true);
        table.addField(ALCOHOL_CONTENT_field);
        
        FieldDefinition RS_KEY_field = new FieldDefinition();
        RS_KEY_field.setName("RS_KEY");
        RS_KEY_field.setTypeName("VARCHAR");
        RS_KEY_field.setSize(10);
        RS_KEY_field.setShouldAllowNull(false);
        RS_KEY_field.setIsPrimaryKey(false);
        RS_KEY_field.setUnique(true);
        RS_KEY_field.setIsIdentity(false);
        table.addField(RS_KEY_field);
        
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

        return table;
    }
    
    public static TableDefinition build_BEER_CONSUMER_REDSTRIPE_CONTENT_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CONSUMER_REDSTRIPE_CONTENT");
    
        FieldDefinition ALCOHOL_CONTENT_field = new FieldDefinition();
        ALCOHOL_CONTENT_field.setName("ALCOHOLCONTENT");
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
        
        FieldDefinition DISCOUNT_field = new FieldDefinition();
        DISCOUNT_field.setName("DISCOUNT");
        DISCOUNT_field.setTypeName("NUMERIC");
        DISCOUNT_field.setSize(15);
        DISCOUNT_field.setIsPrimaryKey(false);
        DISCOUNT_field.setUnique(false);
        DISCOUNT_field.setIsIdentity(false);
        DISCOUNT_field.setShouldAllowNull(true);
        table.addField(DISCOUNT_field);

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
    
    public static TableDefinition build_CORONA_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_CORONA");
    
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
        
        FieldDefinition TAG_NUMBER_field = new FieldDefinition();
        TAG_NUMBER_field.setName("TAG_NUMBER");
        TAG_NUMBER_field.setTypeName("NUMERIC");
        TAG_NUMBER_field.setSize(15);
        TAG_NUMBER_field.setIsPrimaryKey(false);
        TAG_NUMBER_field.setUnique(false);
        TAG_NUMBER_field.setIsIdentity(false);
        TAG_NUMBER_field.setShouldAllowNull(true); 
        table.addField(TAG_NUMBER_field);

        FieldDefinition TAG_CODE_field = new FieldDefinition();
        TAG_CODE_field.setName("TAG_CODE");
        TAG_CODE_field.setTypeName("VARCHAR");
        TAG_CODE_field.setSize(10);
        TAG_CODE_field.setShouldAllowNull(true);
        TAG_CODE_field.setIsPrimaryKey(false);
        TAG_CODE_field.setUnique(false);
        TAG_CODE_field.setIsIdentity(false);
        table.addField(TAG_CODE_field);
        
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
    
    public static TableDefinition build_COMMITTEE_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_COMMITTEE");
    
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
        DESCRIPTION_field.setName("DESCRIP");
        DESCRIPTION_field.setTypeName("VARCHAR");
        DESCRIPTION_field.setSize(40);
        DESCRIPTION_field.setShouldAllowNull(true);
        DESCRIPTION_field.setIsPrimaryKey(false);
        DESCRIPTION_field.setUnique(false);
        DESCRIPTION_field.setIsIdentity(false);
        table.addField(DESCRIPTION_field);

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
        
        FieldDefinition IQ_field = new FieldDefinition();
        IQ_field.setName("CONSUMER_IQ");
        IQ_field.setTypeName("NUMERIC");
        IQ_field.setSize(15);
        IQ_field.setShouldAllowNull(true);
        IQ_field.setIsPrimaryKey(false);
        IQ_field.setUnique(false);
        IQ_field.setIsIdentity(false);
        table.addField(IQ_field);
        
        FieldDefinition ACCREDIDATION_field = new FieldDefinition();
        ACCREDIDATION_field.setName("ACCREDIDATION");
        ACCREDIDATION_field.setTypeName("VARCHAR");
        ACCREDIDATION_field.setSize(40);
        ACCREDIDATION_field.setShouldAllowNull(true);
        ACCREDIDATION_field.setIsPrimaryKey(false);
        ACCREDIDATION_field.setUnique(false);
        ACCREDIDATION_field.setIsIdentity(false);
        table.addField(ACCREDIDATION_field);
        
        return table;
    }
    
    public static TableDefinition build_HEINEKEN_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_HEINEKEN");
    
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
        
        FieldDefinition BOTTLED_DATE_field = new FieldDefinition();
        BOTTLED_DATE_field.setName("BOTTLED_DATE");
        BOTTLED_DATE_field.setTypeName("DATETIME");
        BOTTLED_DATE_field.setSize(23);
        BOTTLED_DATE_field.setIsPrimaryKey(false);
        BOTTLED_DATE_field.setUnique(false);
        BOTTLED_DATE_field.setIsIdentity(false);
        BOTTLED_DATE_field.setShouldAllowNull(true);
        table.addField(BOTTLED_DATE_field);
        
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

        return table;
    }
    
    public static TableDefinition build_LOCATION_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA2_LOCATION");
    
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("VARCHAR");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(false);
        table.addField(ID_field);
        
        FieldDefinition CITY_field = new FieldDefinition();
        CITY_field.setName("CITY");
        CITY_field.setTypeName("VARCHAR");
        CITY_field.setSize(40);
        CITY_field.setShouldAllowNull(false);
        CITY_field.setIsPrimaryKey(false);
        CITY_field.setUnique(false);
        CITY_field.setIsIdentity(false);
        table.addField(CITY_field);
        
        FieldDefinition COUNTRY_field = new FieldDefinition();
        COUNTRY_field.setName("COUNTRY");
        COUNTRY_field.setTypeName("VARCHAR");
        COUNTRY_field.setSize(40);
        COUNTRY_field.setShouldAllowNull(false);
        COUNTRY_field.setIsPrimaryKey(false);
        COUNTRY_field.setUnique(false);
        COUNTRY_field.setIsIdentity(false);
        table.addField(COUNTRY_field);
        
        return table;
    }
    
    public static TableDefinition build_NOISE_BYLAW_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_NOISY");
        
        FieldDefinition CITY_field = new FieldDefinition();
        CITY_field.setName("CITY");
        CITY_field.setTypeName("VARCHAR");
        CITY_field.setSize(25);
        CITY_field.setIsPrimaryKey(true);
        CITY_field.setUnique(false);
        CITY_field.setIsIdentity(false);
        CITY_field.setShouldAllowNull(false);
        table.addField(CITY_field);
        
        FieldDefinition NUMB_field = new FieldDefinition();
        NUMB_field.setName("NUMB");
        NUMB_field.setTypeName("NUMERIC");
        NUMB_field.setSize(15);
        NUMB_field.setIsPrimaryKey(true);
        NUMB_field.setUnique(false);
        NUMB_field.setIsIdentity(false);
        NUMB_field.setShouldAllowNull(false);
        table.addField(NUMB_field);
        
        FieldDefinition DESCRIP_field = new FieldDefinition();
        DESCRIP_field.setName("DESCRIP");
        DESCRIP_field.setTypeName("VARCHAR");
        DESCRIP_field.setSize(100);
        DESCRIP_field.setIsPrimaryKey(false);
        DESCRIP_field.setUnique(false);
        DESCRIP_field.setIsIdentity(false);
        DESCRIP_field.setShouldAllowNull(true);
        table.addField(DESCRIP_field);
        
        return table;
    }
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_ACCLAIMS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("EXPERT_CONSUMER_ACCLAIMS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
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
        
        table.addForeignKeyConstraint("FK_EC_ACC", "ID", "ID", "EXPERT_CONSUMER");
    
        return table;
    }
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_ACCREDIDATION_WITNESS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("EBC_ACCREDIDATION_WITNESS");

        FieldDefinition fieldCONSUMERID = new FieldDefinition();
        fieldCONSUMERID.setName("EBC_ID");
        fieldCONSUMERID.setTypeName("NUMERIC");
        fieldCONSUMERID.setSize(15);
        fieldCONSUMERID.setShouldAllowNull(false);
        fieldCONSUMERID.setIsPrimaryKey(true);
        fieldCONSUMERID.setUnique(false);
        fieldCONSUMERID.setIsIdentity(false);
        fieldCONSUMERID.setForeignKeyFieldName("EXPERT_CONSUMER.ID");
        table.addField(fieldCONSUMERID);
        
        FieldDefinition fieldWITNESSID = new FieldDefinition();
        fieldWITNESSID.setName("WITNESS_ID");
        fieldWITNESSID.setTypeName("NUMERIC");
        fieldWITNESSID.setSize(15);
        fieldWITNESSID.setShouldAllowNull(false);
        fieldWITNESSID.setIsPrimaryKey(true);
        fieldWITNESSID.setUnique(false);
        fieldWITNESSID.setIsIdentity(false);
        fieldWITNESSID.setForeignKeyFieldName("JPA_WITNESS.ID");
        table.addField(fieldWITNESSID);

        return table;
    }
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_AUDIO_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("EXPERT_CONSUMER_AUDIO");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);
    
        FieldDefinition fieldAUDIO = new FieldDefinition();
        fieldAUDIO.setName("AUDIO");
        fieldAUDIO.setTypeName("BLOB");
        fieldAUDIO.setSize(0);
        fieldAUDIO.setSubSize(0);
        fieldAUDIO.setIsPrimaryKey(false);
        fieldAUDIO.setIsIdentity(false);
        fieldAUDIO.setUnique(false);
        fieldAUDIO.setShouldAllowNull(true);
        table.addField(fieldAUDIO);
        
        table.addForeignKeyConstraint("FK_EC_AUD", "ID", "ID", "EXPERT_CONSUMER");
    
        return table;
    }
    
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_COURSES_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("EXPERT_COURSES");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("EBC_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("EXPERT_CONSUMER.ID");
        table.addField(fieldID);
    
        FieldDefinition fieldAWARDS_KEY = new FieldDefinition();
        fieldAWARDS_KEY.setName("COURSE");
        fieldAWARDS_KEY.setTypeName("VARCHAR");
        fieldAWARDS_KEY.setSize(40);
        fieldAWARDS_KEY.setShouldAllowNull(false);
        fieldAWARDS_KEY.setIsPrimaryKey(false);
        fieldAWARDS_KEY.setUnique(true);
        fieldAWARDS_KEY.setIsIdentity(false);
        table.addField(fieldAWARDS_KEY);
        
        FieldDefinition fieldAWARD_CODE = new FieldDefinition();
        fieldAWARD_CODE.setName("COURSE_GRADE");
        fieldAWARD_CODE.setTypeName("VARCHAR");
        fieldAWARD_CODE.setSize(2);
        fieldAWARD_CODE.setShouldAllowNull(false);
        fieldAWARD_CODE.setIsPrimaryKey(false);
        fieldAWARD_CODE.setUnique(false);
        fieldAWARD_CODE.setIsIdentity(false);
        table.addField(fieldAWARD_CODE);
    
        return table;
    }
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_AWARDS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("EXPERT_CONSUMER_AWARDS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
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
        
        table.addForeignKeyConstraint("FK_EC_AWD", "ID", "ID", "EXPERT_CONSUMER");
    
        return table;
    }
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_DESIGNATIONS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("EXPERT_CONSUMER_DESIGNATIONS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("EXPERT_CONSUMER_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);
    
        FieldDefinition fieldDESIGNATION = new FieldDefinition();
        fieldDESIGNATION.setName("DESIGNATION");
        fieldDESIGNATION.setTypeName("VARCHAR");
        fieldDESIGNATION.setSize(20);
        fieldDESIGNATION.setShouldAllowNull(false);
        fieldDESIGNATION.setIsPrimaryKey(false);
        fieldDESIGNATION.setUnique(false);
        fieldDESIGNATION.setIsIdentity(false);
        table.addField(fieldDESIGNATION);
        
        FieldDefinition fieldORDER_COLUMN = new FieldDefinition();
        fieldORDER_COLUMN.setName("ORDER_COLUMN");
        fieldORDER_COLUMN.setTypeName("NUMERIC");
        fieldORDER_COLUMN.setSize(15);
        fieldORDER_COLUMN.setShouldAllowNull(true);
        fieldORDER_COLUMN.setIsPrimaryKey(false);
        fieldORDER_COLUMN.setUnique(false);
        fieldORDER_COLUMN.setIsIdentity(false);
        table.addField(fieldORDER_COLUMN);
        
        table.addForeignKeyConstraint("FK_EC_DESG", "EXPERT_CONSUMER_ID", "ID", "EXPERT_CONSUMER");
        
        return table;
    }
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_CELEBRATIONS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("EXPERT_CELEBRATIONS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("EBC_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("EXPERT_CONSUMER.ID");
        table.addField(fieldID);
    
        FieldDefinition DETAILS_field = new FieldDefinition();
        DETAILS_field.setName("DETAILS");
        DETAILS_field.setTypeName("VARCHAR");
        DETAILS_field.setSize(40);
        DETAILS_field.setShouldAllowNull(true);
        DETAILS_field.setIsPrimaryKey(false);
        DETAILS_field.setUnique(false);
        DETAILS_field.setIsIdentity(false);
        table.addField(DETAILS_field);
        
        FieldDefinition BIRTH_DAY_field = new FieldDefinition();
        BIRTH_DAY_field.setName("BIRTH_DAY");
        BIRTH_DAY_field.setTypeName("NUMERIC");
        BIRTH_DAY_field.setSize(2);
        BIRTH_DAY_field.setShouldAllowNull(true);
        BIRTH_DAY_field.setIsPrimaryKey(false);
        BIRTH_DAY_field.setUnique(false);
        BIRTH_DAY_field.setIsIdentity(false);
        table.addField(BIRTH_DAY_field);
        
        FieldDefinition BIRTH_MONTH_field = new FieldDefinition();
        BIRTH_MONTH_field.setName("BIRTH_MONTH");
        BIRTH_MONTH_field.setTypeName("NUMERIC");
        BIRTH_MONTH_field.setSize(2);
        BIRTH_MONTH_field.setShouldAllowNull(true);
        BIRTH_MONTH_field.setIsPrimaryKey(false);
        BIRTH_MONTH_field.setUnique(false);
        BIRTH_MONTH_field.setIsIdentity(false);
        table.addField(BIRTH_MONTH_field);
        
        FieldDefinition BIRTH_YEAR_field = new FieldDefinition();
        BIRTH_YEAR_field.setName("BIRTH_YEAR");
        BIRTH_YEAR_field.setTypeName("NUMERIC");
        BIRTH_YEAR_field.setSize(4);
        BIRTH_YEAR_field.setShouldAllowNull(true);
        BIRTH_YEAR_field.setIsPrimaryKey(false);
        BIRTH_YEAR_field.setUnique(false);
        BIRTH_YEAR_field.setIsIdentity(false);
        table.addField(BIRTH_YEAR_field);
    
        return table;
    }
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_COMMITTEE_Table() {
        TableDefinition table = new TableDefinition();

        table.setName("JPA_CONSUMER_COMMITTEE");

        FieldDefinition fieldCONSUMERID = new FieldDefinition();
        fieldCONSUMERID.setName("CONSUMER_ID");
        fieldCONSUMERID.setTypeName("NUMERIC");
        fieldCONSUMERID.setSize(15);
        fieldCONSUMERID.setShouldAllowNull(false);
        fieldCONSUMERID.setIsPrimaryKey(true);
        fieldCONSUMERID.setUnique(false);
        fieldCONSUMERID.setIsIdentity(false);
        fieldCONSUMERID.setForeignKeyFieldName("EXPERT_CONSUMER.ID");
        table.addField(fieldCONSUMERID);
        
        FieldDefinition fieldCOMMITTEEID = new FieldDefinition();
        fieldCOMMITTEEID.setName("COMMITTEE_ID");
        fieldCOMMITTEEID.setTypeName("NUMERIC");
        fieldCOMMITTEEID.setSize(15);
        fieldCOMMITTEEID.setShouldAllowNull(false);
        fieldCOMMITTEEID.setIsPrimaryKey(true);
        fieldCOMMITTEEID.setUnique(false);
        fieldCOMMITTEEID.setIsIdentity(false);
        fieldCOMMITTEEID.setForeignKeyFieldName("JPA_COMMITTEE.ID");
        table.addField(fieldCOMMITTEEID);

        FieldDefinition fieldORDER_COLUMN = new FieldDefinition();
        fieldORDER_COLUMN.setName("ORDER_COLUMN");
        fieldORDER_COLUMN.setTypeName("NUMERIC");
        fieldORDER_COLUMN.setSize(15);
        fieldORDER_COLUMN.setShouldAllowNull(true);
        fieldORDER_COLUMN.setIsPrimaryKey(false);
        fieldORDER_COLUMN.setUnique(false);
        fieldORDER_COLUMN.setIsIdentity(false);
        table.addField(fieldORDER_COLUMN);
        
        return table;
    }
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_QUOTES_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("EXPERT_QUOTES");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("EBC_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("EXPERT_CONSUMER.ID");
        table.addField(fieldID);
    
        FieldDefinition fieldQ_DATE = new FieldDefinition();
        fieldQ_DATE.setName("Q_DATE");
        fieldQ_DATE.setTypeName("DATE");
        fieldQ_DATE.setSize(23);
        fieldQ_DATE.setShouldAllowNull(true);
        fieldQ_DATE.setIsPrimaryKey(false);
        fieldQ_DATE.setUnique(false);
        fieldQ_DATE.setIsIdentity(false);
        table.addField(fieldQ_DATE);

        FieldDefinition fieldQUOTE = new FieldDefinition();
        fieldQUOTE.setName("QUOTE");
        fieldQUOTE.setTypeName("VARCHAR");
        fieldQUOTE.setSize(50);
        fieldQUOTE.setShouldAllowNull(false);
        fieldQUOTE.setIsPrimaryKey(false);
        fieldQUOTE.setUnique(false);
        fieldQUOTE.setIsIdentity(false);
        table.addField(fieldQUOTE);
    
        return table;
    }
    
    public static TableDefinition build_EXPERT_BEER_CONSUMER_RECORDS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("EXPERT_CONSUMER_RECORDS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("EXPERT_CONSUMER_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);
        
        FieldDefinition fieldQ_DATE = new FieldDefinition();
        fieldQ_DATE.setName("RECORD_DATE");
        fieldQ_DATE.setTypeName("DATE");
        fieldQ_DATE.setSize(23);
        fieldQ_DATE.setShouldAllowNull(true);
        fieldQ_DATE.setIsPrimaryKey(false);
        fieldQ_DATE.setUnique(false);
        fieldQ_DATE.setIsIdentity(false);
        table.addField(fieldQ_DATE);
        
        FieldDefinition DESCRIP_field = new FieldDefinition();
        DESCRIP_field.setName("DESCRIPTION");
        DESCRIP_field.setTypeName("VARCHAR");
        DESCRIP_field.setSize(40);
        DESCRIP_field.setShouldAllowNull(true);
        DESCRIP_field.setIsPrimaryKey(false);
        DESCRIP_field.setUnique(false);
        DESCRIP_field.setIsIdentity(false);
        table.addField(DESCRIP_field);
        
        FieldDefinition LOCATION_ID_field = new FieldDefinition();
        LOCATION_ID_field.setName("LOCATION_ID");
        LOCATION_ID_field.setTypeName("VARCHAR");
        LOCATION_ID_field.setSize(15);
        LOCATION_ID_field.setIsPrimaryKey(true);
        LOCATION_ID_field.setUnique(false);
        LOCATION_ID_field.setIsIdentity(false);
        LOCATION_ID_field.setShouldAllowNull(false);
        LOCATION_ID_field.setForeignKeyFieldName("JPA2_LOCATION.ID");
        table.addField(LOCATION_ID_field);
        
        FieldDefinition VENUE_NAME_field = new FieldDefinition();
        VENUE_NAME_field.setName("VENUE_NAME");
        VENUE_NAME_field.setTypeName("VARCHAR");
        VENUE_NAME_field.setSize(40);
        VENUE_NAME_field.setShouldAllowNull(true);
        VENUE_NAME_field.setIsPrimaryKey(false);
        VENUE_NAME_field.setUnique(false);
        VENUE_NAME_field.setIsIdentity(false);
        table.addField(VENUE_NAME_field);
        
        FieldDefinition WITNESSES_field = new FieldDefinition();
        WITNESSES_field.setName("WITNESSES");
        WITNESSES_field.setTypeName("NUMERIC");
        WITNESSES_field.setSize(15);
        WITNESSES_field.setIsPrimaryKey(false);
        WITNESSES_field.setUnique(false);
        WITNESSES_field.setIsIdentity(false);
        WITNESSES_field.setShouldAllowNull(true);
        table.addField(WITNESSES_field);
        
        FieldDefinition BUILDER_field = new FieldDefinition();
        BUILDER_field.setName("BUILDER");
        BUILDER_field.setTypeName("VARCHAR");
        BUILDER_field.setSize(40);
        BUILDER_field.setShouldAllowNull(true);
        BUILDER_field.setIsPrimaryKey(false);
        BUILDER_field.setUnique(false);
        BUILDER_field.setIsIdentity(false);
        table.addField(BUILDER_field);
        
        FieldDefinition YEAR_BUILT_field = new FieldDefinition();
        YEAR_BUILT_field.setName("YEAR_BUILT");
        YEAR_BUILT_field.setTypeName("NUMERIC");
        YEAR_BUILT_field.setSize(4);
        YEAR_BUILT_field.setIsPrimaryKey(false);
        YEAR_BUILT_field.setUnique(false);
        YEAR_BUILT_field.setIsIdentity(false);
        YEAR_BUILT_field.setShouldAllowNull(true);
        table.addField(YEAR_BUILT_field);
        
        table.addForeignKeyConstraint("FK_EC_REC", "EXPERT_CONSUMER_ID", "ID", "EXPERT_CONSUMER");
        
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
        
        FieldDefinition IQ_field = new FieldDefinition();
        IQ_field.setName("CONSUMER_IQ");
        IQ_field.setTypeName("NUMERIC");
        IQ_field.setSize(15);
        IQ_field.setShouldAllowNull(true);
        IQ_field.setIsPrimaryKey(false);
        IQ_field.setUnique(false);
        IQ_field.setIsIdentity(false);
        table.addField(IQ_field);
        
        FieldDefinition ACCREDIDATION_field = new FieldDefinition();
        ACCREDIDATION_field.setName("ACCR_DETAILS");
        ACCREDIDATION_field.setTypeName("VARCHAR");
        ACCREDIDATION_field.setSize(40);
        ACCREDIDATION_field.setShouldAllowNull(true);
        ACCREDIDATION_field.setIsPrimaryKey(false);
        ACCREDIDATION_field.setUnique(false);
        ACCREDIDATION_field.setIsIdentity(false);
        table.addField(ACCREDIDATION_field);
        
        return table;
    }
    
    public static TableDefinition build_NOVICE_BEER_CONSUMER_ACCLAIMS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("NOVICE_CONSUMER_ACCLAIMS");
    
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
    
    public static TableDefinition build_NOVICE_BEER_CONSUMER_ACCREDIDATION_WITNESS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("NBC_ACCREDITATION_WITNESS");

        FieldDefinition fieldCONSUMERID = new FieldDefinition();
        fieldCONSUMERID.setName("NBC_ID");
        fieldCONSUMERID.setTypeName("NUMERIC");
        fieldCONSUMERID.setSize(15);
        fieldCONSUMERID.setShouldAllowNull(false);
        fieldCONSUMERID.setIsPrimaryKey(true);
        fieldCONSUMERID.setUnique(false);
        fieldCONSUMERID.setIsIdentity(false);
        fieldCONSUMERID.setForeignKeyFieldName("NOVICE_CONSUMER.ID");
        table.addField(fieldCONSUMERID);
        
        FieldDefinition fieldWITNESSID = new FieldDefinition();
        fieldWITNESSID.setName("WITNESSID");
        fieldWITNESSID.setTypeName("NUMERIC");
        fieldWITNESSID.setSize(15);
        fieldWITNESSID.setShouldAllowNull(false);
        fieldWITNESSID.setIsPrimaryKey(true);
        fieldWITNESSID.setUnique(false);
        fieldWITNESSID.setIsIdentity(false);
        fieldWITNESSID.setForeignKeyFieldName("JPA_WITNESS.ID");
        table.addField(fieldWITNESSID);

        return table;
    }
    
    public static TableDefinition build_NOVICE_BEER_CONSUMER_AWARDS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("NOVICE_CONSUMER_AWARDS");
    
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
    
    public static TableDefinition build_NOVICE_BEER_CONSUMER_COMMITTEE_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_NBC_COMMITTEE");

        FieldDefinition fieldCONSUMERID = new FieldDefinition();
        fieldCONSUMERID.setName("NBC_ID");
        fieldCONSUMERID.setTypeName("NUMERIC");
        fieldCONSUMERID.setSize(15);
        fieldCONSUMERID.setShouldAllowNull(false);
        fieldCONSUMERID.setIsPrimaryKey(true);
        fieldCONSUMERID.setUnique(false);
        fieldCONSUMERID.setIsIdentity(false);
        fieldCONSUMERID.setForeignKeyFieldName("NOVICE_CONSUMER.ID");
        table.addField(fieldCONSUMERID);
        
        FieldDefinition fieldCOMMITTEEID = new FieldDefinition();
        fieldCOMMITTEEID.setName("COM_ID");
        fieldCOMMITTEEID.setTypeName("NUMERIC");
        fieldCOMMITTEEID.setSize(15);
        fieldCOMMITTEEID.setShouldAllowNull(false);
        fieldCOMMITTEEID.setIsPrimaryKey(true);
        fieldCOMMITTEEID.setUnique(false);
        fieldCOMMITTEEID.setIsIdentity(false);
        fieldCOMMITTEEID.setForeignKeyFieldName("JPA_COMMITTEE.ID");
        table.addField(fieldCOMMITTEEID);

        FieldDefinition fieldORDER_COLUMN = new FieldDefinition();
        fieldORDER_COLUMN.setName("ORDER_COLUMN");
        fieldORDER_COLUMN.setTypeName("NUMERIC");
        fieldORDER_COLUMN.setSize(15);
        fieldORDER_COLUMN.setShouldAllowNull(true);
        fieldORDER_COLUMN.setIsPrimaryKey(false);
        fieldORDER_COLUMN.setUnique(false);
        fieldORDER_COLUMN.setIsIdentity(false);
        table.addField(fieldORDER_COLUMN);
        
        return table;
    }
    
    public static TableDefinition build_NOVICE_BEER_CONSUMER_DESIGNATIONS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("NOVICE_CONSUMER_DESIGNATIONS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("NOVICE_CONSUMER_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("NOVICE_CONSUMER.ID");
        table.addField(fieldID);
    
        FieldDefinition fieldDESIGNATION = new FieldDefinition();
        fieldDESIGNATION.setName("DESIGNATION");
        fieldDESIGNATION.setTypeName("VARCHAR");
        fieldDESIGNATION.setSize(20);
        fieldDESIGNATION.setShouldAllowNull(false);
        fieldDESIGNATION.setIsPrimaryKey(false);
        fieldDESIGNATION.setUnique(false);
        fieldDESIGNATION.setIsIdentity(false);
        table.addField(fieldDESIGNATION);
        
        FieldDefinition fieldORDER_COLUMN = new FieldDefinition();
        fieldORDER_COLUMN.setName("ORDER_COLUMN");
        fieldORDER_COLUMN.setTypeName("NUMERIC");
        fieldORDER_COLUMN.setSize(15);
        fieldORDER_COLUMN.setShouldAllowNull(true);
        fieldORDER_COLUMN.setIsPrimaryKey(false);
        fieldORDER_COLUMN.setUnique(false);
        fieldORDER_COLUMN.setIsIdentity(false);
        table.addField(fieldORDER_COLUMN);
    
        return table;
    }
    
    public static TableDefinition build_NOVICE_BEER_CONSUMER_RECORDS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("NOVICE_CONSUMER_RECORDS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("NOVICE_CONSUMER_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("NOVICE_CONSUMER.ID");
        table.addField(fieldID);
        
        FieldDefinition fieldQ_DATE = new FieldDefinition();
        fieldQ_DATE.setName("REC_DATE");
        fieldQ_DATE.setTypeName("DATE");
        fieldQ_DATE.setSize(23);
        fieldQ_DATE.setShouldAllowNull(true);
        fieldQ_DATE.setIsPrimaryKey(false);
        fieldQ_DATE.setUnique(false);
        fieldQ_DATE.setIsIdentity(false);
        table.addField(fieldQ_DATE);
        
        FieldDefinition DESCRIP_field = new FieldDefinition();
        DESCRIP_field.setName("DESCRIP");
        DESCRIP_field.setTypeName("VARCHAR");
        DESCRIP_field.setSize(40);
        DESCRIP_field.setShouldAllowNull(true);
        DESCRIP_field.setIsPrimaryKey(false);
        DESCRIP_field.setUnique(false);
        DESCRIP_field.setIsIdentity(false);
        table.addField(DESCRIP_field);
        
        FieldDefinition LOCATION_ID_field = new FieldDefinition();
        LOCATION_ID_field.setName("LOC_ID");
        LOCATION_ID_field.setTypeName("VARCHAR");
        LOCATION_ID_field.setSize(15);
        LOCATION_ID_field.setIsPrimaryKey(true);
        LOCATION_ID_field.setUnique(false);
        LOCATION_ID_field.setIsIdentity(false);
        LOCATION_ID_field.setShouldAllowNull(false);
        LOCATION_ID_field.setForeignKeyFieldName("JPA2_LOCATION.ID");
        table.addField(LOCATION_ID_field);
        
        FieldDefinition VENUE_field = new FieldDefinition();
        VENUE_field.setName("VENUE");
        VENUE_field.setTypeName("VARCHAR");
        VENUE_field.setSize(40);
        VENUE_field.setShouldAllowNull(true);
        VENUE_field.setIsPrimaryKey(false);
        VENUE_field.setUnique(false);
        VENUE_field.setIsIdentity(false);
        table.addField(VENUE_field);
        
        FieldDefinition VENUE_ATTENDANCE_field = new FieldDefinition();
        VENUE_ATTENDANCE_field.setName("VENUE_ATTENDANCE");
        VENUE_ATTENDANCE_field.setTypeName("NUMERIC");
        VENUE_ATTENDANCE_field.setSize(15);
        VENUE_ATTENDANCE_field.setIsPrimaryKey(false);
        VENUE_ATTENDANCE_field.setUnique(false);
        VENUE_ATTENDANCE_field.setIsIdentity(false);
        VENUE_ATTENDANCE_field.setShouldAllowNull(true);
        table.addField(VENUE_ATTENDANCE_field);
        
        FieldDefinition VENUE_BUILDER_field = new FieldDefinition();
        VENUE_BUILDER_field.setName("VENUE_BUILDER");
        VENUE_BUILDER_field.setTypeName("VARCHAR");
        VENUE_BUILDER_field.setSize(40);
        VENUE_BUILDER_field.setShouldAllowNull(true);
        VENUE_BUILDER_field.setIsPrimaryKey(false);
        VENUE_BUILDER_field.setUnique(false);
        VENUE_BUILDER_field.setIsIdentity(false);
        table.addField(VENUE_BUILDER_field);
        
        FieldDefinition VENUE_YEAR_BUILT_field = new FieldDefinition();
        VENUE_YEAR_BUILT_field.setName("VENUE_YEAR_BUILT");
        VENUE_YEAR_BUILT_field.setTypeName("NUMERIC");
        VENUE_YEAR_BUILT_field.setSize(4);
        VENUE_YEAR_BUILT_field.setIsPrimaryKey(false);
        VENUE_YEAR_BUILT_field.setUnique(false);
        VENUE_YEAR_BUILT_field.setIsIdentity(false);
        VENUE_YEAR_BUILT_field.setShouldAllowNull(true);
        table.addField(VENUE_YEAR_BUILT_field);
        
        return table;
    }
    
    public static TableDefinition build_OFFICIAL_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_OFFICIAL");
    
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
        DESCRIPTION_field.setName("NAME");
        DESCRIPTION_field.setTypeName("VARCHAR");
        DESCRIPTION_field.setSize(40);
        DESCRIPTION_field.setShouldAllowNull(true);
        DESCRIPTION_field.setIsPrimaryKey(false);
        DESCRIPTION_field.setUnique(false);
        DESCRIPTION_field.setIsIdentity(false);
        table.addField(DESCRIPTION_field);
        
        FieldDefinition AGE_field = new FieldDefinition();
        AGE_field.setName("AGE");
        AGE_field.setTypeName("NUMERIC");
        AGE_field.setSize(15);
        AGE_field.setIsPrimaryKey(false);
        AGE_field.setUnique(false);
        AGE_field.setIsIdentity(false);
        AGE_field.setShouldAllowNull(true);
        table.addField(AGE_field);
        
        FieldDefinition fieldSTARTDATE = new FieldDefinition();
        fieldSTARTDATE.setName("START_DATE");
        fieldSTARTDATE.setTypeName("VARCHAR");
        fieldSTARTDATE.setSize(40);
        fieldSTARTDATE.setShouldAllowNull(true);
        fieldSTARTDATE.setIsPrimaryKey(false);
        fieldSTARTDATE.setUnique(false);
        fieldSTARTDATE.setIsIdentity(false);
        table.addField(fieldSTARTDATE);
    
        FieldDefinition fieldENDDATE = new FieldDefinition();
        fieldENDDATE.setName("END_DATE");
        fieldENDDATE.setTypeName("VARCHAR");
        fieldENDDATE.setSize(40);
        fieldENDDATE.setShouldAllowNull(true);
        fieldENDDATE.setIsPrimaryKey(false);
        fieldENDDATE.setUnique(false);
        fieldENDDATE.setIsIdentity(false);
        table.addField(fieldENDDATE);
        
        FieldDefinition fieldOFFICIALENTRYID = new FieldDefinition();
        fieldOFFICIALENTRYID.setName("OFFICIAL_ENTRYID");
        fieldOFFICIALENTRYID.setTypeName("NUMERIC");
        fieldOFFICIALENTRYID.setSize(15);
        fieldOFFICIALENTRYID.setShouldAllowNull(true);
        fieldOFFICIALENTRYID.setIsPrimaryKey(false);
        fieldOFFICIALENTRYID.setUnique(false);
        fieldOFFICIALENTRYID.setIsIdentity(false);
        fieldOFFICIALENTRYID.setForeignKeyFieldName("JPA_OFFICIAL_ENTRY.ID");
        table.addField(fieldOFFICIALENTRYID);
        
        FieldDefinition EBC_ID_field = new FieldDefinition();
        EBC_ID_field.setName("FK_EBC_ID");
        EBC_ID_field.setTypeName("NUMERIC");
        EBC_ID_field.setSize(15);
        EBC_ID_field.setShouldAllowNull(true);
        EBC_ID_field.setIsPrimaryKey(false);
        EBC_ID_field.setUnique(false);
        EBC_ID_field.setIsIdentity(false);
        EBC_ID_field.setForeignKeyFieldName("EXPERT_CONSUMER.ID");
        table.addField(EBC_ID_field);
        
        FieldDefinition NBC_ID_field = new FieldDefinition();
        NBC_ID_field.setName("FK_NBC_ID");
        NBC_ID_field.setTypeName("NUMERIC");
        NBC_ID_field.setSize(15);
        NBC_ID_field.setShouldAllowNull(true);
        NBC_ID_field.setIsPrimaryKey(false);
        NBC_ID_field.setUnique(false);
        NBC_ID_field.setIsIdentity(false);
        NBC_ID_field.setForeignKeyFieldName("NOVICE_CONSUMER.ID");
        table.addField(NBC_ID_field);
        
        return table;
    }
    
    public static TableDefinition build_OFFICIAL_ENTRY_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_OFFICIAL_ENTRY");
    
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(false);
        table.addField(ID_field);
        
        return table;
    }
    
    public static TableDefinition build_OFFICIAL_COMPENSATIONTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_OFFICIAL_COMPENSATION");
    
        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(false);
        ID_field.setShouldAllowNull(false);
        ID_field.setForeignKeyFieldName("JPA_OFFICIAL.ID");
        table.addField(ID_field);

        FieldDefinition SALARY_field = new FieldDefinition();
        SALARY_field.setName("SALARY");
        SALARY_field.setTypeName("NUMERIC");
        SALARY_field.setSize(15);
        SALARY_field.setIsPrimaryKey(false);
        SALARY_field.setUnique(false);
        SALARY_field.setIsIdentity(false);
        SALARY_field.setShouldAllowNull(true);
        table.addField(SALARY_field);
        
        FieldDefinition BONUS_field = new FieldDefinition();
        BONUS_field.setName("BONUS");
        BONUS_field.setTypeName("NUMERIC");
        BONUS_field.setSize(15);
        BONUS_field.setIsPrimaryKey(false);
        BONUS_field.setUnique(false);
        BONUS_field.setIsIdentity(false);
        BONUS_field.setShouldAllowNull(true);
        table.addField(BONUS_field);
        
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
         
        FieldDefinition ISSUE_DATE_field = new FieldDefinition();
        ISSUE_DATE_field.setName("ISSUE_DATE");
        ISSUE_DATE_field.setTypeName("DATETIME");
        ISSUE_DATE_field.setSize(23);
        ISSUE_DATE_field.setIsPrimaryKey(false);
        ISSUE_DATE_field.setUnique(false);
        ISSUE_DATE_field.setIsIdentity(false);
        ISSUE_DATE_field.setShouldAllowNull(true);
        table.addField(ISSUE_DATE_field);   
        
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
    
    public static TableDefinition build_WITNESS_Table() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_WITNESS");
    
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
        DESCRIPTION_field.setName("NAME");
        DESCRIPTION_field.setTypeName("VARCHAR");
        DESCRIPTION_field.setSize(40);
        DESCRIPTION_field.setShouldAllowNull(true);
        DESCRIPTION_field.setIsPrimaryKey(false);
        DESCRIPTION_field.setUnique(false);
        DESCRIPTION_field.setIsIdentity(false);
        table.addField(DESCRIPTION_field);

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

    /**
     * Drop tables manually because constraints changed.
     */
    public void replaceTables(DatabaseSession session, SchemaManager schemaManager) {
        if (!SchemaManager.FAST_TABLE_CREATOR && !useFastTableCreatorAfterInitialCreate) {
            try {
                session.executeNonSelectingSQL("drop table EXPERT_CONSUMER_ACCLAIMS");
            } catch (Exception ignore) {}
            try {
                session.executeNonSelectingSQL("drop table EXPERT_CONSUMER_AUDIO");
            } catch (Exception ignore) {}
            try {
                session.executeNonSelectingSQL("drop table EXPERT_CONSUMER_AWARDS");
            } catch (Exception ignore) {}
            try {
                session.executeNonSelectingSQL("drop table EXPERT_CONSUMER_DESIGNATIONS");
            } catch (Exception ignore) {}
            try {
                session.executeNonSelectingSQL("drop table EXPERT_CONSUMER_RECORDS");
            } catch (Exception ignore) {}
        }
        super.replaceTables(session, schemaManager);
    }
}
