/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

@Entity
@Table(name="CMP3_HOCKEY_COACH")
public class HockeyCoach implements Serializable {
    private int id;
    private CoachVitals vitals;
    private String lastName;
    private String firstName;
    
    public HockeyCoach () {}

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
    
    public String toString() {
        return "Hockey coach: " + getFirstName() + " " + getLastName();
    }
}

