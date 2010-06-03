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
 *     06/03/2010.2.1 christopher.delahunt - 
 *          bug 301675: Relationships on embeddables can't be fetched by QueryHints
 ******************************************************************************/ 
package org.eclipse.persistence.testing.tests.jpa.complexaggregate;

import java.util.ArrayList;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.JoinedAttributeTestHelper;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.CoachVitals;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.HockeyCoach;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.HockeyPlayer;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.HockeyTeam;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.PersonalVitals;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.Role;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.TeamVitals;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.Vitals;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;
import javax.persistence.EntityManager;

/**
 * @author cdelahun
 *
 */
public class ComplexAggregateJoin extends EntityContainerTestBase{
	protected Session m_session;
    HockeyTeam team1;
    HockeyPlayer player1;
    HockeyCoach coach1 ;
    
    List<HockeyCoach> hockeyCoachResults, controledCoachList;
    List<HockeyPlayer> hockeyPlayerResults, controledPlayerList;    
        
    public ComplexAggregateJoin() {
        setDescription("Tests nested aggregates with relational mappings.");
    }
    
    public void populate (EntityManager em) {

        team1 = new HockeyTeam();
        team1.setName("Axemen");
        team1.setAwayColor("Red");
        team1.setHomeColor("White");
        team1.setLevel("Division 5");

        ////////Player 1 ////////
        player1 = new HockeyPlayer();
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
        coach1 = new HockeyCoach();
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

        getEntityManager().persist(team1);

        getEntityManager().persist(player1);
        getEntityManager().persist(coach1);

        //setup relationships:
        teamVitals1.setHockeyTeam(team1);
        team1.getPlayers().add(player1);        
        team1.getCoaches().add(coach1);
        coachVitals1.setHockeyTeam(team1);

        getEntityManager().flush();
    }
    
    public void test() throws Exception {
        try{
            beginTransaction();
            populate(getEntityManager());
            
            
            getEntityManager().clear();
            m_session = ((EntityManagerImpl) getEntityManager()).getServerSession();
            m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
            
            Query query1 = getEntityManager().createQuery("Select a from HockeyCoach a where a.id = "+coach1.getId());
            query1.setHint(QueryHints.LEFT_FETCH, "a.vitals.hockeyTeam");
            hockeyCoachResults = query1.getResultList();
            
            getEntityManager().clear();
            m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
            
            Query query2 = getEntityManager().createQuery("Select z from HockeyPlayer z where z.playerId = "+player1.getPlayerId());
            //query2.setHint(QueryHints.LEFT_FETCH, "z.coach");
            query2.setHint(QueryHints.LEFT_FETCH, "z.vitals.teamVitals.hockeyTeam.coaches");
            hockeyPlayerResults = query2.getResultList();
            
            getEntityManager().clear();
            m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
            
            HockeyCoach expectedCoach = getEntityManager().find(HockeyCoach.class, coach1.getId());
            expectedCoach.getVitals().getHockeyTeam();
            controledCoachList = new ArrayList<HockeyCoach>(1);
            controledCoachList.add(expectedCoach);
    
            getEntityManager().clear();
            m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
    
            HockeyPlayer expectedPlayer = getEntityManager().find(HockeyPlayer.class, player1.getPlayerId());
            expectedPlayer.getVitals().getTeamVitals().getHockeyTeam().getCoaches().size();
            controledPlayerList = new ArrayList<HockeyPlayer>(1);
            controledPlayerList.add(expectedPlayer);
            
            getEntityManager().clear();
            m_session.getIdentityMapAccessor().initializeAllIdentityMaps();
        } finally  {
            try{
                rollbackTransaction();
            } catch (RuntimeException re){
                //ignore
            }
        }
    }

    public void verify() {
        String errorMsg = JoinedAttributeTestHelper.compareCollections(controledCoachList, hockeyCoachResults, m_session.getClassDescriptor(HockeyCoach.class), (AbstractSession)m_session);
        if(errorMsg.length() > 0) {
            throw new TestErrorException(errorMsg);
        }
        
        errorMsg = JoinedAttributeTestHelper.compareCollections(controledPlayerList, hockeyPlayerResults, m_session.getClassDescriptor(HockeyPlayer.class), (AbstractSession)m_session);
        if(errorMsg.length() > 0) {
            throw new TestErrorException(errorMsg);
        }
    }
}
