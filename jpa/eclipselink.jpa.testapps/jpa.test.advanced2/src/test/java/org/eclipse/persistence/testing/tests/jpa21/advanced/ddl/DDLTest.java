/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2012, 2024 IBM Corporation. All rights reserved.
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.spi.PersistenceUnitInfo;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Health;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Level;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.RunningStatus;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.Organizer;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.PUInfoInvocationHandler;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.Race;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.Responsibility;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.Runner;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.RunnerInfo;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.RunnerStatus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DDLTest extends JUnitTestCase {
    public DDLTest() {}

    public DDLTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    /**
     * Return the the persistence unit name for this test suite.
     */
    @Override
    public String getPersistenceUnitName() {
        return "advanced2x-ddl-template";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DDLTest");

        // Test database specific tests, create scripts then connect and use.
        suite.addTest(new DDLTest("testPersistenceGenerateSchemaUseConnection"));
        suite.addTest(new DDLTest("testPersistenceGenerateSchemaNoConnection_Derby"));
        suite.addTest(new DDLTest("testPersistenceGenerateSchemaNoConnection_MySQL"));
        suite.addTest(new DDLTest("testPersistenceGenerateSchemaNoConnection_Oracle"));

        suite.addTest(new DDLTest("testPersistenceGenerateSchemaDropOnlyScript"));
        suite.addTest(new DDLTest("testPersistenceGenerateSchemaUsingProvidedWriters"));
        //suite.addTest(new DDLTest("testRootTargetScriptFileName"));
        suite.addTest(new DDLTest("testIllegalArgumentExceptionWithNoScriptTargetProvided"));

        suite.addTest(new DDLTest("testContainerGenerateSchema"));

        return suite;
    }

    /**
     * Test the generate schema feature from the Persistence API.
     */
    public void testPersistenceGenerateSchemaUseConnection() {
        String GENERATE_SCHEMA_NO_CONNECTION_DROP_TARGET = "jpa21-generate-schema-use-connection-drop.jdbc";
        String GENERATE_SCHEMA_NO_CONNECTION_CREATE_TARGET = "jpa21-generate-schema-use-connection-create.jdbc";
        String GENERATE_SCHEMA_NO_CONNECTION_SESSION_NAME = "generate-schema-use-conn-session";

        // Get database properties will pick up test.properties database connection details.
        Map<String, Object> properties = new HashMap<>(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
        properties.put(PersistenceUnitProperties.SESSION_NAME, GENERATE_SCHEMA_NO_CONNECTION_SESSION_NAME);
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, "true");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, GENERATE_SCHEMA_NO_CONNECTION_DROP_TARGET);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, GENERATE_SCHEMA_NO_CONNECTION_CREATE_TARGET);

        Persistence.generateSchema(getPersistenceUnitName(), properties);

        // Now create an entity manager and build some objects for this PU using
        // the same session name. Create the schema on the database with the
        // target scripts built previously.
        testPersistenceGenerateSchemaOnDatabase(GENERATE_SCHEMA_NO_CONNECTION_CREATE_TARGET, GENERATE_SCHEMA_NO_CONNECTION_DROP_TARGET, GENERATE_SCHEMA_NO_CONNECTION_SESSION_NAME);
    }

    /**
     * Test the generate schema feature from the Persistence API on Derby.
     */
    public void testPersistenceGenerateSchemaNoConnection_Derby() {
        if (getPlatform().isDerby()) {
            testPersistenceGenerateSchemaNoConnection("derby", null, null);
        }
    }

    /**
     * Test the generate schema feature from the Persistence API on MySQL.
     */
    public void testPersistenceGenerateSchemaNoConnection_MySQL() {
        if (getPlatform().isMySQL()) {
            testPersistenceGenerateSchemaNoConnection("MySQL", "5", "5");
        }
    }

    /**
     * Test the generate schema feature from the Persistence API on Oracle.
     */
    public void testPersistenceGenerateSchemaNoConnection_Oracle() {
        if (getPlatform().isOracle()) {
            testPersistenceGenerateSchemaNoConnection("Oracle", "11", "1");
        }
    }

    /**
     * Test the generate schema feature from the Persistence API.
     */
    public void testPersistenceGenerateSchemaDropOnlyScript() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.SESSION_NAME, "generate-schema-no-conn-session-drop-only");
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, true);
        properties.put(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME, "MySQL");
        properties.put(PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION, "5");
        properties.put(PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION, "5");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, "jpa21-generate-schema-no-connection-drop-only.jdbc");

        Persistence.generateSchema(getPersistenceUnitName(), properties);
    }

    /**
     * Test the generate schema feature from the Persistence API.
     */
    public void testPersistenceGenerateSchemaUsingProvidedWriters() {
        // Get database properties will pick up test.properties database connection details.
        Map<String, Object> properties = new HashMap<>(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
        properties.put(PersistenceUnitProperties.SESSION_NAME, "generate-schema-using-provided-writers");
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, "true");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);

        try {
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, new FileWriter("jpa21-generate-schema-using-drop-writer.jdbc"));
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, new FileWriter("jpa21-generate-schema-using-create-writer.jdbc"));

            Persistence.generateSchema(getPersistenceUnitName(), properties);
        } catch (IOException e) {
            fail("Error occurred: " + e);
        }
    }

    public void testRootTargetScriptFileName() {
        // This test is not called. It can be tested manually by uncommenting
        // it out in the suite setup.
        // Get database properties will pick up test.properties database connection details.
        Map<String, Object> properties = new HashMap<>(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
        properties.put(PersistenceUnitProperties.SESSION_NAME, "testRootTargetScriptFileName");
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, "true");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, "/temp-generate-schema-drop.jdbc");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, "/temp-generate-schema-create.jdbc");

        Persistence.generateSchema(getPersistenceUnitName(), properties);
    }

    public void testIllegalArgumentExceptionWithNoScriptTargetProvided() {
        // Get database properties will pick up test.properties database connection details.
        Map<String, Object> properties = new HashMap<>(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
        properties.put(PersistenceUnitProperties.SESSION_NAME, "testIllegalArgumentExceptionWithNoScriptTargetsProvided");
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, "true");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_ACTION);

        try {
            Persistence.generateSchema(getPersistenceUnitName(), properties);
        } catch (PersistenceException exception) {
            assertTrue("IllegalArgumentException not thrown", exception.getCause() instanceof IllegalArgumentException);
            return;
        }

        fail("Illegal Argument Exception was not thrown when a target script name not provided.");
    }

    /**
     * Test the container PersistenceProvider.generateSchema(PersistenceUnitInfo
     * info, Map map) method from the Persistence API.
     */
    public void testContainerGenerateSchema() {
        String CONTAINER_GENERATE_SCHEMA_DROP_TARGET = "jpa21-container-generate-schema-drop.jdbc";
        String CONTAINER_GENERATE_SCHEMA_CREATE_TARGET = "jpa21-container-generate-schema-create.jdbc";
        String CONTAINER_GENERATE_SCHEMA_SESSION_NAME = "container-generate-schema-session";

        // Get database properties will pick up test.properties database connection details.
        Map<String, Object> properties = new HashMap<>(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
        properties.put(PersistenceUnitProperties.SESSION_NAME, CONTAINER_GENERATE_SCHEMA_SESSION_NAME);
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, "true");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, CONTAINER_GENERATE_SCHEMA_DROP_TARGET);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, CONTAINER_GENERATE_SCHEMA_CREATE_TARGET);

        // When a container calls PersistenceProvider.generateSchema(PersistenceUnitInfo info, Map map),
        // the container passes its own PUInfo Implementation. To avoid having to implement our own PUInfo,
        // a proxy object is created that invokes SEPersistenceUnitInfo's methods in the background.
        PersistenceProvider provider = new PersistenceProvider();
        JPAInitializer initializer = provider.getInitializer(getPersistenceUnitName(), properties);
        SEPersistenceUnitInfo sePUImpl = initializer.findPersistenceUnitInfo(getPersistenceUnitName(), properties);
        PersistenceUnitInfo puinfo = (PersistenceUnitInfo) Proxy.newProxyInstance(SEPersistenceUnitInfo.class.getClassLoader(), new Class<?>[] { PersistenceUnitInfo.class }, new PUInfoInvocationHandler(sePUImpl));
        provider.generateSchema(puinfo, properties);

        // Now create an entity manager and build some objects for this PU using
        // the same session name. Create the schema on the database with the
        // target scripts built previously.
        testPersistenceGenerateSchemaOnDatabase(CONTAINER_GENERATE_SCHEMA_CREATE_TARGET, CONTAINER_GENERATE_SCHEMA_DROP_TARGET, CONTAINER_GENERATE_SCHEMA_SESSION_NAME);
    }

    /**
     * Test the generate schema feature from the Persistence API.
     * <p>
     * This test will then further use the PU and make sure the connection
     * occurs and we can persist objects.
     * <p>
     * All properties are set in code.
     */
    private void testPersistenceGenerateSchemaNoConnection(String platform, String majorVersion, String minorVersion) {
        // Get platform call will deploy our app and be stored in our
        // testing framework. Need to clear it for this test.
        closeEntityManagerFactory();

        String GENERATE_SCHEMA_NO_CONNECTION_DROP_TARGET = "jpa21-generate-schema-no-connection-drop-"+platform+".jdbc";
        String GENERATE_SCHEMA_NO_CONNECTION_CREATE_TARGET = "jpa21-generate-schema-no-connection-create-"+platform+".jdbc";
        String GENERATE_SCHEMA_NO_CONNECTION_SESSION_NAME = "generate-schema-no-conn-session-" + platform;

        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.SESSION_NAME, GENERATE_SCHEMA_NO_CONNECTION_SESSION_NAME);
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, true);
        properties.put(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME, platform);

        if (majorVersion != null) {
            properties.put(PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION, majorVersion);
        }

        if (minorVersion != null) {
            properties.put(PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION, minorVersion);
        }

        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, GENERATE_SCHEMA_NO_CONNECTION_DROP_TARGET);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, GENERATE_SCHEMA_NO_CONNECTION_CREATE_TARGET);

        Persistence.generateSchema(getPersistenceUnitName(), properties);

        // Now create an entity manager and build some objects for this PU using
        // the same session name. Create the schema on the database with the
        // target scripts built previously.
        testPersistenceGenerateSchemaOnDatabase(GENERATE_SCHEMA_NO_CONNECTION_CREATE_TARGET, GENERATE_SCHEMA_NO_CONNECTION_DROP_TARGET, GENERATE_SCHEMA_NO_CONNECTION_SESSION_NAME);
    }

    private void testPersistenceGenerateSchemaOnDatabase(String createSource, String dropSource, String sessionName) {
        // Now create an entity manager and build some objects for this PU using
        // the same session name. Create the schema on the database with the
        // target scripts built previously.
        EntityManager em = null;

        try {
            // Get database properties will pick up test.properties database connection details.
            Map<String, Object> properties = new HashMap<>(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
            properties.put(PersistenceUnitProperties.SESSION_NAME, sessionName);
            properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, true);
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_SOURCE, PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPT_SOURCE);
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_SCRIPT_SOURCE, new FileReader(createSource));
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_DROP_SOURCE, PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPT_SOURCE);
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_DROP_SCRIPT_SOURCE, new FileReader(dropSource));

            em = createEntityManager(properties);

            // If on the server, need to trigger the DDL through Persistence
            // API, createEntityManager call above will not do it.
            if (isOnServer()) {
                Persistence.generateSchema(this.getPersistenceUnitName(), properties);
            }

            beginTransaction(em);

            Runner runner = new Runner();
            runner.setAge(53);
            runner.setIsFemale();
            runner.setFirstName("Doris");
            runner.setLastName("Day");
            runner.addPersonalBest("10 KM", "47:34");
            runner.addPersonalBest("5", "26:41");
            runner.addAccomplishment("Ran 100KM without stopping", new Date(System.currentTimeMillis()));
            RunnerInfo runnerInfo = new RunnerInfo();
            runnerInfo.setHealth(Health.H);
            runnerInfo.setLevel(Level.A);
            RunnerStatus runnerStatus = new RunnerStatus();
            runnerStatus.setRunningStatus(RunningStatus.D);
            runnerInfo.setStatus(runnerStatus);
            runner.setInfo(runnerInfo);

            Race race = new Race();
            race.setName("The Ultimate Marathon");
            race.addRunner(runner);

            Organizer organizer = new Organizer();
            organizer.setName("Joe Organ");
            organizer.setRace(race);

            Responsibility responsibility = new Responsibility();
            responsibility.setUniqueIdentifier(System.currentTimeMillis());
            responsibility.setDescription("Raise funds");

            race.addOrganizer(organizer, responsibility);

            em.persist(race);
            em.persist(organizer);
            em.persist(runner);
            commitTransaction(em);

            // Clear the cache
            em.clear();
            clearCache();

            Runner runnerRefreshed = em.find(Runner.class, runner.getId());
            assertEquals("The age conversion did not work.", 52, (int) runnerRefreshed.getAge());
            assertEquals("The embeddable health conversion did not work.", runnerRefreshed.getInfo().getHealth(), Health.HEALTHY);
            assertEquals("The embeddable level conversion did not work.", runnerRefreshed.getInfo().getLevel(), Level.AMATEUR);
            assertEquals("The nested embeddable running status conversion did not work.", runnerRefreshed.getInfo().getStatus().getRunningStatus(), RunningStatus.DOWN_TIME);
            assertEquals("The number of personal bests for this runner is incorrect.", 2, runnerRefreshed.getPersonalBests().size());
            assertTrue("Distance (map key) conversion did not work.", runnerRefreshed.getPersonalBests().containsKey("10K"));
            assertTrue("Distance (map key) conversion did not work.", runnerRefreshed.getPersonalBests().containsKey("5K"));
            assertTrue("Time (map value) conversion did not work.", runnerRefreshed.getPersonalBests().containsValue("47:34.0"));
            assertTrue("Time (map value) conversion did not work.", runnerRefreshed.getPersonalBests().containsValue("26:41.0"));

            Race raceRefreshed = em.find(Race.class, race.getId());
            Map<Responsibility, Organizer> organizers = raceRefreshed.getOrganizers();
            assertFalse("No race organizers returned.", organizers.isEmpty());
            assertEquals("More than one race organizer returned.", 1, organizers.size());

            Responsibility resp = organizers.keySet().iterator().next();
            assertEquals("Responsibility was not uppercased by the converter", "RAISE FUNDS", resp.getDescription());

            for (String accomplishment : runnerRefreshed.getAccomplishments().keySet()) {
                assertTrue("Accomplishment (map key) conversion did not work.", accomplishment.endsWith("!!!"));
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }

            throw e;
        } catch (FileNotFoundException e) {
            fail("Error loading source script file: " + e);
        } finally {
            closeEntityManager(em);
        }
    }
}
