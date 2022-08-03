/*
 * Copyright (c) 2012, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2012, 2022 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
//     12/24/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     01/08/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     01/11/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     01/16/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     01/24/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     02/02/2015-2.6.0 Dalia Abo Sheasha
//       - 458462: generateSchema throws a ClassCastException within a container
package org.eclipse.persistence.testing.tests.jpa21.advanced.ddl;

import jakarta.persistence.Persistence;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;

import java.util.HashMap;
import java.util.Map;

public class DDLSchemaTest extends JUnitTestCase {
    public DDLSchemaTest() {}

    public DDLSchemaTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    /**
     * Return the the persistence unit name for this test suite.
     */
    @Override
    public String getPersistenceUnitName() {
        return "ddl-schema-template";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DDLSchemaTest");

        // These test schema manipulations during DDL generation. Their output
        // files are manually inspected. Tests ensure there are no errors
        // during the generation.
        suite.addTest(new DDLSchemaTest("testDatabaseSchemaGenerationCreateOnly"));
        suite.addTest(new DDLSchemaTest("testDatabaseSchemaGenerationDropOnly"));
        suite.addTest(new DDLSchemaTest("testDatabaseSchemaGenerationDropAndCreate"));
        //suite.addTest(new DDLTest("testDatabaseSchemaGenerationURLTargets"));

        return suite;
    }

    public void testDatabaseSchemaGenerationCreateOnly() {
        // Get database properties will pick up test.properties database connection details.
        Map<String, Object> properties = new HashMap<>(getEntityManagerFactory().getProperties());
        properties.put(PersistenceUnitProperties.SESSION_NAME, "ddl-schema-create-only-session");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_DATABASE_SCHEMAS, "true");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, "jpa21-ddl-schema-create-only-create.jdbc");

        try {
            Persistence.generateSchema("ddl-schema-template", properties);
        } catch (Exception exception) {
            fail("Exception caught when generating schema: " + exception.getMessage());
        }
    }

    public void testDatabaseSchemaGenerationDropOnly() {
        // Get database properties will pick up test.properties database connection details.
        Map<String, Object> properties = new HashMap<>(getEntityManagerFactory().getProperties());
        properties.put(PersistenceUnitProperties.SESSION_NAME, "ddl-schema-drop-only-session");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_DATABASE_SCHEMAS, "true");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, "jpa21-ddl-schema-drop-only-drop.jdbc");

        try {
            Persistence.generateSchema("ddl-schema-template", properties);
        } catch (Exception exception) {
            exception.printStackTrace();
            fail("Exception caught when generating schema: " + exception.getMessage());
        }
    }

    public void testDatabaseSchemaGenerationDropAndCreate() {
        // Get database properties will pick up test.properties database connection details.
        Map<String, Object> properties = new HashMap<>(getEntityManagerFactory().getProperties());
        properties.put(PersistenceUnitProperties.SESSION_NAME, "ddl-schema-drop-and-create-session");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_DATABASE_SCHEMAS, "true");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, "jpa21-ddl-schema-drop-and-create-drop.jdbc");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, "jpa21-ddl-schema-drop-and-create-create.jdbc");

        try {
            Persistence.generateSchema("ddl-schema-template", properties);
        } catch (Exception exception) {
            fail("Exception caught when generating schema: " + exception.getMessage());
        }
    }

    public void testDatabaseSchemaGenerationURLTargets() {
        // This test is not called. It can be tested manually by uncommenting
        // it out in the suite setup.
        // Get database properties will pick up test.properties database connection details.
        Map<String, Object> properties = new HashMap<>(getEntityManagerFactory().getProperties());
        properties.put(PersistenceUnitProperties.SESSION_NAME, "ddl-schema-url-target-session");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, "file:///jpa21-ddl-schema-url-target-create.jdbc");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, "file:///jpa21-ddl-schema-url-target-drop.jdbc");

        try {
            Persistence.generateSchema("ddl-schema-template", properties);
        } catch (Exception exception) {
            fail("Exception caught when generating schema: " + exception.getMessage());
        }
    }
}
