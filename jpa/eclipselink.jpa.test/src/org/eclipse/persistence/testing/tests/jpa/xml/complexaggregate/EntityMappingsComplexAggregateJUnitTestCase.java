/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/02/2008-1.0M6 Guy Pelletier 
 *       - 224155: embeddable-attributes should be extended in the EclipseLink ORM.XML schema
 *     08/27/2008-1.1 Guy Pelletier 
 *       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.xml.complexaggregate;

import javax.persistence.EntityManager;

import junit.framework.*;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.xml.complexaggregate.CitySlicker;
import org.eclipse.persistence.testing.models.jpa.xml.complexaggregate.ComplexAggregateTableCreator;
import org.eclipse.persistence.testing.models.jpa.xml.complexaggregate.CountryDweller;
import org.eclipse.persistence.testing.models.jpa.xml.complexaggregate.HockeyPlayer;
import org.eclipse.persistence.testing.models.jpa.xml.complexaggregate.HockeyTeam;
import org.eclipse.persistence.testing.models.jpa.xml.complexaggregate.HockeyTeamDetails;
import org.eclipse.persistence.testing.models.jpa.xml.complexaggregate.HockeyTeamId;
import org.eclipse.persistence.testing.models.jpa.xml.complexaggregate.Name;
import org.eclipse.persistence.testing.models.jpa.xml.complexaggregate.PersonalVitals;
import org.eclipse.persistence.testing.models.jpa.xml.complexaggregate.Role;
import org.eclipse.persistence.testing.models.jpa.xml.complexaggregate.TeamVitals;
import org.eclipse.persistence.testing.models.jpa.xml.complexaggregate.Vitals;
import org.eclipse.persistence.testing.tests.jpa.TestingProperties;

/**
 * The mapping file for this test model is located here:
 * resource/eclipselinkorm/eclipselink-xml-extended-model/complexaggregate-extended-entity-mappings
 */
public class EntityMappingsComplexAggregateJUnitTestCase extends JUnitTestCase {
    private static HockeyTeamId[] teamIDs = new HockeyTeamId[3];
    
    private String m_persistenceUnit;
    
    public EntityMappingsComplexAggregateJUnitTestCase() {
        super();
    }
    
    public EntityMappingsComplexAggregateJUnitTestCase(String name) {
        super(name);
    }
    
    public EntityMappingsComplexAggregateJUnitTestCase(String name, String persistenceUnit) {
        super(name);
        
        m_persistenceUnit = persistenceUnit;
    }
    
    public static Test suite() {
        String ormTesting = TestingProperties.getProperty(TestingProperties.ORM_TESTING, TestingProperties.JPA_ORM_TESTING);
        final String persistenceUnit = ormTesting.equals(TestingProperties.JPA_ORM_TESTING)? "default" : "extended-complex-aggregate";
        TestSuite suite = new TestSuite("Complex Aggregate Model - " + persistenceUnit);
        
        if (persistenceUnit.equals("extended-complex-aggregate")) {
            suite.addTest(new EntityMappingsComplexAggregateJUnitTestCase("testSetup", persistenceUnit));
            suite.addTest(new EntityMappingsComplexAggregateJUnitTestCase("testCreateExtendedObjects", persistenceUnit));
            suite.addTest(new EntityMappingsComplexAggregateJUnitTestCase("testVerifyExtendedObjects", persistenceUnit));
            suite.addTest(new EntityMappingsComplexAggregateJUnitTestCase("testAutoGenerationOnEmbeddedId", persistenceUnit));
        }
        
        return suite;
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        DatabaseSession session = JUnitTestCase.getServerSession(m_persistenceUnit);
        new ComplexAggregateTableCreator().replaceTables(session);
        clearCache(m_persistenceUnit);
    }
    
    public void setUp () {
        super.setUp();
        clearCache(m_persistenceUnit);
    }
    
