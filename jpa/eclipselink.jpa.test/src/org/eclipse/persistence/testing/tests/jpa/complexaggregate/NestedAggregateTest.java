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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 *     02/26/2009-2.0 Guy Pelletier 
 *       - 264001: dot notation for mapped-by and order-by
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.complexaggregate;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.*;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;
import org.eclipse.persistence.testing.framework.TestErrorException;

/**
 * BUG 4401389 - EJB30: EMBEDDABLES CAN NOT BE NESTED
 *
 * @author Guy Pelletier
 * @date June 9, 2005
 * @version 1.0
 */
public class NestedAggregateTest extends EntityContainerTestBase {
    protected Session m_session;
    protected boolean m_reset = false;    // reset gets called twice on error
    protected int[] teamIDs = new int[3];
        
    public NestedAggregateTest() {
        setDescription("Tests nested aggregates with relational mappings.");
    }
    
    public void setup () {
        super.setup();
        m_reset = true;
        m_session = ((EntityManagerImpl) getEntityManager()).getActiveSession();
        m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test() throws Exception {
        try {
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
            
            //////// Commit the new objects ////////
            beginTransaction();
            
            try{
                getEntityManager().persist(team1);
                teamIDs[0] = team1.getId();
                getEntityManager().persist(team2);
                teamIDs[1] = team2.getId();
                getEntityManager().persist(team3);
                teamIDs[2] = team3.getId();
                
                getEntityManager().persist(player1);
                getEntityManager().persist(player2);
                getEntityManager().persist(player3);
                getEntityManager().persist(player4);
                getEntityManager().persist(player5);
                getEntityManager().persist(player6);
                
                getEntityManager().persist(coach1);
                getEntityManager().persist(coach2);
                getEntityManager().persist(coach3);
                getEntityManager().persist(coach4);
                getEntityManager().persist(coach5);
                getEntityManager().persist(coach6);
                
                commitTransaction();
            } catch (RuntimeException ex) {
                rollbackTransaction();
                throw ex;
            }
        } catch (DatabaseException e) {
            throw new TestErrorException("Exception caught when populating the teams and players: " + e.getMessage());
        }
    }
    
    public void reset () {
        if (m_reset) {
            m_reset = false;
        }
    }
    
    public void verify() {
        // Check the cache for the objects
        checkTeam(teamIDs[0]);
        checkTeam(teamIDs[1]);
        checkTeam(teamIDs[2]);
        
        // Initialize the identity map to make sure they were persisted
        m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
        
        checkTeam(teamIDs[0]);
        checkTeam(teamIDs[1]);
        checkTeam(teamIDs[2]);
        
        // Verify the order by on coaches worked for team 1 (teamIDs[0])
        HockeyTeam team = getEntityManager().find(HockeyTeam.class, teamIDs[0]);

        if (((HockeyCoach) team.getCoaches().get(0)).getVitals().getPersonalVitals().getAge() != 67) {
            throw new TestErrorException("The order by specification on age for hockey coaches was not observed.");
        }
    }
    
    private void checkTeam(int id) {
        HockeyTeam team = getEntityManager().find(HockeyTeam.class, id);
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
}
