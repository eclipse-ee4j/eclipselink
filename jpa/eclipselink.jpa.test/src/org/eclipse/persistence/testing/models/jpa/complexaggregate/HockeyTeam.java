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
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import java.util.List;
import java.util.ArrayList;
import javax.persistence.*;

import java.io.Serializable;
import static javax.persistence.GenerationType.*;

@Entity
@Table(name="CMP3_HOCKEY_TEAM")
public class HockeyTeam implements Serializable {
    private int id;
    private String name;
    private String level;
    private String homeColor;
    private String awayColor;
    private List<HockeyCoach> coaches;
    private List<HockeyPlayer> players;
    
    public HockeyTeam() {
        coaches = new ArrayList<HockeyCoach>();
        players = new ArrayList<HockeyPlayer>();
    }
    
    @Column(name="AWAY_COLOR")
    public String getAwayColor() {
        return awayColor;    
    }
    
    // This is a mapping within an embeddable. Test finding it using the
    // dot notation qualification.
    @OneToMany(mappedBy="vitals.hockeyTeam")
    @OrderBy("vitals.personalVitals.age DESC")
    public List<HockeyCoach> getCoaches() {
        return coaches;
    }
    
    @Column(name="HOME_COLOR")
    public String getHomeColor() {
        return homeColor;
    }
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="HOCKEY_TEAM_TABLE_GENERATOR")
	@TableGenerator(
        name="HOCKEY_TEAM_TABLE_GENERATOR", 
        table="CMP3_HOCKEY_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="HOCKEY_TEAM_SEQ"
    )   
    @Column(name="ID")
    public int getId() { 
        return id; 
    }

    @Column(name="TEAM_LEVEL")
    public String getLevel() {
        return level;
    }
    
    @Column(name="NAME")
    public String getName() {
        return name;
    }
    
    // This is a mapping within an embeddable. Test finding it using NO
    // dot notation qualification.
    @OneToMany(mappedBy="hockeyTeam")
    public List<HockeyPlayer> getPlayers() {
        return players;
    }
    
    public void setAwayColor(String awayColor) {
        this.awayColor = awayColor;
    }
    
    public void setCoaches(List<HockeyCoach> coaches) {
        this.coaches = coaches;
    }
    
    public void setHomeColor(String homeColor) {
        this.homeColor = homeColor;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPlayers(List<HockeyPlayer> players) {
        this.players = players;
    }
    
    public String toString() {
        return "Hockey team: " + getName();
    }
}
