/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.*;
import java.io.Serializable;
import static javax.persistence.GenerationType.*;

@Entity
@Table(name="CMP3_HOCKEY_PLAYER")
public class HockeyPlayer implements Serializable {
    private int playerId;
    private Vitals vitals;
    private String lastName;
    private String firstName;
    private HockeyCoach coach;

    public HockeyPlayer () {}

    @Column(name="FNAME")
	public String getFirstName() { 
        return firstName; 
    }
    
    @Column(name="LNAME")
	public String getLastName() { 
        return lastName; 
    }
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="HOCKEY_PLAYER_TABLE_GENERATOR")
	@TableGenerator(
        name="HOCKEY_PLAYER_TABLE_GENERATOR", 
        table="CMP3_HOCKEY_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="HOCKEY_PLAYER_SEQ"
    )
    public int getPlayerId() { 
        return playerId; 
    }
    
    @Embedded
    @AttributeOverride(name="teamVitals.jerseyNumber", column=@Column(name="JERSEY_NUMBER"))
    public Vitals getVitals() {
        return vitals;
    }
    
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }
    
	public void setPlayerId(int playerId) { 
        this.playerId = playerId; 
    }

	public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }
    
    public void setVitals(Vitals vitals) {
        this.vitals = vitals;
    }

    @ManyToOne
    public HockeyCoach getCoach() {
        return coach;
    }

    public void setCoach(HockeyCoach coach) {
        this.coach = coach;
    }
    
    public String toString() {
        return "Hockey player: " + getFirstName() + " " + getLastName();
    }
}
