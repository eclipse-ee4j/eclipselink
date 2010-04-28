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
 *     Mar 17, 2010-2.0.2 Chris Delahunt 
 *       - Bug 304251: can not call EntityManager.merge on Entities with MapKeyColumn and ALCT 
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.complexaggregate;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.CoachVitals;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.HockeyCoach;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.HockeyPlayer;
import org.eclipse.persistence.testing.models.jpa.complexaggregate.PersonalVitals;
import org.eclipse.persistence.testing.tests.jpa.EntityContainerTestBase;

/**
 * @author Chris Delahunt
 * @since Eclipselink 2.0.2
 *
 */
public class MapKeyColumnMergeTest extends EntityContainerTestBase {

    protected Object playerId;
    protected Object player2Id;
    protected Object coachId;
    
    public MapKeyColumnMergeTest() {
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

        HockeyPlayer player2 = new HockeyPlayer();
        player2.setFirstName("Power");
        player2.setLastName("Flower");
        
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
        
        beginTransaction();
        getEntityManager().persist(player);
        getEntityManager().persist(player2);
        getEntityManager().persist(coach);
        playerId = player.getPlayerId();
        player2Id = player2.getPlayerId();
        coachId = coach.getId();
        coach.addFavouritePlayer(player);
        commitTransaction();
        getEntityManager().close();
        beginTransaction();
//        ((ChangeTracker)coach)._persistence_setPropertyChangeListener(null);
//        ((IndirectMap)coach.getFavouritePlayers())._persistence_setPropertyChangeListener(null);
        coach.addFavouritePlayer(player2);
        getEntityManager().merge(coach);
        commitTransaction();
    }
    
    public void reset(){
        HockeyPlayer player = getEntityManager().find(HockeyPlayer.class, playerId);
        HockeyPlayer player2 = getEntityManager().find(HockeyPlayer.class, player2Id);
        HockeyCoach coach = getEntityManager().find(HockeyCoach.class, coachId);
        beginTransaction();
        if (player != null){
            getEntityManager().remove(player);
        }
        if (player2 != null){
            getEntityManager().remove(player2);
        }
        if (coach != null){
            getEntityManager().remove(coach);
        }
        commitTransaction();
    }
    
}

