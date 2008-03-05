/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.*;
import java.io.Serializable;
import static javax.persistence.FetchType.*;

@Embeddable
public class TeamVitals implements Serializable {
    private String position;
    private int jerseyNumber;
    private HockeyTeam hockeyTeam;
    
    public TeamVitals() {}

    @Column(name="POSITION")
    public String getPosition() {
        return position;
    }
    
    @OneToOne(fetch=EAGER)
	@JoinColumn(name="TEAM_ID", referencedColumnName="ID")    
    public HockeyTeam getHockeyTeam() {
        return hockeyTeam;
    }
    
    @Column(name="JERSEY_NUMBER")
    public int getJerseyNumber() {
        return jerseyNumber;
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
    
    public String toString() {
        return "Team Vitals: Team [" + getHockeyTeam().getName() + "], Position [" + getPosition() + "]";
    }
}
