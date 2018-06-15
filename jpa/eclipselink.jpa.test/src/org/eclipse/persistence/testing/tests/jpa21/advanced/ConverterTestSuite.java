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
//     10/09/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     10/25/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     02/28/2013-2.5 Chris Delahunt
//       - 402029: Application exceptions need to be wrapped in PersistenceException
//     06/03/2013-2.5.1 Guy Pelletier
//       - 402380: 3 jpa21/advanced tests failed on server with
//         "java.lang.NoClassDefFoundError: org/eclipse/persistence/testing/models/jpa21/advanced/enums/Gender"
//     07/16/2013-2.5.1 Guy Pelletier
//       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
//     11/06/2014-2.6 Tomas Kraus
//       - 449818: Test to verify Convert annotation on ElementCollection of Embeddable class.
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.ConverterClass;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa21.advanced.Organizer;
import org.eclipse.persistence.testing.models.jpa21.advanced.Race;
import org.eclipse.persistence.testing.models.jpa21.advanced.Responsibility;
import org.eclipse.persistence.testing.models.jpa21.advanced.Runner;
import org.eclipse.persistence.testing.models.jpa21.advanced.RunnerInfo;
import org.eclipse.persistence.testing.models.jpa21.advanced.RunnerStatus;
import org.eclipse.persistence.testing.models.jpa21.advanced.RunnerVictory;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.CompetitionConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.ResponsibilityConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Health;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Level;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.RunningStatus;

public class ConverterTestSuite extends JUnitTestCase {
    public ConverterTestSuite() {}

    public ConverterTestSuite(String name) {
        super(name);
        setPuName("MulitPU-1");
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("ConverterTestSuite");

        suite.addTest(new ConverterTestSuite("testAutoApplyConverter"));
        suite.addTest(new ConverterTestSuite("testAnnotationConverters"));
        suite.addTest(new ConverterTestSuite("testConverterExceptionWrapping1"));
        suite.addTest(new ConverterTestSuite("testConverterExceptionWrapping2"));
        suite.addTest(new ConverterTestSuite("testConvertOnElementCollection"));

        return suite;
    }

    /**
     * Test that an attribute picks up an auto apply converter.
     */
    public void testAutoApplyConverter() {
        ServerSession session = getPersistenceUnitServerSession();

        ClassDescriptor employeeDescriptor = session.getDescriptor(Employee.class);
        DirectToFieldMapping salaryMapping = (DirectToFieldMapping) employeeDescriptor.getMappingForAttributeName("salary");
        assertNotNull("Salary mapping did not have the auto apply converter", salaryMapping.getConverter());

        DirectToFieldMapping previousSalaryMapping = (DirectToFieldMapping) employeeDescriptor.getMappingForAttributeName("previousSalary");
        assertNull("Salary mapping did not have the auto apply converter", previousSalaryMapping.getConverter());

        ClassDescriptor runnerDescriptor = session.getDescriptor(Runner.class);

        DirectToFieldMapping tagsMapping = (DirectToFieldMapping) runnerDescriptor.getMappingForAttributeName("tags");
        assertTrue("Tags mapping did not have a converter", tagsMapping.hasConverter());
        assertTrue("Serials mappings did not have a SerializedObjectConverter", tagsMapping.getConverter() instanceof ConverterClass);

        DirectToFieldMapping serialsMapping = (DirectToFieldMapping) runnerDescriptor.getMappingForAttributeName("serials");
        assertTrue("Serials mapping did not have a converter", serialsMapping.hasConverter());
        assertTrue("Serials mappings did not have a SerializedObjectConverter", serialsMapping.getConverter() instanceof SerializedObjectConverter);
    }

