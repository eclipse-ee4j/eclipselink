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
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.*;

import java.io.Serializable;
import static javax.persistence.GenerationType.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name="CMP3_HOCKEY_COACH")
public class HockeyCoach implements Serializable {
    private int id;
    private CoachVitals vitals;
    private String lastName;
    private String firstName;
    
    private Map<HockeyPlayerName, HockeyPlayer> favouritePlayers;
    
    public HockeyCoach () {
        favouritePlayers = new HashMap<HockeyPlayerName, HockeyPlayer>();
    }

    @Column(name="FNAME")
    public String getFirstName() { 
        return firstName; 
    }
    
    @Column(name="LNAME")
    public String getLastName() { 
        return lastName; 
    }
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="HOCKEY_COACH_TABLE_GENERATOR")
    @TableGenerator(
        name="HOCKEY_COACH_TABLE_GENERATOR", 
        table="CMP3_HOCKEY_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="HOCKEY_PLAYER_SEQ"
    )
    @Column(name="ID")
    public int getId() { 
        return id; 
    }
    
    @Embedded
    public CoachVitals getVitals() {
        return vitals;
    }
    
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }

    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }
    
    public void setVitals(CoachVitals vitals) {
        this.vitals = vitals;
    }
    
    @OneToMany(mappedBy="coach")
    public Map<HockeyPlayerName, HockeyPlayer> getFavouritePlayers() {
        return favouritePlayers;
    }

    public void setFavouritePlayers(
            Map<HockeyPlayerName, HockeyPlayer> favouritePlayers) {
        this.favouritePlayers = favouritePlayers;
    }
    
    public void addFavouritePlayer(HockeyPlayer player){
        player.setCoach(this);
        HockeyPlayerName name = new HockeyPlayerName();
        name.setFirstName(player.getFirstName());
        name.setLastName(player.getLastName());
        favouritePlayers.put(name, player);
    }
    
    public String toString() {
        return "Hockey coach: " + getFirstName() + " " + getLastName();
    }
}

