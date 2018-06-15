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
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.testing.tests.jpa21.advanced;

import java.util.Date;
import java.util.Map;

import javax.persistence.EntityManager;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

import org.eclipse.persistence.sessions.server.ServerSession;

import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Health;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Level;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.RunningStatus;

import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.Organizer;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.Race;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.Responsibility;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.Runner;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.RunnerInfo;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.RunnerStatus;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.Shoe;
import org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl.Sprinter;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;

import junit.framework.TestSuite;
import junit.framework.Test;

public class XMLForeignKeyTestSuite extends JUnitTestCase {
    public XMLForeignKeyTestSuite() {}

    public XMLForeignKeyTestSuite(String name) {
        super(name);
        setPuName("MulitPU-3");
    }

    protected void assertForeignKeyConstraint(String name, String definition, DatabaseTable table) {
        assertTrue("No foreign key constraints were found on table [" + table.getName()+ "]", table.hasForeignKeyConstraints());
        ForeignKeyConstraint constraint = table.getForeignKeyConstraint(name);
        assertNotNull("The foreign key constraint named [" + name + "] was not found on table [" + table.getName() + "]", constraint);
        assertFalse("The foreign key definition for the constraint named [" + name + "] on table [" + table + "] was null.", constraint.getForeignKeyDefinition() == null);
        assertTrue("The foreign key definition for the constraint named [" + name + "] on table [" + table + "] was set as [" + constraint.getForeignKeyDefinition() + " ], but was expecting [" + definition + " ]", constraint.getForeignKeyDefinition().equals(definition));
    }

    /**
     * Return the the persistence unit name for this test suite.
     */
    @Override
    public String getPersistenceUnitName() {
        return "MulitPU-3";
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("XMLForeignKeyTestSuite");

        suite.addTest(new XMLForeignKeyTestSuite("testInheritancePrimaryKeyForeignKey"));
        suite.addTest(new XMLForeignKeyTestSuite("testCollectionTableForeignKey"));
        suite.addTest(new XMLForeignKeyTestSuite("testJoinTableForeignKeys"));
        suite.addTest(new XMLForeignKeyTestSuite("testMapKeyForeignKey"));
        suite.addTest(new XMLForeignKeyTestSuite("testManyToOneForeignKey"));
        suite.addTest(new XMLForeignKeyTestSuite("testElementCollectionForeignKeys"));

        suite.addTest(new XMLForeignKeyTestSuite("testReadAndWriteDDLObjects"));

        return suite;
    }

    /**
     * Tests an inheritance primary key foreign key setting.
     */
    public void testInheritancePrimaryKeyForeignKey() {
        ClassDescriptor sprinterDescriptor = getPersistenceUnitServerSession().getDescriptor(Sprinter.class);
        DatabaseTable table = sprinterDescriptor.getTable("JPA21_XML_DDL_SPRINTER");

        assertForeignKeyConstraint("FK_JPA21_XML_Sprinter", "FOREIGN KEY (SPRINTER_ID) REFERENCES JPA21_XML_DDL_RUNNER (ID)", table);
    }

    /**
     * Tests an inheritance primary key foreign key setting.
     */
    public void testCollectionTableForeignKey() {
        ClassDescriptor runnerDescriptor = getPersistenceUnitServerSession().getDescriptor(Runner.class);
        DirectCollectionMapping mapping = (DirectCollectionMapping) runnerDescriptor.getMappingForAttributeName("personalBests");
        DatabaseTable table = mapping.getReferenceTable();

        assertForeignKeyConstraint("FK_JPA21_XML_Runner_PBS", "FOREIGN KEY (RUNNER_ID) REFERENCES JPA21_XML_DDL_RUNNER (ID)", table);
    }

