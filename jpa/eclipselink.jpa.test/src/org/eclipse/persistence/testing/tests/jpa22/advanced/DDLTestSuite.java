/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
package org.eclipse.persistence.testing.tests.jpa22.advanced;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.PersistenceProvider;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.ddl.Organizer;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.ddl.PUInfoInvocationHandler;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.ddl.Race;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.ddl.Responsibility;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.ddl.Runner;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.ddl.RunnerInfo;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.ddl.RunnerStatus;
import org.eclipse.persistence.testing.models.jpa22.advanced.enums.Health;
import org.eclipse.persistence.testing.models.jpa22.advanced.enums.Level;
import org.eclipse.persistence.testing.models.jpa22.advanced.enums.RunningStatus;

import junit.framework.TestSuite;
import junit.framework.Test;

public class DDLTestSuite extends JUnitTestCase {
    public DDLTestSuite() {}

    public DDLTestSuite(String name) {
        super(name);
        setPuName("MulitPU-5");
    }

    /**
     * Return the the persistence unit name for this test suite.
     */
    @Override
    public String getPersistenceUnitName() {
        return "MulitPU-5";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DDLTestSuite");

        // Test database specific tests, create scripts then connect and use.
        suite.addTest(new DDLTestSuite("testPersistenceGenerateSchemaUseConnection"));
        suite.addTest(new DDLTestSuite("testPersistenceGenerateSchemaNoConnection_Derby"));
        suite.addTest(new DDLTestSuite("testPersistenceGenerateSchemaNoConnection_MySQL"));
        suite.addTest(new DDLTestSuite("testPersistenceGenerateSchemaNoConnection_Oracle"));

        suite.addTest(new DDLTestSuite("testPersistenceGenerateSchemaDropOnlyScript"));
        suite.addTest(new DDLTestSuite("testPersistenceGenerateSchemaUsingProvidedWriters"));
        //suite.addTest(new DDLTestSuite("testRootTargetScriptFileName"));
        suite.addTest(new DDLTestSuite("testIllegalArgumentExceptionWithNoScriptTargetProvided"));

        // These test schema manipulations during DDL generation. Their output
        // files are manually inspected. Tests ensure there are no errors
        // during the generation.
        suite.addTest(new DDLTestSuite("testDatabaseSchemaGenerationCreateOnly"));
        suite.addTest(new DDLTestSuite("testDatabaseSchemaGenerationDropOnly"));
        suite.addTest(new DDLTestSuite("testDatabaseSchemaGenerationDropAndCreate"));
        //suite.addTest(new DDLTestSuite("testDatabaseSchemaGenerationURLTargets"));

        suite.addTest(new DDLTestSuite("testContainerGenerateSchema"));

        return suite;
    }

