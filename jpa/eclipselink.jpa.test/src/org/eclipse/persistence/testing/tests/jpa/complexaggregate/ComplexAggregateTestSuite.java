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
 *     Oracle - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.complexaggregate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.testing.framework.JoinedAttributeTestHelper;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.Body;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.CitySlicker;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.CoachVitals;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.ComplexAggregateTableCreator;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.CountryDweller;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.Heart;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.HockeyCoach;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.HockeyPlayer;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.HockeyTeam;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.Name;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.PersonalVitals;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.Role;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.TeamVitals;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.Torso;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.Vitals;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.World;

/**
 * <p><b>Purpose</b>: To JPA with the complex aggregate test model.
 */
public class ComplexAggregateTestSuite extends JUnitTestCase {

    public ComplexAggregateTestSuite() {
    }
    
    public ComplexAggregateTestSuite(String name) {
        super(name);
    }
    
    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new ComplexAggregateTableCreator().replaceTables(getDatabaseSession());
        clearCache();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("ComplexAggregateTestSuite");
        suite.addTest(new ComplexAggregateTestSuite("testSetup"));
        suite.addTest(new ComplexAggregateTestSuite("testMapKeyColumnMerge"));
        suite.addTest(new ComplexAggregateTestSuite("testAggregatePrimaryKey"));
        suite.addTest(new ComplexAggregateTestSuite("testAggregatePrimaryKeyOrderBy"));
        suite.addTest(new ComplexAggregateTestSuite("testNestedAggregate"));
        suite.addTest(new ComplexAggregateTestSuite("testNestedAggregatePrimaryKey"));
        suite.addTest(new ComplexAggregateTestSuite("testAggregateReadOnlyMapKey"));
        suite.addTest(new ComplexAggregateTestSuite("testComplexAggregateJoin"));

