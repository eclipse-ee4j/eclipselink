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
 *     02/01/2012-2.4 Chris Delahunt
 *       - 368365: add DDL support to alter existing tables to add missing fields
 ******************************************************************************/   
package org.eclipse.persistence.testing.tests.jpa.ddlgeneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.tools.schemaframework.DefaultTableGenerator;
import org.eclipse.persistence.tools.schemaframework.TableCreator;

public class DDLGenerationExtendTablesJUnitTestSuite extends
        DDLGenerationJUnitTestSuite {

    public DDLGenerationExtendTablesJUnitTestSuite() {
    }

    public DDLGenerationExtendTablesJUnitTestSuite(String name) {
        super(name);
        setPuName(DDL_PU);
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        // Create the EM.  This might unnecessarily drop and create the tables
        EntityManager em = createEntityManager(DDL_PU);
        DatabaseSessionImpl session = this.getDatabaseSession(DDL_PU);
        
        TableCreator defaultTableCreator = new DefaultTableGenerator(session.getProject(), true).generateDefaultTableCreator();
        defaultTableCreator.setIgnoreDatabaseException(true);
        //drop the tables using the current project
        defaultTableCreator.dropTables(session);
        
        //check that the tables do not exist:
        Exception exception = null;
        try {
            em.createQuery("Select c from Course c").getResultList();
        } catch (DatabaseException caught){
            //we expect an exception since the table should not exist, but do not know what exception the database might throw.  
            exception = caught;
        }
        this.assertNotNull("setup failed because a query on a drop table did not throw an exception.", exception);

        //create some empty tables so that we can test they are altered correctly later.
        TableCreator tbCreator = new EmptyTableCreator();
        tbCreator.createTables(session);

        //refresh the metadata with the create-or-alter table to create the remaining tables and alter the ones created above.
        //testing later on just verifies that this worked correctly.
        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
        //create-or-extend only works on the database
        properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
        //this causes DDL generation to occur on refreshMetadata rather than wait until an em is obtained
        properties.put(PersistenceUnitProperties.DEPLOY_ON_STARTUP, "true");
        // JEE requires a transaction to keep the em open.
        beginTransaction(em);
        JpaHelper.getEntityManagerFactory(em).refreshMetadata(properties);
        commitTransaction(em);
        closeEntityManager(em);
        clearCache(DDL_PU);

    }
    
    public static Test suite() {
        if (System.getProperty("server.platform") != null) {
            // "MulitPU-1" is the persistence unit name used on servers
            DDL_PU = "MulitPU-1";
        }
        TestSuite suite = new TestSuite();
        suite.setName("DDLCreateAndAlterTablesTestSuite");
       
        // 'create-or-extend-tables' cannot be tested properly if 'toggle-fast-table-creator' is set as true.
        // DELETE FROM TABLE is executed instead of DROP TABLE / CREATE TABLE when 'toggle-fast-table-creator' is set as true. 
        // Skip this test suite if 'toggle-fast-table-creator' is set as true. (bug 372179)
        if (Boolean.getBoolean("eclipselink.test.toggle-fast-table-creator")) {
            return suite;
        }
        
        suite.addTest(new DDLGenerationExtendTablesJUnitTestSuite("testSetup"));
        List<String> tests = new ArrayList<String>();
        tests.add("testDDLPkConstraintErrorIncludingRelationTableColumnName");
        tests.add("testOptionalPrimaryKeyJoinColumnRelationship");
        tests.add("testPrimaryKeyJoinColumns");
        tests.add("testDDLEmbeddableMapKey");
        tests.add("testDDLAttributeOverrides");
        tests.add("testDDLAttributeOverridesOnElementCollection");
        tests.add("testDDLUniqueKeysAsJoinColumns");
        tests.add("testDDLUniqueConstraintsByAnnotations");
        tests.add("testDDLUniqueConstraintsByXML");
        tests.add("testDDLSubclassEmbeddedIdPkColumnsInJoinedStrategy");
        tests.add("testBug241308");
        tests.add("testDDLUnidirectionalOneToMany");
        tests.add("testCascadeMergeOnManagedEntityWithOrderedList");
        tests.add("testDirectCollectionMapping");
        tests.add("testAggregateCollectionMapping");
        tests.add("testOneToManyMapping");
        tests.add("testUnidirectionalOneToManyMapping");
        tests.add("testManyToManyMapping");
        tests.add("testManyToManyWithMultipleJoinColumns");
        tests.add("testEmbeddedManyToMany");
        tests.add("testEmbeddedOneToOne");
        tests.add("testAssociationOverrideToEmbeddedManyToMany");
        tests.add("testDeleteObjectWithEmbeddedManyToMany");
        tests.add("testLAZYLOBWithEmbeddedId");
        tests.add("testElementMapOnEmbedded");
        if (! JUnitTestCase.isJPA10()) {
            tests.add("testCreateMafiaFamily707");
            tests.add("testCreateMafiaFamily007");
            tests.add("testValidateMafiaFamily707");
            tests.add("testValidateMafiaFamily007");
        }
        tests.add("testSimpleSelectFoo");
        Collections.sort(tests);
        for (String test : tests) {
            suite.addTest(new DDLGenerationExtendTablesJUnitTestSuite(test));
        }
        return suite;
    }
    public class EmptyTableCreator extends TableCreator {
        public EmptyTableCreator() {
            org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();
    
            // SECTION: TABLE
            tabledefinition.setName("Countries");
    
            // SECTION: ID FIELD
            org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
            field.setName("iso_code");
            field.setType(String.class);
            field.setSize(50);
            field.setShouldAllowNull(false);
            field.setIsPrimaryKey(true);
            field.setUnique(false);
            field.setIsIdentity(true);
            tabledefinition.addField(field);
            
            addTableDefinition(tabledefinition);
            
            // SECTION: TABLE
            tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();
            tabledefinition.setName("DDL_EMP");
    
            // SECTION: ID FIELD
            field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
            field.setName("ID");
            field.setType(Integer.class);
            field.setShouldAllowNull(false);
            field.setIsPrimaryKey(true);
            field.setUnique(false);
            field.setIsIdentity(true);
            tabledefinition.addField(field);
            
            addTableDefinition(tabledefinition);
            
            // SECTION: TABLE
            tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();
            tabledefinition.setName("DDL_COURSE");
    
            // SECTION: ID FIELD
            field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
            field.setName("ID");
            field.setType(Long.class);
            field.setShouldAllowNull(false);
            field.setIsPrimaryKey(true);
            field.setUnique(false);
            field.setIsIdentity(true);
            tabledefinition.addField(field);
            
            addTableDefinition(tabledefinition);
            
         // SECTION: TABLE
            tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();
            tabledefinition.setName("Foos");
    
            // SECTION: ID FIELD
            field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
            field.setName("contact_id");
            field.setType(Integer.class);
            field.setShouldAllowNull(false);
            field.setIsPrimaryKey(true);
            field.setUnique(false);
            field.setIsIdentity(true);
            tabledefinition.addField(field);

            field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
            field.setName("ordinal_nbr");
            field.setType(Integer.class);
            field.setShouldAllowNull(false);
            field.setIsPrimaryKey(true);
            field.setUnique(false);
            field.setIsIdentity(true);
            tabledefinition.addField(field);
            
            addTableDefinition(tabledefinition);
    
         // SECTION: TABLE
            tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();
            tabledefinition.setName("DDL_CKENTA");
    
            // SECTION: ID FIELDS
            field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
            field.setName("SEQ");
            field.setType(Integer.class);
            field.setShouldAllowNull(false);
            field.setIsPrimaryKey(true);
            field.setUnique(false);
            field.setIsIdentity(true);
            tabledefinition.addField(field);
            
            field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
            field.setName("F_NAME");
            field.setType(String.class);
            field.setSize(64);
            field.setShouldAllowNull(false);
            field.setIsPrimaryKey(true);
            field.setUnique(false);
            field.setIsIdentity(true);
            tabledefinition.addField(field);
            
            field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
            field.setName("L_NAME");
            field.setType(String.class);
            field.setSize(64);
            field.setShouldAllowNull(false);
            field.setIsPrimaryKey(true);
            field.setUnique(false);
            field.setIsIdentity(true);
            tabledefinition.addField(field);

            addTableDefinition(tabledefinition);
        }
    }

}
