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

import static javax.persistence.FetchType.EAGER;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Collection;

@Embeddable
public class CoachVitals implements Serializable {
    private HockeyTeam hockeyTeam;
    private PersonalVitals personalVitals;
    private Collection<String> nickNames;

    public CoachVitals() {}
    
    @OneToOne(fetch=EAGER)
    @JoinColumn(name="TEAM_ID", referencedColumnName="ID")    
    public HockeyTeam getHockeyTeam() {
        return hockeyTeam;
    }
    
    @Embedded
    public PersonalVitals getPersonalVitals() {
        return personalVitals;
    }
    
    public void setHockeyTeam(HockeyTeam hockeyTeam) {
        this.hockeyTeam = hockeyTeam;
    }
    
    public void setPersonalVitals(PersonalVitals personalVitals) {
        this.personalVitals = personalVitals;
    }
    
    @ElementCollection
    public Collection<String> getNickNames() {
        return nickNames;
    }

    public void setNickNames(Collection<String> nickNames) {
        this.nickNames = nickNames;
    }
}
