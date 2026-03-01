/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     04/12/2013-2.5 Guy Pelletier
//       - 405640: JPA 2.1 schema generation drop operation fails to include dropping defaulted fk constraints.
package org.eclipse.persistence.testing.tests.advanced2.ddl;

import jakarta.persistence.EntityManager;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa21.advanced.ddl.Coach;
import org.eclipse.persistence.testing.models.jpa21.advanced.ddl.Driver;
import org.eclipse.persistence.testing.models.jpa21.advanced.ddl.Organizer;
import org.eclipse.persistence.testing.models.jpa21.advanced.ddl.Race;
import org.eclipse.persistence.testing.models.jpa21.advanced.ddl.Responsibility;
import org.eclipse.persistence.testing.models.jpa21.advanced.ddl.Runner;
import org.eclipse.persistence.testing.models.jpa21.advanced.ddl.RunnerInfo;
import org.eclipse.persistence.testing.models.jpa21.advanced.ddl.RunnerStatus;
import org.eclipse.persistence.testing.models.jpa21.advanced.ddl.Shoe;
import org.eclipse.persistence.testing.models.jpa21.advanced.ddl.Sprinter;
import org.eclipse.persistence.testing.models.jpa21.advanced.ddl.Vehicle;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Health;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Level;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.RunningStatus;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

public class ForeignKeyTest extends JUnitTestCase {
    public ForeignKeyTest() {}

