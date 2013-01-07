/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/22/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
 *     12/24/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 *     01/08/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.sessions.server.ServerSession;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa21.advanced.ddl.Sprinter;

import junit.framework.TestSuite;
import junit.framework.Test;

public class DDLTestSuite extends JUnitTestCase {
    protected static final String GENERATE_SCHEMA_USE_CONNECTION_PU = "generate-schema-use-connection";
    protected static final String GENERATE_SCHEMA_NO_CONNECTION_PU = "generate-schema-no-connection";
    
    public DDLTestSuite() {}
    
    public DDLTestSuite(String name) {
        super(name);
    }
    
    @Override
    public void setUp () {
        super.setUp();
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DDLTestSuite");

        suite.addTest(ForeignKeyTestSuite.suite());
        suite.addTest(IndexTestSuite.suite());
        
        suite.addTest(new DDLTestSuite("testPersistenceGenerateSchemaUseConnection"));
        suite.addTest(new DDLTestSuite("testPersistenceGenerateSchemaNoConnection"));
        
        return suite;
    }
    
    /**
     * Test the generate schema feature from the Persistence API. 
     */
    public void testPersistenceGenerateSchemaUseConnection() {
        Map properties = JUnitTestCaseHelper.getDatabaseProperties(GENERATE_SCHEMA_USE_CONNECTION_PU);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_ACTION, PersistenceUnitProperties.SCHEMA_DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_TARGET, PersistenceUnitProperties.SCHEMA_SCRIPTS_GENERATION);
        properties.put(PersistenceUnitProperties.SCHEMA_DROP_SCRIPT_TARGET, "generate-schema-use-connection-drop.jdbc");
        properties.put(PersistenceUnitProperties.SCHEMA_CREATE_SCRIPT_TARGET, "generate-schema-use-connection-create.jdbc");
         
        Persistence.generateSchema(GENERATE_SCHEMA_USE_CONNECTION_PU, properties);
    }
    
    /**
     * Test the generate schema feature from the Persistence API. 
     */
    public void testPersistenceGenerateSchemaNoConnection() {
        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.SESSION_NAME, "generate-schema-no-conn-session");
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, true);
        properties.put(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME, "MySQL");
        properties.put(PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION, "5");
        properties.put(PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION, "5");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_ACTION, PersistenceUnitProperties.SCHEMA_DROP_AND_CREATE);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_TARGET, PersistenceUnitProperties.SCHEMA_SCRIPTS_GENERATION);
        properties.put(PersistenceUnitProperties.SCHEMA_DROP_SCRIPT_TARGET, "generate-schema-no-connection-drop.jdbc");
        properties.put(PersistenceUnitProperties.SCHEMA_CREATE_SCRIPT_TARGET, "generate-schema-no-connection-create.jdbc");
         
        Persistence.generateSchema(GENERATE_SCHEMA_NO_CONNECTION_PU, properties);
    }
}
