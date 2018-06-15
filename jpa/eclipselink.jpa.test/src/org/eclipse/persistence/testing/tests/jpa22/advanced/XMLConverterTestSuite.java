/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
package org.eclipse.persistence.testing.tests.jpa22.advanced;

import java.util.Date;
import java.util.Map;

import javax.persistence.EntityManager;

import junit.framework.TestSuite;
import junit.framework.Test;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import org.eclipse.persistence.testing.models.jpa22.advanced.enums.Health;
import org.eclipse.persistence.testing.models.jpa22.advanced.enums.Level;
import org.eclipse.persistence.testing.models.jpa22.advanced.enums.RunningStatus;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.Organizer;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.Race;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.Responsibility;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.Runner;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.RunnerInfo;
import org.eclipse.persistence.testing.models.jpa22.advanced.xml.RunnerStatus;

public class XMLConverterTestSuite extends JUnitTestCase {
    public XMLConverterTestSuite() {}

    public XMLConverterTestSuite(String name) {
        super(name);
        setPuName("MulitPU-4");
    }

    /**
     * Return the the persistence unit name for this test suite.
     */
    @Override
    public String getPersistenceUnitName() {
        return "MulitPU-4";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("XMLConverterTestSuite");

        suite.addTest(new XMLConverterTestSuite("testXMLConverters"));

        return suite;
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
        } finally {
            closeEntityManager(em);
        }
    }
}