    /**
     * Test that converters are set.
     */
    public void testAnnotationConverters() {
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
            runner.addTag("tag1");
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
            em.flush();

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
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    /**
     * Test that application exceptions thrown from the converter's convertToEntityAttribute method get
     * wrapped in a PersistenceException.
     * Added for bug 402029: Application exceptions need to be wrapped in PersistenceException
     */
    public void testConverterExceptionWrapping1() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            // setup
            Race race = new Race();
            race.setName("Just Another Marathon");

            Organizer organizer = new Organizer();
            organizer.setName("John Smith");
            organizer.setRace(race);

            Responsibility responsibility = new Responsibility();
            responsibility.setUniqueIdentifier(new Long(System.currentTimeMillis()));
            //This string causes an exception to be thrown from the ResponsibilityConverter.convertToEntityAttribute method
            responsibility.setDescription(ResponsibilityConverter.THROW_EXCEPTION_IN_TO_ENTITY_ATTRIBUTE);

            race.addOrganizer(organizer, responsibility);

            try {
                em.persist(race);
                em.persist(organizer);
                em.flush();

                // Clear the cache
                em.clear();
                clearCache();

                race = em.find(Race.class, race.getId());
                //trigger indirection
                race.getOrganizers().get(responsibility);
            } catch (javax.persistence.PersistenceException pe) {
                if (pe.getCause() == null ||
                        !ResponsibilityConverter.THROW_EXCEPTION_IN_TO_ENTITY_ATTRIBUTE.equals(pe.getCause().getMessage())) {
                    //rethrow this exception because it does not contain the expected application generated RuntimeException
                    throw pe;
                }//else this error is expected and can be ignored
            } catch (RuntimeException unexpected) {
                if (ResponsibilityConverter.THROW_EXCEPTION_IN_TO_ENTITY_ATTRIBUTE.equals(unexpected.getMessage())) {
                    this.fail("Application exception thrown from convertToEntityAttribute was not wrapped in a PersistenceException");
                }
                throw unexpected;
            }

        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    /**
     * Test that application exceptions thrown from the converter's convertToDatabaseColumn method get
     * wrapped in a PersistenceException.
     * Added for bug 402029: Application exceptions need to be wrapped in PersistenceException
     */
    public void testConverterExceptionWrapping2() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            // setup
            Race race = new Race();
            race.setName("Just Another Marathon");

            Organizer organizer = new Organizer();
            organizer.setName("John Smith");
            organizer.setRace(race);

            Responsibility responsibility = new Responsibility();
            responsibility.setUniqueIdentifier(new Long(System.currentTimeMillis()));
            //This string causes an exception to be thrown from the ResponsibilityConverter.convertToDatabaseColumn method
            responsibility.setDescription(ResponsibilityConverter.THROW_EXCEPTION_IN_TO_DATABASE_COLUMN);

            race.addOrganizer(organizer, responsibility);

            try {
                em.persist(race);
                em.persist(organizer);
                em.flush();
            } catch (javax.persistence.PersistenceException pe) {
                if (pe.getCause() == null ||
                        !ResponsibilityConverter.THROW_EXCEPTION_IN_TO_DATABASE_COLUMN.equals(pe.getCause().getMessage())) {
                    //rethrow this exception because it does not contain the expected application generated RuntimeException
                    throw pe;
                }//else this error is expected and can be ignored

            } catch (RuntimeException unexpected) {
                if (ResponsibilityConverter.THROW_EXCEPTION_IN_TO_DATABASE_COLUMN.equals(unexpected.getMessage())) {
                    this.fail("Application exception thrown from convertToDatabaseColumn was not wrapped in a PersistenceException");
                }
                throw unexpected;
            }

        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    /**
     * Verify {@code @Convert} annotation on {@code @ElementCollection} mapping. Two collections are defined, first
     * with annotation and second one without it. Tests verifies if converter class is applied on both collections
     * or not.
     */
    public void testConvertOnElementCollection() {
        final EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            // Converter used in @ElementCollection mapping.
            final CompetitionConverter converter = new CompetitionConverter();
            // Run test for all existing Runner instances.
            final TypedQuery<Runner> rq = em.createNamedQuery("Runner.listAll", Runner.class);
            final List<Runner> runners = rq.getResultList();
            assertTrue("No Runner object was returned", runners.size() > 0);
            for (Runner runner : runners) {

                // Verify Map processed with @Convert annotation.
                final Map <String, RunnerVictory> victoriesThisYear = runner.getVictoriesThisYear();
                final Query rvtq = em.createNamedQuery("RunnerVictoryThis.getById");
                rvtq.setParameter(1, runner.getId());
                final List<Object[]> victories = rvtq.getResultList();
                assertEquals("Count of retrieved objects from database shall be the same.",
                        runner.getVictoriesThisYear().size(), victories.size());
                for (Object[] victory : victories) {
                    final String name = (String)victory[0];
                    final Long id = ((Number)victory[1]).longValue();
                    final String competition = (String)victory[2];
                    final RunnerVictory entityVictory = victoriesThisYear.get(name);
                    String entityCompetitionConverted
                            = converter.convertToDatabaseColumn(entityVictory.getCompetition());
                    String dbCompetitionConverted = converter.convertToEntityAttribute(competition);
                    assertFalse("Entity and database values shall not match.",
                            competition.equals(entityVictory.getCompetition()));
                    assertTrue("Manually converted entity value and plain database value shall match.",
                            competition.equals(entityCompetitionConverted));
                    assertTrue("Manually converted database value and plain entity value shall match.",
                            entityVictory.getCompetition().equals(dbCompetitionConverted));
                }
            }
            for (Runner runner : runners) {

                // Verify Map not processed with @Convert annotation.
                final Map <String, RunnerVictory> victoriesLastYear = runner.getVictoriesLastYear();
                final Query rvlq = em.createNamedQuery("RunnerVictoryLast.getById");
                rvlq.setParameter(1, runner.getId());
                final List<Object[]> victories = rvlq.getResultList();
                assertEquals("Count of retrieved objects from database shall be the same.",
                        runner.getVictoriesLastYear().size(), victories.size());
                for (Object[] victory : victories) {
                    final String name = (String)victory[0];
                    final Long id = ((Number)victory[1]).longValue();
                    final String competition = (String)victory[2];
                    final RunnerVictory entityVictory = victoriesLastYear.get(name);
                    // Converter is missing so only entity to database conversion check makes sense.
                    String entityCompetitionConverted
                            = converter.convertToDatabaseColumn(entityVictory.getCompetition());
                    assertTrue("Entity and database values shall match.",
                            competition.equals(entityVictory.getCompetition()));
                    assertFalse("Manually converted entity value and plain database value shall not match.",
                            competition.equals(entityCompetitionConverted));
                }
            }
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    @Override
    public String getPersistenceUnitName() {
        return "MulitPU-1";
    }
}
