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
 *     tware - test for bug fix 299774
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.complexaggregate;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.CoachVitals;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.HockeyCoach;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.HockeyPlayer;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.PersonalVitals;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.TeamVitals;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.Vitals;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

public class AggregateReadOnlyMapKeyTest extends EntityContainerTestBase {

    private int playerId;
    private int coachId;
    
    public AggregateReadOnlyMapKeyTest() {
        setDescription("Tests using a read-only aggregate as a map key.");
    }
    
    public void setup () {
        super.setup();
        ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test(){
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
        
        beginTransaction();
        getEntityManager().persist(player);
        getEntityManager().persist(coach);
        commitTransaction();
        
        playerId = player.getPlayerId();
        coachId = coach.getId();
        
        ((EntityManagerImpl)getEntityManager()).getActiveSession().getIdentityMapAccessor().initializeAllIdentityMaps();            

        beginTransaction();
        player = getEntityManager().find(HockeyPlayer.class, player.getPlayerId());
        coach = getEntityManager().find(HockeyCoach.class, coach.getId());
        
        coach.addFavouritePlayer(player);
        commitTransaction();
    }
    
    public void reset(){
        HockeyPlayer player = getEntityManager().find(HockeyPlayer.class, playerId);
        HockeyCoach coach = getEntityManager().find(HockeyCoach.class, coachId);
        beginTransaction();
        if (player != null){
            getEntityManager().remove(player);
        }
        if (coach != null){
            getEntityManager().remove(coach);
        }
        commitTransaction();
    }
    
}
