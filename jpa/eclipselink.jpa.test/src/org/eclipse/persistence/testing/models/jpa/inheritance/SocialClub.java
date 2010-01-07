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
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.inheritance;

import static javax.persistence.GenerationType.TABLE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name="TPC_SOCIAL_CLUB")
//@Inheritance(strategy=TABLE_PER_CLASS)
public class SocialClub {
    @Id
    @GeneratedValue(strategy=TABLE, generator="SOCIAL_CLUB_TABLE_GENERATOR")
    @TableGenerator(
        name="SOCIAL_CLUB_TABLE_GENERATOR", 
        table="CMP3_SOCIAL_CLUB_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="SOCIAL_CLUB_SEQ")
    private Integer id;
    
    private String name;
    
    @ManyToMany(mappedBy="socialClubs")
    private List<ContractedPersonel> members;
    
    public SocialClub() {
        members = new ArrayList<ContractedPersonel>();
    }
    
    public void addMember(ContractedPersonel member) {
        getMembers().add(member);
        member.getSocialClubs().add(this);
    }
    
    public Integer getId() {
        return id;
    }
    
    public List<ContractedPersonel> getMembers() {
        return members;
    }
    
    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setMembers(List<ContractedPersonel> members) {
        this.members = members;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