    /**
     * Tests a join table foreign key settings.
     */
    public void testJoinTableForeignKeys() {
        ClassDescriptor runnerDescriptor = getPersistenceUnitServerSession().getDescriptor(Runner.class);
        ManyToManyMapping mapping = (ManyToManyMapping) runnerDescriptor.getMappingForAttributeName("races");
        DatabaseTable table = mapping.getRelationTable();

        assertForeignKeyConstraint("FK_JPA21_XMLRunners_Races", "FOREIGN KEY (RUNNER_ID) REFERENCES JPA21_XML_DDL_RUNNER (ID)", table);
        assertForeignKeyConstraint("FK_JPA21_XMLRunners_Races_Inverse", "FOREIGN KEY (RACE_ID) REFERENCES JPA21_XML_DDL_RACE (ID)", table);
    }

    /**
     * Tests a map key foreign key setting.
     */
    public void testMapKeyForeignKey() {
        ClassDescriptor runnerDescriptor = getPersistenceUnitServerSession().getDescriptor(Runner.class);
        OneToManyMapping mapping = (OneToManyMapping) runnerDescriptor.getMappingForAttributeName("shoes");
        OneToOneMapping keyMapping = (OneToOneMapping) ((MappedKeyMapContainerPolicy) mapping.getContainerPolicy()).getKeyMapping();
        DatabaseTable table = keyMapping.getForeignKeyFields().get(0).getTable();

        assertForeignKeyConstraint("FK_JPA21_XMLRunner_ShoeTag", "FOREIGN KEY (TAG_ID) REFERENCES JPA21_XML_DDL_SHOE_TAG (ID)", table);
    }

    /**
     * Tests a many to one foreign key setting.
     */
    public void testManyToOneForeignKey() {
        ClassDescriptor shoeDescriptor = getPersistenceUnitServerSession().getDescriptor(Shoe.class);
        OneToOneMapping mapping = (OneToOneMapping) shoeDescriptor.getMappingForAttributeName("runner");
        DatabaseTable table = mapping.getForeignKeyFields().get(0).getTable();

        assertForeignKeyConstraint("FK_JPA21_XMLShoes_Runner", "FOREIGN KEY (RUNNER_ID) REFERENCES JPA21_XML_DDL_RUNNER (ID)", table);

        ClassDescriptor organizerDescriptor = getPersistenceUnitServerSession().getDescriptor(Organizer.class);
        mapping = (OneToOneMapping) organizerDescriptor.getMappingForAttributeName("race");
        table = mapping.getForeignKeyFields().get(0).getTable();

        assertForeignKeyConstraint("FK_JPA21_XMLOrganizer_Race", "FOREIGN KEY (RACE_ID) REFERENCES JPA21_XML_DDL_RACE (ID)", table);
    }

    /**
     * Tests an element collection foreign key settings.
     */
    public void testElementCollectionForeignKeys() {
        ClassDescriptor runnerDescriptor = getPersistenceUnitServerSession().getDescriptor(Runner.class);
        DirectMapMapping mapping = (DirectMapMapping) runnerDescriptor.getMappingForAttributeName("endorsements");
        OneToOneMapping keyMapping = (OneToOneMapping) ((MappedKeyMapContainerPolicy) mapping.getContainerPolicy()).getKeyMapping();
        DatabaseTable table = mapping.getReferenceTable();

        assertForeignKeyConstraint("FK_JPA21_XMLEndorsements", "FOREIGN KEY (ATHLETE_ID) REFERENCES JPA21_XML_DDL_RUNNER (ID)", table);

        table = keyMapping.getForeignKeyFields().get(0).getTable();

        assertForeignKeyConstraint("FK_JPA21_XMLEndorsements_Key", "FOREIGN KEY (ENDORSER_ID) REFERENCES JPA21_XML_DDL_ENDORSER (ID)", table);

        mapping = (DirectMapMapping) runnerDescriptor.getMappingForAttributeName("accomplishments");
        table = mapping.getReferenceTable();

        assertForeignKeyConstraint("FK_JPA21_XMLAccomplishments", "FOREIGN KEY (ATHLETE_ID) REFERENCES JPA21_XML_DDL_RUNNER (ID)", table);
    }

    /**
     * Test copied from xml Converter test suite. Will write and read the
     * objects back and at the same time test their converters as well.
     */
    public void testReadAndWriteDDLObjects() {
        // Load scripts for this PU are built for MySql
        if (getPlatform().isMySQL()) {
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
}
