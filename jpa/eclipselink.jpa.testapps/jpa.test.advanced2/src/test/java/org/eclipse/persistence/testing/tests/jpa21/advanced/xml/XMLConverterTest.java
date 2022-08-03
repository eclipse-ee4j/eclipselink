/*
 * Copyright (c) 2012, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     10/25/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.testing.tests.jpa21.advanced.xml;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Health;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Level;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.RunningStatus;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.Organizer;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.Race;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.Responsibility;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.Runner;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.RunnerInfo;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.RunnerStatus;

import java.util.Date;
import java.util.Map;

public class XMLConverterTest extends JUnitTestCase {
    public XMLConverterTest() {}

    public XMLConverterTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    /**
     * Return the the persistence unit name for this test suite.
     */
    @Override
    public String getPersistenceUnitName() {
        return "advanced2x-xml";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("XMLConverterTest");

        suite.addTest(new XMLConverterTest("testSetup"));
        suite.addTest(new XMLConverterTest("testXMLConverters"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedTableCreator().replaceTables(getPersistenceUnitServerSession());
        org.eclipse.persistence.testing.tests.jpa21.advanced.xml.EmployeePopulator employeePopulator = new EmployeePopulator();
        employeePopulator.buildExamples();
        employeePopulator.persistExample(getPersistenceUnitServerSession());
        clearCache();
    }

    /**
     * Test that converters are set.
     */
    public void testXMLConverters() {
        EntityManager em = createEntityManager();

        try {
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
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
    }
}