    public ForeignKeyTest(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    protected void assertForeignKeyConstraint(String name, String definition, DatabaseTable table) {
        assertTrue("No foreign key constraints were found on table [" + table.getName()+ "]", table.hasForeignKeyConstraints());
        ForeignKeyConstraint constraint = table.getForeignKeyConstraint(name);
        assertNotNull("The foreign key constraint named [" + name + "] was not found on table [" + table.getName() + "]", constraint);
        assertNotNull("The foreign key definition for the constraint named [" + name + "] on table [" + table + "] was null.", constraint.getForeignKeyDefinition());
        assertEquals("The foreign key definition for the constraint named [" + name + "] on table [" + table + "] was set as [" + constraint.getForeignKeyDefinition() + " ], but was expecting [" + definition + " ]", constraint.getForeignKeyDefinition(), definition);
    }

    /**
     * Return the the persistence unit name for this test suite.
     */
    @Override
    public String getPersistenceUnitName() {
        return "advanced2x-ddl";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("ForeignKeyTest");

        suite.addTest(new ForeignKeyTest("testInheritancePrimaryKeyForeignKey"));
        suite.addTest(new ForeignKeyTest("testCollectionTableForeignKey"));
        suite.addTest(new ForeignKeyTest("testJoinTableForeignKeys"));
        suite.addTest(new ForeignKeyTest("testMapKeyForeignKey"));
        suite.addTest(new ForeignKeyTest("testManyToOneForeignKey"));
        suite.addTest(new ForeignKeyTest("testElementCollectionForeignKeys"));

        suite.addTest(new ForeignKeyTest("testReadAndWriteDDLObjects"));
        suite.addTest(new ForeignKeyTest("testBug441546"));


        return suite;
    }

    /**
     * Tests an inheritance primary key foreign key setting.
     */
    public void testInheritancePrimaryKeyForeignKey() {
        ClassDescriptor sprinterDescriptor = getPersistenceUnitServerSession().getDescriptor(Sprinter.class);
        DatabaseTable table = sprinterDescriptor.getTable("JPA21_DDL_SPRINTER");

        assertForeignKeyConstraint("Sprinter_Foreign_Key", "FOREIGN KEY (SPRINTER_ID) REFERENCES JPA21_DDL_RUNNER (ID)", table);
    }

    /**
     * Tests a collection table foreign key setting.
     */
    public void testCollectionTableForeignKey() {
        ClassDescriptor runnerDescriptor = getPersistenceUnitServerSession().getDescriptor(Runner.class);
        DirectCollectionMapping mapping = (DirectCollectionMapping) runnerDescriptor.getMappingForAttributeName("personalBests");
        DatabaseTable table = mapping.getReferenceTable();

        assertForeignKeyConstraint("Runner_PBS_Foreign_Key", "FOREIGN KEY (RUNNER_ID) REFERENCES JPA21_DDL_RUNNER (ID)", table);
    }

    /**
     * Tests a join table foreign key settings.
     */
    public void testJoinTableForeignKeys() {
        ClassDescriptor runnerDescriptor = getPersistenceUnitServerSession().getDescriptor(Runner.class);
        ManyToManyMapping mapping = (ManyToManyMapping) runnerDescriptor.getMappingForAttributeName("races");
        DatabaseTable table = mapping.getRelationTable();

        assertForeignKeyConstraint("Runners_Races_Foreign_Key", "FOREIGN KEY (RUNNER_ID) REFERENCES JPA21_DDL_RUNNER (ID)", table);
        assertForeignKeyConstraint("Runners_Races_Inverse_Foreign_Key", "FOREIGN KEY (RACE_ID) REFERENCES JPA21_DDL_RACE (ID)", table);
    }

    /**
     * Tests a map key foreign key setting.
     */
    public void testMapKeyForeignKey() {
        ClassDescriptor runnerDescriptor = getPersistenceUnitServerSession().getDescriptor(Runner.class);
        OneToManyMapping mapping = (OneToManyMapping) runnerDescriptor.getMappingForAttributeName("shoes");
        OneToOneMapping keyMapping = (OneToOneMapping) ((MappedKeyMapContainerPolicy) mapping.getContainerPolicy()).getKeyMapping();
        DatabaseTable table = keyMapping.getForeignKeyFields().get(0).getTable();

        assertForeignKeyConstraint("Runner_ShoeTag_Foreign_Key", "FOREIGN KEY (TAG_ID) REFERENCES JPA21_DDL_SHOE_TAG (ID)", table);
    }

    /**
     * Tests a many to one foreign key setting.
     */
    public void testManyToOneForeignKey() {
        ClassDescriptor shoeDescriptor = getPersistenceUnitServerSession().getDescriptor(Shoe.class);
        OneToOneMapping mapping = (OneToOneMapping) shoeDescriptor.getMappingForAttributeName("runner");
        DatabaseTable table = mapping.getForeignKeyFields().get(0).getTable();

        assertForeignKeyConstraint("Shoes_Runner_Foreign_Key", "FOREIGN KEY (RUNNER_ID) REFERENCES JPA21_DDL_RUNNER (ID)", table);

        ClassDescriptor organizerDescriptor = getPersistenceUnitServerSession().getDescriptor(Organizer.class);
        mapping = (OneToOneMapping) organizerDescriptor.getMappingForAttributeName("race");
        table = mapping.getForeignKeyFields().get(0).getTable();

        assertForeignKeyConstraint("Organizer_Race_Foreign_Key", "FOREIGN KEY (RACE_ID) REFERENCES JPA21_DDL_RACE (ID)", table);
    }

    /**
     * Tests an element collection foreign key settings.
     */
    public void testElementCollectionForeignKeys() {
        ClassDescriptor runnerDescriptor = getPersistenceUnitServerSession().getDescriptor(Runner.class);
        DirectMapMapping mapping = (DirectMapMapping) runnerDescriptor.getMappingForAttributeName("endorsements");
        OneToOneMapping keyMapping = (OneToOneMapping) ((MappedKeyMapContainerPolicy) mapping.getContainerPolicy()).getKeyMapping();
        DatabaseTable table = mapping.getReferenceTable();

        assertForeignKeyConstraint("Endorsements_Foreign_Key", "FOREIGN KEY (ATHLETE_ID) REFERENCES JPA21_DDL_RUNNER (ID)", table);

        table = keyMapping.getForeignKeyFields().get(0).getTable();

        assertForeignKeyConstraint("Endorsements_Key_Foreign_Key", "FOREIGN KEY (ENDORSER_ID) REFERENCES JPA21_DDL_ENDORSER (ID)", table);

        mapping = (DirectMapMapping) runnerDescriptor.getMappingForAttributeName("accomplishments");
        table = mapping.getReferenceTable();

        assertForeignKeyConstraint("Accomplistments_Foreign_Key", "FOREIGN KEY (ATHLETE_ID) REFERENCES JPA21_DDL_RUNNER (ID)", table);
    }

    /**
     * Test copied from Converter test suite. Will write and read the objects
     * back and at the same time test their converters as well.
     */
    public void testReadAndWriteDDLObjects() {
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

            Coach coach = new Coach();
            runner.addCoach(coach);

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
            em.persist(coach);
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
    
    /**
     * Tests a many to one foreign key setting with null foreign key definition.
     */
    public void testBug441546() {
        if (isOnServer()) {
            return;
        }

        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            
            Driver driver = new Driver();
            driver.setName("Bob");
            
            Vehicle vehicle = new Vehicle();
            vehicle.setVnum("1A467");
            vehicle.setDriver(driver);

            em.persist(driver);
            em.persist(vehicle);
            commitTransaction(em);
 
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            throw e;
        } finally {
            closeEntityManager(em);
        }
        
        em = createEntityManager();
        try {
            // Try Deleting Driver with constraint
            beginTransaction(em);
            
            Driver d2 = em.find(Driver.class, "Bob");
            em.remove(d2);
            
            commitTransaction(em);         
            fail("Foreign Key constraint is not defined in database on table.");
   
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            Throwable cause = e.getCause();

            if (cause instanceof DatabaseException) {
                assertTrue("Error Deleting row with constraint with different error.", cause.getCause() instanceof SQLException);
            } else { 
                throw e;
            }

        } finally {
            closeEntityManager(em);
        }
    }
}
