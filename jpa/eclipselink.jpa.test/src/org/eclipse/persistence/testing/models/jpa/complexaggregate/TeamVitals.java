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
 *     08/28/2008-1.1 Guy Pelletier 
 *       - 245120: unidir one-to-many within embeddable fails to deploy for missing primary key field
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;
import static javax.persistence.CascadeType.PERSIST;

@Embeddable
public class TeamVitals implements Serializable {
    private String position;
    private int jerseyNumber;
    private HockeyTeam hockeyTeam;
    private List<Role> roles;
    
    public TeamVitals() {
        roles = new ArrayList<Role>();
    }

    @Column(name="PLAYER_POSITION")
    public String getPosition() {
        return position;
    }
    
    @OneToOne(fetch=EAGER)
	@JoinColumn(name="TEAM_ID", referencedColumnName="ID")    
    public HockeyTeam getHockeyTeam() {
        return hockeyTeam;
    }
    
    // Going to set this as a nested attribute override on HockeyPlayer.
    //@Column(name="JERSEY_NUMBER")
    public int getJerseyNumber() {
        return jerseyNumber;
    }
    
    @OneToMany(cascade=PERSIST)
    @JoinTable(
        name="PLAYER_ROLES",
        joinColumns=@JoinColumn(name="PLAYER_ID"),
        inverseJoinColumns=@JoinColumn(name="ROLE_ID")
    )
    public List<Role> getRoles() {
        return roles;
    }
        
    public void setPosition(String position) {
        this.position = position;    
    }
    
    public void setHockeyTeam(HockeyTeam hockeyTeam) {
        this.hockeyTeam = hockeyTeam;
    }
    
    public void setJerseyNumber(int jerseyNumber) {
        this.jerseyNumber = jerseyNumber;
    }
    
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    
    public String toString() {
        return "Team Vitals: Team [" + getHockeyTeam().getName() + "], Position [" + getPosition() + "]";
    }
}
