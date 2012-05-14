/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     14/05/2012-2.4 Guy Pelletier  
 *       - 376603: Provide for table per tenant support for multitenant applications
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantTableDiscriminator;

import static org.eclipse.persistence.annotations.MultitenantType.TABLE_PER_TENANT;
import static org.eclipse.persistence.annotations.TenantTableDiscriminatorType.SUFFIX;

@Entity
@Table(name="JPA_CANDIDATE")
@Multitenant(TABLE_PER_TENANT)
@TenantTableDiscriminator(type=SUFFIX)
@SecondaryTable(name="JPA_CANDY_SALARY")
public class Candidate {
    @Id
    @GeneratedValue
    public long id;
    
    @Column(table="JPA_CANDY_SALARY")
    public int salary;
    
    public String name;
    
    @ManyToOne
    public Riding riding;
    
    @ManyToOne
    public Party party;
    
    @ManyToMany
    @JoinTable(
        name="JPA_CAN_SUP",
        joinColumns=@JoinColumn(name="CANDIDATE_ID"),
        inverseJoinColumns=@JoinColumn(name="SUPPORTER_ID")
    )
    public List<Supporter> supporters;
    
    @ElementCollection
    @CollectionTable(name="JPA_CANDIDATE_HONORS")
    @Column(name="HONOR")
    public List<String> honors;
    
    public Candidate() {
        honors = new ArrayList<String>();
        supporters = new ArrayList<Supporter>();
    }
    
    public void addHonor(String honor) {
        honors.add(honor);
    }
    
    public void addSupporter(Supporter supporter) {
        supporters.add(supporter);
        supporter.addSupportedCandidate(this);
    }

    public List<String> getHonors() {
        return honors;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public Party getParty() {
        return party;
    }
    
    public Riding getRiding() {
        return riding;
    }

    public int getSalary() { 
        return salary; 
    }
    
    public List<Supporter> getSupporters() {
        return supporters;
    }
    
    public void setHonors(List<String> honors) {
        this.honors = honors;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setParty(Party party) {
        this.party = party;
    }
    
    public void setRiding(Riding riding) {
        this.riding = riding;
    }

    public void setSalary(int salary) { 
        this.salary = salary; 
    }
    
    public void setSupporters(List<Supporter> supporters) {
        this.supporters = supporters;
    }
}