        return suite;
    }
        
    public void testMapKeyColumnMerge() {        
        clearCache();
        
        EntityManager em = createEntityManager();
        try {
            HockeyPlayer player = new HockeyPlayer();
            player.setFirstName("Guy");
            player.setLastName("Flower");
            // must have non null Vitals and TeamVitals because TeamVitals has a target foreign key mapping, therefore allowingNull automatically set to false
            Vitals vitals = new Vitals();
            vitals.setTeamVitals(new TeamVitals());
            player.setVitals(vitals);
    
            HockeyPlayer player2 = new HockeyPlayer();
            player2.setFirstName("Power");
            player2.setLastName("Flower");;
            // must have non null Vitals and TeamVitals because TeamVitals has a target foreign key mapping, therefore allowingNull automatically set to false
            Vitals vitals2 = new Vitals();
            vitals2.setTeamVitals(new TeamVitals());
            player2.setVitals(vitals2);
            
            HockeyCoach coach = new HockeyCoach();
            coach.setFirstName("Scott");
            coach.setLastName("Arrowman");
            CoachVitals cVitals = new CoachVitals();
            PersonalVitals pVitals = new PersonalVitals();
            pVitals.setAge(45);
            pVitals.setHeight(3.3);
            pVitals.setWeight(333);
            cVitals.setPersonalVitals(pVitals);
            coach.setVitals(cVitals);
            
            beginTransaction(em);
            
            em.persist(player);
            em.persist(player2);
            em.persist(coach);
            Object playerId = player.getPlayerId();
            Object player2Id = player2.getPlayerId();
            Object coachId = coach.getId();
            coach.addFavouritePlayer(player);
            
            commitTransaction(em);
            closeEntityManager(em);
            
            em = createEntityManager();
            beginTransaction(em);
    //        ((ChangeTracker)coach)._persistence_setPropertyChangeListener(null);
    //        ((IndirectMap)coach.getFavouritePlayers())._persistence_setPropertyChangeListener(null);
            coach.addFavouritePlayer(player2);
            em.merge(coach);
            commitTransaction(em);
            
            beginTransaction(em);
            player = em.find(HockeyPlayer.class, playerId);
            player2 = em.find(HockeyPlayer.class, player2Id);
            coach = em.find(HockeyCoach.class, coachId);
            if (player != null){
                em.remove(player);
            }
            if (player2 != null){
                em.remove(player2);
            }
            if (coach != null){
                em.remove(coach);
            }
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    public void testAggregatePrimaryKey() {
        clearCache();
        EntityManager em = createEntityManager();
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
            clearCache();           
            em.clear();
            
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
            closeEntityManagerAndTransaction(em);
            throw e;
        }
    }
    
    public void testAggregatePrimaryKeyOrderBy() {
        clearCache();
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            
            World world = new World();
            
            // First pair
            Name name1 = new Name();
            name1.setFirstName("Guy");
            name1.setLastName("Pelletier");
            
            CountryDweller countryDweller1 = new CountryDweller();
            countryDweller1.setAge(30);
            countryDweller1.setName(name1);
            countryDweller1.setGender("Male");
            em.persist(countryDweller1);
            world.addCountryDweller(countryDweller1);
            
            CitySlicker citySlicker1 = new CitySlicker();
            citySlicker1.setAge(53);
            // Bug 300696 - Invalid tests: sharing embedded objects is not allowed 
            // Changed the test to use clone instead of sharing the same Name between the two Entities.
            Name name1Clone = (Name)name1.clone();
            citySlicker1.setName(name1Clone);
            citySlicker1.setGender("Male");
            em.persist(citySlicker1);
            world.addCitySlicker(citySlicker1);
            
            // Second pair
            Name name2 = new Name();
            name2.setFirstName("Steve");
            name2.setLastName("Harp");
            
            CountryDweller countryDweller2 = new CountryDweller();
            countryDweller2.setAge(28);
            countryDweller2.setName(name2);
            countryDweller2.setGender("Male");
            em.persist(countryDweller2);
            world.addCountryDweller(countryDweller2);
            
            CitySlicker citySlicker2 = new CitySlicker();
            citySlicker2.setAge(41);
            // Bug 300696 - Invalid tests: sharing embedded objects is not allowed 
            // Changed the test to use clone instead of sharing the same Name between the two Entities.
            Name name2Clone = (Name)name2.clone();
            citySlicker2.setName(name2Clone);
            citySlicker2.setGender("Male");
            em.persist(citySlicker2);
            world.addCitySlicker(citySlicker2);
            
            // Third pair
            Name name3 = new Name();
            name3.setFirstName("Jesse");
            name3.setLastName("Petoncle");
            
            CountryDweller countryDweller3 = new CountryDweller();
            countryDweller3.setAge(48);
            countryDweller3.setName(name3);
            countryDweller3.setGender("Male");
            em.persist(countryDweller3);
            world.addCountryDweller(countryDweller3);
            
            CitySlicker citySlicker3 = new CitySlicker();
            citySlicker3.setAge(76);
            // Bug 300696 - Invalid tests: sharing embedded objects is not allowed 
            // Changed the test to use clone instead of sharing the same Name between the two Entities.
            Name name3Clone = (Name)name3.clone();
            citySlicker3.setName(name3Clone);
            citySlicker3.setGender("Female");
            em.persist(citySlicker3);
            world.addCitySlicker(citySlicker3);

            em.persist(world);
            
            commitTransaction(em);
            
            // Clear the cache.
            clearCache();           
            em.clear();            
            
            // Now read them back in and delete them.
            beginTransaction(em);
            
            World w = em.find(World.class, world.getId());
            
            Collection css = w.getCitySlickers();
            css.toString();
            Collection cds = w.getCountryDwellers();
            cds.toString();
        
            // Check the ordering
            // JBS - Ordering check removed as order is random based on class method order which is not consistent in Java.
            //citySlickersAreOrdered = ((CitySlicker) css.elementAt(0)).getName().getFirstName().equals("Guy") && ((CitySlicker) css.elementAt(1)).getName().getFirstName().equals("Jesse") && ((CitySlicker) css.elementAt(2)).getName().getFirstName().equals("Steve");
            //countryDwellersAreOrdered = ((CountryDweller) cds.elementAt(0)).getAge() == 28 && ((CountryDweller) cds.elementAt(1)).getAge() == 30 && ((CountryDweller) cds.elementAt(2)).getAge() == 48;
        
            // Make sure we delete them    
            // Note that in Identity case name1Clone may no longer have the same id as name1
            CitySlicker cs1 = em.find(CitySlicker.class, name1Clone);
            em.remove(cs1);
            CitySlicker cs2 = em.find(CitySlicker.class, name2Clone);
            em.remove(cs2);
            CitySlicker cs3 = em.find(CitySlicker.class, name3Clone);
            em.remove(cs3);
            
            CountryDweller cd1 = em.merge(countryDweller1);
            em.remove(cd1);
            CountryDweller cd2 = em.merge(countryDweller2);
            em.remove(cd2);
            CountryDweller cd3 = em.merge(countryDweller3);
            em.remove(cd3);
            
            commitTransaction(em);
        
        } catch (RuntimeException e) {
            closeEntityManagerAndTransaction(em);
            throw e;
        }
    }
        
    public void testNestedAggregate() {
        int[] teamIDs = new int[3];
        clearCache();
        //////// Team 1 ////////
        HockeyTeam team1 = new HockeyTeam();
        team1.setName("Axemen");
        team1.setAwayColor("Red");
        team1.setHomeColor("White");
        team1.setLevel("Division 5");
        
        //////// Team 2 ////////
        HockeyTeam team2 = new HockeyTeam();
        team2.setName("Cartier Partners");
        team2.setAwayColor("Black");
        team2.setHomeColor("White");
        team2.setLevel("Division 2");
        
        //////// Team 3 ////////
        HockeyTeam team3 = new HockeyTeam();
        team3.setName("Dead Last");
        team3.setAwayColor("Blue");
        team3.setHomeColor("White");
        team3.setLevel("Division 4");
        
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
        
        //////// Coach 1 ////////
        HockeyCoach coach1 = new HockeyCoach();
        coach1.setFirstName("Don");
        coach1.setLastName("Hyslop");
        
        PersonalVitals coachPersonalVitals1 = new PersonalVitals();
        coachPersonalVitals1.setAge(55);
        coachPersonalVitals1.setHeight(1.85);
        coachPersonalVitals1.setWeight(200);
        
        CoachVitals coachVitals1 = new CoachVitals();
        coachVitals1.setPersonalVitals(coachPersonalVitals1);
        coachVitals1.setHockeyTeam(team1);
        team1.getCoaches().add(coach1);
        coach1.setVitals(coachVitals1);
        
        //////// Coach 2 ////////
        HockeyCoach coach2 = new HockeyCoach();
        coach2.setFirstName("Al");
        coach2.setLastName("Montoya");
        
        PersonalVitals coachPersonalVitals2 = new PersonalVitals();
        coachPersonalVitals2.setAge(63);
        coachPersonalVitals2.setHeight(1.86);
        coachPersonalVitals2.setWeight(213);
        
        CoachVitals coachVitals2 = new CoachVitals();
        coachVitals2.setPersonalVitals(coachPersonalVitals2);
        coachVitals2.setHockeyTeam(team2);
        team2.getCoaches().add(coach2);
        coach2.setVitals(coachVitals2);
        
        //////// Coach 3 ////////
        HockeyCoach coach3 = new HockeyCoach();
        coach3.setFirstName("Guy");
        coach3.setLastName("Carbonneau");
        
        PersonalVitals coachPersonalVitals3 = new PersonalVitals();
        coachPersonalVitals3.setAge(47);
        coachPersonalVitals3.setHeight(1.91);
        coachPersonalVitals3.setWeight(191);
        
        CoachVitals coachVitals3 = new CoachVitals();
        coachVitals3.setPersonalVitals(coachPersonalVitals3);
        coachVitals3.setHockeyTeam(team3);
        team3.getCoaches().add(coach3);
        coach3.setVitals(coachVitals3);
        
        //////// Coach 4 ////////
        HockeyCoach coach4 = new HockeyCoach();
        coach4.setFirstName("Walter");
        coach4.setLastName("Mullen");
        
        PersonalVitals coachPersonalVitals4 = new PersonalVitals();
        coachPersonalVitals4.setAge(67);
        coachPersonalVitals4.setHeight(1.94);
        coachPersonalVitals4.setWeight(187);
        
        CoachVitals coachVitals4 = new CoachVitals();
        coachVitals4.setPersonalVitals(coachPersonalVitals4);
        coachVitals4.setHockeyTeam(team1);
        team1.getCoaches().add(coach4);
        coach4.setVitals(coachVitals4);
        
        //////// Coach 5 ////////
        HockeyCoach coach5 = new HockeyCoach();
        coach5.setFirstName("Ben");
        coach5.setLastName("Gasket");
        
        PersonalVitals coachPersonalVitals5 = new PersonalVitals();
        coachPersonalVitals5.setAge(33);
        coachPersonalVitals5.setHeight(1.67);
        coachPersonalVitals5.setWeight(155);
        
        CoachVitals coachVitals5 = new CoachVitals();
        coachVitals5.setPersonalVitals(coachPersonalVitals5);
        coachVitals5.setHockeyTeam(team2);
        team2.getCoaches().add(coach5);
        coach5.setVitals(coachVitals5);
        
        //////// Coach 6 ////////
        HockeyCoach coach6 = new HockeyCoach();
        coach6.setFirstName("Jim");
        coach6.setLastName("Balogna");
        
        PersonalVitals coachPersonalVitals6 = new PersonalVitals();
        coachPersonalVitals6.setAge(37);
        coachPersonalVitals6.setHeight(1.77);
        coachPersonalVitals6.setWeight(179);
        
        CoachVitals coachVitals6 = new CoachVitals();
        coachVitals6.setPersonalVitals(coachPersonalVitals5);
        coachVitals6.setHockeyTeam(team3);
        team3.getCoaches().add(coach6);
        coach6.setVitals(coachVitals6);

        EntityManager em = createEntityManager();
        //////// Commit the new objects ////////
        beginTransaction(em);
        
        try{
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
            
            em.persist(coach1);
            em.persist(coach2);
            em.persist(coach3);
            em.persist(coach4);
            em.persist(coach5);
            em.persist(coach6);
            
            commitTransaction(em);
        } catch (RuntimeException ex) {
            closeEntityManagerAndTransaction(em);
            throw ex;
        }

        em = createEntityManager();
        // Check the cache for the objects
        checkTeam(teamIDs[0], em);
        checkTeam(teamIDs[1], em);
        checkTeam(teamIDs[2], em);
        
        // Initialize the identity map to make sure they were persisted
        clearCache();

        em = createEntityManager();
        em.clear();
        
        checkTeam(teamIDs[0], em);
        checkTeam(teamIDs[1], em);
        checkTeam(teamIDs[2], em);

        // Verify the order by on coaches worked for team 1 (teamIDs[0])
        HockeyTeam team = em.find(HockeyTeam.class, teamIDs[0]);

        if (team.getCoaches().get(0).getVitals().getPersonalVitals().getAge() != 67) {
            throw new TestErrorException("The order by specification on age for hockey coaches was not observed: " + team.getCoaches());
        }
    }
    
    private void checkTeam(int id, EntityManager em) {
        HockeyTeam team = em.find(HockeyTeam.class, id);
        if (team == null){
            throw new TestErrorException("Hockey team with ID: " + id + ", was not created.");
        }
        
        if (team.getPlayers().size() != 2) {
            throw new TestErrorException("Hockey team with ID: " + id + ", did not have 2 players, had: " + team.getPlayers().size());
        }
        
        if (team.getCoaches().size() != 2) {
            throw new TestErrorException("Hockey team with ID: " + id + ", did not have 2 coach, had: " + team.getCoaches().size());
        }
    }

    public void testNestedAggregatePrimaryKey() {
        clearCache();
        
        Torso torso;

        EntityManager em = createEntityManager();
        try {
            Body body = new Body();
            torso = new Torso();
            Heart heart = new Heart();
            heart.setSize(8);
            torso.setHeart(heart);
            body.setTorso(torso);
            
            beginTransaction(em);
            em.persist(body);
            commitTransaction(em);
        } catch (Exception e) {
            throw new TestErrorException("Exception caught when persisting the new body: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }

        em = createEntityManager();
        Exception m_testException = null;
        DatabaseSessionImpl m_session = getDatabaseSession();
        Body m_refreshedBody = null;
        // Try to read the body back, clear the cache first.
        try {
            em.clear();
            m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
            m_refreshedBody = em.find(Body.class, torso);
        } catch (Exception e) {
            m_testException = e;
        }
    
        if (m_testException != null) {
            throw new TestErrorException("Exception caught reading back the persisted body: " + m_testException);
        }

        if (m_refreshedBody == null) {
            throw new TestErrorException("Unable to read back the persisted body");
        }

        ClassDescriptor descriptor = getDatabaseSession().getClassDescriptor(Body.class);
        Object pks = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(m_refreshedBody, m_session);
        Torso createdTorso = (Torso) descriptor.getCMPPolicy().createPrimaryKeyInstanceFromId(pks, m_session);        
        assertTrue("PK's do not match.", m_refreshedBody.getTorso().equals(createdTorso));

    }

    public void testAggregateReadOnlyMapKey() {
        clearCache();

        HockeyPlayer player = new HockeyPlayer();
        player.setFirstName("Guy");
        player.setLastName("Flower");

        PersonalVitals personalVitals2 = new PersonalVitals();
        personalVitals2.setAge(45);
        personalVitals2.setHeight(1.80);
        personalVitals2.setWeight(185);
        
        TeamVitals teamVitals2 = new TeamVitals();
        teamVitals2.setJerseyNumber(70);
        teamVitals2.setPosition("Right wing");
        
        Vitals vitals2 = new Vitals();
        vitals2.setPersonalVitals(personalVitals2);
        vitals2.setTeamVitals(teamVitals2);
        player.setVitals(vitals2);
        
        HockeyCoach coach = new HockeyCoach();
        coach.setFirstName("Scott");
        coach.setLastName("Arrowman");
        
        PersonalVitals coachPersonalVitals1 = new PersonalVitals();
        coachPersonalVitals1.setAge(65);
        coachPersonalVitals1.setHeight(1.85);
        coachPersonalVitals1.setWeight(200);
        
        CoachVitals coachVitals1 = new CoachVitals();
        coachVitals1.setPersonalVitals(coachPersonalVitals1);
        coach.setVitals(coachVitals1);

        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.persist(player);
            em.persist(coach);
            commitTransaction(em);
            
            Object playerId = player.getPlayerId();
            Object coachId = coach.getId();
    
            clearCache();           
            em.clear();          
    
            beginTransaction(em);
            player = em.find(HockeyPlayer.class, player.getPlayerId());
            coach = em.find(HockeyCoach.class, coach.getId());
            
            coach.addFavouritePlayer(player);
            commitTransaction(em);
            
            beginTransaction(em);
            player = em.find(HockeyPlayer.class, playerId);
            coach = em.find(HockeyCoach.class, coachId);
            if (player != null){
                em.remove(player);
            }
            if (coach != null){
                em.remove(coach);
            }
            commitTransaction(em);
        } finally {
            closeEntityManagerAndTransaction(em);
        }
    }

    public void testComplexAggregateJoin() {
        clearCache();

        HockeyTeam team1 = new HockeyTeam();
        team1.setName("Axemen");
        team1.setAwayColor("Red");
        team1.setHomeColor("White");
        team1.setLevel("Division 5");

        ////////Player 1 ////////
        HockeyPlayer player1 = new HockeyPlayer();
        player1.setFirstName("Guy");
        player1.setLastName("Pelletier");

        PersonalVitals personalVitals1 = new PersonalVitals();
        personalVitals1.setAge(29);
        personalVitals1.setHeight(1.80);
        personalVitals1.setWeight(180);

        TeamVitals teamVitals1 = new TeamVitals();

        teamVitals1.setJerseyNumber(20);
        teamVitals1.setPosition("Goalie");
        teamVitals1.getRoles().add(new Role("Stop pucks!"));

        Vitals vitals1 = new Vitals();
        vitals1.setPersonalVitals(personalVitals1);
        vitals1.setTeamVitals(teamVitals1);
        player1.setVitals(vitals1);


        ////////Coach 1 ////////
        HockeyCoach coach1 = new HockeyCoach();
        coach1.setFirstName("Don");
        coach1.setLastName("Hyslop");

        PersonalVitals coachPersonalVitals1 = new PersonalVitals();
        coachPersonalVitals1.setAge(55);
        coachPersonalVitals1.setHeight(1.85);
        coachPersonalVitals1.setWeight(200);

        CoachVitals coachVitals1 = new CoachVitals();
        coachVitals1.setPersonalVitals(coachPersonalVitals1);
        coach1.setVitals(coachVitals1);

        ////////persist the new objects ////////

        EntityManager em = createEntityManager();
        beginTransaction(em);
        em.persist(team1);

        em.persist(player1);
        em.persist(coach1);

        //setup relationships:
        teamVitals1.setHockeyTeam(team1);
        team1.getPlayers().add(player1);        
        team1.getCoaches().add(coach1);
        coachVitals1.setHockeyTeam(team1);

        em.flush();

        DatabaseSessionImpl m_session = getDatabaseSession();
        List hockeyCoachResults = null;
        List controledCoachList =  null;
        List controledPlayerList = null;
        List hockeyPlayerResults = null;
        try {            
            em.clear();
            m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
            
            Query query1 = em.createQuery("Select a from HockeyCoach a where a.id = "+coach1.getId());
            query1.setHint(QueryHints.LEFT_FETCH, "a.vitals.hockeyTeam");
            hockeyCoachResults = query1.getResultList();
            
            em.clear();
            m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
            
            Query query2 = em.createQuery("Select z from HockeyPlayer z where z.playerId = "+player1.getPlayerId());
            //query2.setHint(QueryHints.LEFT_FETCH, "z.coach");
            query2.setHint(QueryHints.LEFT_FETCH, "z.vitals.teamVitals.hockeyTeam.coaches");
            hockeyPlayerResults = query2.getResultList();
            
            em.clear();
            m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
            
            HockeyCoach expectedCoach = em.find(HockeyCoach.class, coach1.getId());
            expectedCoach.getVitals().getHockeyTeam();
            controledCoachList = new ArrayList<HockeyCoach>(1);
            controledCoachList.add(expectedCoach);
    
            em.clear();
            m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
    
            HockeyPlayer expectedPlayer = em.find(HockeyPlayer.class, player1.getPlayerId());
            expectedPlayer.getVitals().getTeamVitals().getHockeyTeam().getCoaches().size();
            controledPlayerList = new ArrayList<HockeyPlayer>(1);
            controledPlayerList.add(expectedPlayer);
            
            em.clear();
            m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
        } finally  {
            try {
                closeEntityManagerAndTransaction(em);
            } catch (RuntimeException re){
                //ignore
            }
        }

        String errorMsg = JoinedAttributeTestHelper.compareCollections(controledCoachList, hockeyCoachResults, m_session.getClassDescriptor(HockeyCoach.class), m_session);
        if (errorMsg.length() > 0) {
            throw new TestErrorException(errorMsg);
        }
        
        errorMsg = JoinedAttributeTestHelper.compareCollections(controledPlayerList, hockeyPlayerResults, m_session.getClassDescriptor(HockeyPlayer.class), m_session);
        if (errorMsg.length() > 0) {
            throw new TestErrorException(errorMsg);
        }
    }
}