    public void testCreateExtendedObjects() {
        //////// Team 1 ////////
        HockeyTeam team1 = new HockeyTeam("Axemen");
        HockeyTeamDetails team1Details = new HockeyTeamDetails();
        team1Details.setAwayColor("Red");
        team1Details.setHomeColor("White");
        team1Details.setLevel("Division 5");
        team1.setTeamDetails(team1Details);
            
        //////// Team 2 ////////
        HockeyTeam team2 = new HockeyTeam("Cartier Partners");
        HockeyTeamDetails team2Details = new HockeyTeamDetails();
        team2Details.setAwayColor("Black");
        team2Details.setHomeColor("White");
        team2Details.setLevel("Division 2");
        team2.setTeamDetails(team2Details);
            
        //////// Team 3 ////////
        HockeyTeam team3 = new HockeyTeam("Dead Last");
        HockeyTeamDetails team3Details = new HockeyTeamDetails();
        team3Details.setAwayColor("Blue");
        team3Details.setHomeColor("White");
        team3Details.setLevel("Division 4");
        team3.setTeamDetails(team3Details);
            
        //////// Player 1 ////////
        HockeyPlayer player1 = new HockeyPlayer();
        player1.setFirstName("Guy");
        player1.setLastName("Pelletier");
            
        PersonalVitals personalVitals1 = new PersonalVitals();
        personalVitals1.setAge(29);
        personalVitals1.setHeight(1.80);
        personalVitals1.setWeight(180);
            
        TeamVitals teamVitals1 = new TeamVitals();
        teamVitals1.setHockeyTeam(team2);
        team2.getPlayers().add(player1);
        teamVitals1.setJerseyNumber(20);
        teamVitals1.setPosition("Goalie");
        teamVitals1.getRoles().add(new Role("Stop pucks!"));
            
        Vitals vitals1 = new Vitals();
        vitals1.setPersonalVitals(personalVitals1);
        vitals1.setTeamVitals(teamVitals1);
        player1.setVitals(vitals1);
            
        //////// Player 2 ////////
        HockeyPlayer player2 = new HockeyPlayer();
        player2.setFirstName("Dave");
        player2.setLastName("McCann");
            
        PersonalVitals personalVitals2 = new PersonalVitals();
        personalVitals2.setAge(35);
        personalVitals2.setHeight(1.77);
        personalVitals2.setWeight(165);
            
        TeamVitals teamVitals2 = new TeamVitals();
        teamVitals2.setHockeyTeam(team1);
        team1.getPlayers().add(player2);
        teamVitals2.setJerseyNumber(70);
        teamVitals2.setPosition("Left wing");
        teamVitals2.getRoles().add(new Role("Power play unit"));
        teamVitals2.getRoles().add(new Role("Face-off specialist"));
            
        Vitals vitals2 = new Vitals();
        vitals2.setPersonalVitals(personalVitals2);
        vitals2.setTeamVitals(teamVitals2);
        player2.setVitals(vitals2);
        
        //////// Player 3 ////////
        HockeyPlayer player3 = new HockeyPlayer();
        player3.setFirstName("Tom");
        player3.setLastName("Ware");
            
        PersonalVitals personalVitals3 = new PersonalVitals();
        personalVitals3.setAge(30);
        personalVitals3.setHeight(1.83);
        personalVitals3.setWeight(200);
            
        TeamVitals teamVitals3 = new TeamVitals();
        teamVitals3.setHockeyTeam(team1);
        team1.getPlayers().add(player3);
        teamVitals3.setJerseyNumber(12);
        teamVitals3.setPosition("Defence");
        teamVitals3.getRoles().add(new Role("Penalty kill unit"));
            
        Vitals vitals3 = new Vitals();
        vitals3.setPersonalVitals(personalVitals3);
        vitals3.setTeamVitals(teamVitals3);
        player3.setVitals(vitals3);
            
        //////// Player 4 ////////
        HockeyPlayer player4 = new HockeyPlayer();
        player4.setFirstName("George");
        player4.setLastName("Robinson");
            
        PersonalVitals personalVitals4 = new PersonalVitals();
        personalVitals4.setAge(32);
        personalVitals4.setHeight(1.86);
        personalVitals4.setWeight(210);
            
        TeamVitals teamVitals4 = new TeamVitals();
        teamVitals4.setHockeyTeam(team3);
        team3.getPlayers().add(player4);
        teamVitals4.setJerseyNumber(6);
        teamVitals4.setPosition("Center");
        teamVitals4.getRoles().add(new Role("Power play unit"));
        teamVitals4.getRoles().add(new Role("Goon"));
            
        Vitals vitals4 = new Vitals();
        vitals4.setPersonalVitals(personalVitals4);
        vitals4.setTeamVitals(teamVitals4);
        player4.setVitals(vitals4);
            
        //////// Player 5 ////////
        HockeyPlayer player5 = new HockeyPlayer();
        player5.setFirstName("Andrew");
        player5.setLastName("Glennie");
            
        PersonalVitals personalVitals5 = new PersonalVitals();
        personalVitals5.setAge(31);
        personalVitals5.setHeight(1.80);
        personalVitals5.setWeight(205);
            
        TeamVitals teamVitals5 = new TeamVitals();
        teamVitals5.setHockeyTeam(team3);
        team3.getPlayers().add(player5);
        teamVitals5.setJerseyNumber(7);
        teamVitals5.setPosition("Right wing");
            
        Vitals vitals5 = new Vitals();
        vitals5.setPersonalVitals(personalVitals5);
        vitals5.setTeamVitals(teamVitals5);
        player5.setVitals(vitals5);
            
        //////// Player 6 ////////
        HockeyPlayer player6 = new HockeyPlayer();
        player6.setFirstName("David");
        player6.setLastName("Whittaker");
            
        PersonalVitals personalVitals6 = new PersonalVitals();
        personalVitals6.setAge(32);
        personalVitals6.setHeight(1.83);
        personalVitals6.setWeight(190);
            
        TeamVitals teamVitals6 = new TeamVitals();
        teamVitals6.setHockeyTeam(team2);
        team2.getPlayers().add(player6);
        teamVitals6.setJerseyNumber(17);
        teamVitals6.setPosition("Defence");
            
        Vitals vitals6 = new Vitals();
        vitals6.setPersonalVitals(personalVitals6);
        vitals6.setTeamVitals(teamVitals6);
        player6.setVitals(vitals6);
            
        //////// Commit the new objects ////////
        EntityManager em = createEntityManager(m_persistenceUnit);
        
        try {
            beginTransaction(em);
            
            em.persist(team1);
            teamIDs[0] = team1.getId();
            em.persist(team2);
            teamIDs[1] = team2.getId();
            em.persist(team3);
            teamIDs[2] = team3.getId();
                
            em.persist(player1);
            em.persist(player2);
            em.persist(player3);
            em.persist(player4);
            em.persist(player5);
            em.persist(player6);
                
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        closeEntityManager(em);
    }
    
    public void testVerifyExtendedObjects() {
        // Check the cache for the objects
        checkTeam(teamIDs[0]);
        checkTeam(teamIDs[1]);
        checkTeam(teamIDs[2]);
        
        // Clear the cache to ensure the objects were persisted.
        clearCache(m_persistenceUnit);
        
        checkTeam(teamIDs[0]);
        checkTeam(teamIDs[1]);
        checkTeam(teamIDs[2]);
    }
    
    private void checkTeam(HockeyTeamId id) {
        EntityManager em = createEntityManager(m_persistenceUnit);
        HockeyTeam team = em.find(HockeyTeam.class, id);
        assertFalse("Hockey team with ID: " + id + ", was not created.", team == null);
        assertTrue("Hockey team with ID: " + id + ", did not have 2 players added.", team.getPlayers().size() == 2);
    }
    
    
    public void testAutoGenerationOnEmbeddedId() {
        EntityManager em = createEntityManager(m_persistenceUnit);
        
        try {
            Name name1 = new Name();
            name1.setFirstName("Tom");
            name1.setLastName("Ware");
            
            CountryDweller countryDweller = new CountryDweller();
            countryDweller.setAge(30);
            countryDweller.setName(name1);
            
            CitySlicker citySlicker = new CitySlicker();
            citySlicker.setAge(53);
            // Bug 300696 - Invalid tests: sharing embedded objects is not allowed 
            // Changed the test to use clone instead of sharing the same Name between the two Entities.
            Name name1Clone = (Name)name1.clone();
            citySlicker.setName(name1Clone);
            
            Name name2 = new Name();
            name2.setFirstName("Guy");
            name2.setLastName("Pelletier");
            
            CountryDweller countryDweller2 = new CountryDweller();
            countryDweller2.setAge(65);
            countryDweller2.setName(name2);
        
            beginTransaction(em);
            em.persist(countryDweller);
            em.persist(countryDweller2);
            em.persist(citySlicker);
            commitTransaction(em);
        
            // Clear the cache.
            clearCache(m_persistenceUnit);            
            
            // Now read them back in and delete them.
            beginTransaction(em);
            
            // Note that in Identity case name1Clone may no longer have the same id as name1
            CitySlicker cs = em.find(CitySlicker.class, name1Clone);
            CountryDweller cd = em.merge(countryDweller);
            CountryDweller cd2 = em.merge(countryDweller2);
            
            em.remove(cs);
            em.remove(cd);
            em.remove(cd2);
            
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            
            closeEntityManager(em);
            throw e;
        }
        
        closeEntityManager(em);
    }
}