    public void testPersistenceGenerateSchemaOnDatabase(String createSource, String dropSource, String sessionName) {
        // Now create an entity manager and build some objects for this PU using
        // the same session name. Create the schema on the database with the
        // target scripts built previously.
        EntityManager em = null;

        try {
            Map properties = new HashMap();
            // Get database properties will pick up test.properties database connection details.
            properties.putAll(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
            properties.put(PersistenceUnitProperties.SESSION_NAME, sessionName);
            properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, true);
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_DATABASE_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_SOURCE, PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPT_SOURCE);
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_SCRIPT_SOURCE, new FileReader(new File(createSource)));
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_DROP_SOURCE, PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPT_SOURCE);
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_DROP_SCRIPT_SOURCE, new FileReader(new File(dropSource)));

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
            responsibility.setUniqueIdentifier(new Long(System.currentTimeMillis()));
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
            assertTrue("The age conversion did not work.", runnerRefreshed.getAge() == 52);
            assertTrue("The embeddable health conversion did not work.", runnerRefreshed.getInfo().getHealth().equals(Health.HEALTHY));
            assertTrue("The embeddable level conversion did not work.", runnerRefreshed.getInfo().getLevel().equals(Level.AMATEUR));
            assertTrue("The nested embeddable running status conversion did not work.", runnerRefreshed.getInfo().getStatus().getRunningStatus().equals(RunningStatus.DOWN_TIME));
            assertTrue("The number of personal bests for this runner is incorrect.", runnerRefreshed.getPersonalBests().size() == 2);
            assertTrue("Distance (map key) conversion did not work.", runnerRefreshed.getPersonalBests().keySet().contains("10K"));
            assertTrue("Distance (map key) conversion did not work.", runnerRefreshed.getPersonalBests().keySet().contains("5K"));
            assertTrue("Time (map value) conversion did not work.", runnerRefreshed.getPersonalBests().values().contains("47:34.0"));
            assertTrue("Time (map value) conversion did not work.", runnerRefreshed.getPersonalBests().values().contains("26:41.0"));

            Race raceRefreshed = em.find(Race.class, race.getId());
            Map<Responsibility, Organizer> organizers = raceRefreshed.getOrganizers();
            assertFalse("No race organizers returned.", organizers.isEmpty());
            assertTrue("More than one race organizer returned.", organizers.size() == 1);

            Responsibility resp = organizers.keySet().iterator().next();
            assertTrue("Responsibility was not uppercased by the converter", resp.getDescription().equals("RAISE FUNDS"));

            for (String accomplishment : runnerRefreshed.getAccomplishments().keySet()) {
                assertTrue("Accomplishment (map key) conversion did not work.", accomplishment.endsWith("!!!"));
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } catch (FileNotFoundException e) {
            fail("Error loading source script file: " + e);
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Test the generate schema feature from the Persistence API.
     */
    public void testPersistenceGenerateSchemaUseConnection() {
        String GENERATE_SCHEMA_NO_CONNECTION_DROP_TARGET = "jpa22-generate-schema-use-connection-drop.jdbc";
        String GENERATE_SCHEMA_NO_CONNECTION_CREATE_TARGET = "jpa22-generate-schema-use-connection-create.jdbc";
        String GENERATE_SCHEMA_NO_CONNECTION_SESSION_NAME = "jpa22-generate-schema-use-conn-session";

        Map properties = new HashMap();
        // Get database properties will pick up test.properties database connection details.
        properties.putAll(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
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
     * Test the generate schema feature from the Persistence API.
     *
     * This test will then further use the PU and make sure the connection
     * occurs and we can persist objects.
     *
     * All properties are set in code.
     */
    public void testPersistenceGenerateSchemaNoConnection(String platform, String majorVersion, String minorVersion) {
        // Get platform call will deploy our app and be stored in our
        // testing framework. Need to clear it for this test.
        closeEntityManagerFactory();

        String GENERATE_SCHEMA_NO_CONNECTION_DROP_TARGET = "jpa22-generate-schema-no-connection-drop-"+platform+".jdbc";
        String GENERATE_SCHEMA_NO_CONNECTION_CREATE_TARGET = "jpa22-generate-schema-no-connection-create-"+platform+".jdbc";
        String GENERATE_SCHEMA_NO_CONNECTION_SESSION_NAME = "jpa22-generate-schema-no-conn-session-" + platform;

        Map properties = new HashMap();
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
        Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.SESSION_NAME, "generate-schema-no-conn-session-drop-only");
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, true);
        properties.put(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME, "MySQL");
        properties.put(PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION, "5");
        properties.put(PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION, "5");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, "jpa22-generate-schema-no-connection-drop-only.jdbc");

        Persistence.generateSchema(getPersistenceUnitName(), properties);
    }

    /**
     * Test the generate schema feature from the Persistence API.
     */
    public void testPersistenceGenerateSchemaUsingProvidedWriters() {
        Map properties = new HashMap();
        // Get database properties will pick up test.properties database connection details.
        properties.putAll(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
        properties.put(PersistenceUnitProperties.SESSION_NAME, "jpa22-generate-schema-using-provided-writers");
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, "true");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);

        try {
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, new FileWriter(new File("jpa22-generate-schema-using-drop-writer.jdbc")));
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, new FileWriter(new File("jpa22-generate-schema-using-create-writer.jdbc")));

            Persistence.generateSchema(getPersistenceUnitName(), properties);
        } catch (IOException e) {
            fail("Error occurred: " + e);
        }
    }

    public void testRootTargetScriptFileName() {
        // This test is not called. It can be tested manually by uncommenting
        // it out in the suite setup.
        Map properties = new HashMap();
        // Get database properties will pick up test.properties database connection details.
        properties.putAll(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
        properties.put(PersistenceUnitProperties.SESSION_NAME, "jpa22-testRootTargetScriptFileName");
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, "true");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, "/temp-generate-schema-drop.jdbc");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, "/temp-generate-schema-create.jdbc");

        Persistence.generateSchema(getPersistenceUnitName(), properties);
    }

    public void testIllegalArgumentExceptionWithNoScriptTargetProvided() {
        Map properties = new HashMap();
        // Get database properties will pick up test.properties database connection details.
        properties.putAll(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
        properties.put(PersistenceUnitProperties.SESSION_NAME, "jpa22-testIllegalArgumentExceptionWithNoScriptTargetsProvided");
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

    public void testDatabaseSchemaGenerationCreateOnly() {
        //currently server test framework supports multiple persistence units tests up to 5 PUs(MulitPU-1...5), the tests of this 6th PU will be excluded from running on server
        if (!isOnServer()) {
            Map properties = new HashMap();
            // Get database properties will pick up test.properties database connection details.
            properties.putAll(JUnitTestCaseHelper.getDatabaseProperties("ddl-schema-template"));
            properties.put(PersistenceUnitProperties.SESSION_NAME, "jpa22-ddl-schema-create-only-session");
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_ACTION);
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_DATABASE_SCHEMAS, "true");
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, "jpa22-ddl-schema-create-only-create.jdbc");

            try {
                Persistence.generateSchema("ddl-schema-template", properties);
            } catch (Exception exception) {
                fail("Exception caught when generating schema: " + exception.getMessage());
            }
        }
    }

    public void testDatabaseSchemaGenerationDropOnly() {
        //currently server test framework supports multiple persistence units tests up to 5 PUs(MulitPU-1...5), the tests of this 6th PU will be excluded from running on server
        if (!isOnServer()) {
            Map properties = new HashMap();
            // Get database properties will pick up test.properties database connection details.
            properties.putAll(JUnitTestCaseHelper.getDatabaseProperties("ddl-schema-template"));
            properties.put(PersistenceUnitProperties.SESSION_NAME, "jpa22-ddl-schema-drop-only-session");
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_ACTION);
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_DATABASE_SCHEMAS, "true");
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, "jpa22-ddl-schema-drop-only-drop.jdbc");

            try {
                Persistence.generateSchema("ddl-schema-template", properties);
            } catch (Exception exception) {
                exception.printStackTrace();
                fail("Exception caught when generating schema: " + exception.getMessage());
            }
        }
    }

    public void testDatabaseSchemaGenerationDropAndCreate() {
        //currently server test framework supports multiple persistence units tests up to 5 PUs(MulitPU-1...5), the tests of this 6th PU will be excluded from running on server
        if (!isOnServer()) {
            Map properties = new HashMap();
            // Get database properties will pick up test.properties database connection details.
            properties.putAll(JUnitTestCaseHelper.getDatabaseProperties("ddl-schema-template"));
            properties.put(PersistenceUnitProperties.SESSION_NAME, "jpa22-ddl-schema-drop-and-create-session");
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_CREATE_DATABASE_SCHEMAS, "true");
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, "jpa22-ddl-schema-drop-and-create-drop.jdbc");
            properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, "jpa22-ddl-schema-drop-and-create-create.jdbc");

            try {
                Persistence.generateSchema("ddl-schema-template", properties);
            } catch (Exception exception) {
                fail("Exception caught when generating schema: " + exception.getMessage());
            }
        }
    }

    public void testDatabaseSchemaGenerationURLTargets() {
        // This test is not called. It can be tested manually by uncommenting
        // it out in the suite setup.
        Map properties = new HashMap();
        // Get database properties will pick up test.properties database connection details.
        properties.putAll(JUnitTestCaseHelper.getDatabaseProperties("ddl-schema-template"));
        properties.put(PersistenceUnitProperties.SESSION_NAME, "jpa22-ddl-schema-url-target-session");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, "file:///jpa22-ddl-schema-url-target-create.jdbc");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, "file:///jpa22-ddl-schema-url-target-drop.jdbc");

        try {
            Persistence.generateSchema("ddl-schema-template", properties);
        } catch (Exception exception) {
            fail("Exception caught when generating schema: " + exception.getMessage());
        }
    }

    /**
     * Test the container PersistenceProvider.generateSchema(PersistenceUnitInfo
     * info, Map map) method from the Persistence API.
     */
    public void testContainerGenerateSchema() {
        String CONTAINER_GENERATE_SCHEMA_DROP_TARGET = "jpa22-container-generate-schema-drop.jdbc";
        String CONTAINER_GENERATE_SCHEMA_CREATE_TARGET = "jpa22-container-generate-schema-create.jdbc";
        String CONTAINER_GENERATE_SCHEMA_SESSION_NAME = "jpa22-container-generate-schema-session";

        Map properties = new HashMap();
        // Get database properties will pick up test.properties database connection details.
        properties.putAll(JUnitTestCaseHelper.getDatabaseProperties(getPersistenceUnitName()));
        properties.put(PersistenceUnitProperties.SESSION_NAME, CONTAINER_GENERATE_SCHEMA_SESSION_NAME);
        properties.put(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, "true");
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_ACTION, PersistenceUnitProperties.SCHEMA_GENERATION_DROP_AND_CREATE_ACTION);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_DROP_TARGET, CONTAINER_GENERATE_SCHEMA_DROP_TARGET);
        properties.put(PersistenceUnitProperties.SCHEMA_GENERATION_SCRIPTS_CREATE_TARGET, CONTAINER_GENERATE_SCHEMA_CREATE_TARGET);

        // When a container calls PersistenceProvider.generateSchema(PersistenceUnitInfo info, Map map),
        // the container passes its own PUInfo Implementation. To avoid having to implement our own PUInfo,
        // a proxy object is created that invokes SEPersistenceUnitInfo's methods in the background.
        PersistenceProvider provider = new PersistenceProvider();
        JPAInitializer initializer = provider.getInitializer(puName, properties);
        SEPersistenceUnitInfo sePUImpl = initializer.findPersistenceUnitInfo(puName, properties);
        PersistenceUnitInfo puinfo = (PersistenceUnitInfo) Proxy.newProxyInstance(SEPersistenceUnitInfo.class.getClassLoader(), new Class[] { PersistenceUnitInfo.class }, new PUInfoInvocationHandler(sePUImpl));
        provider.generateSchema(puinfo, properties);

        // Now create an entity manager and build some objects for this PU using
        // the same session name. Create the schema on the database with the
        // target scripts built previously.
        testPersistenceGenerateSchemaOnDatabase(CONTAINER_GENERATE_SCHEMA_CREATE_TARGET, CONTAINER_GENERATE_SCHEMA_DROP_TARGET, CONTAINER_GENERATE_SCHEMA_SESSION_NAME);
    }
}
