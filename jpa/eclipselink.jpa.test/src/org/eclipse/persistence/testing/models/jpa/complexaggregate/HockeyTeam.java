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
    private List<HockeyPlayer> players;
    
    public HockeyTeam() {
        players = new ArrayList();
    }
    
    @Column(name="AWAY_COLOR")
    public String getAwayColor() {
        return awayColor;    
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
    
    @OneToMany(mappedBy="hockeyTeam")
    public List<HockeyPlayer> getPlayers() {
        return players;
    }
    
    public void setAwayColor(String awayColor) {
        this.awayColor = awayColor;
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
