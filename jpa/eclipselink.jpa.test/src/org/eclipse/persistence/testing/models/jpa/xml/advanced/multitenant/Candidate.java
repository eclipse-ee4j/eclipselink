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
package org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant;

import java.util.ArrayList;
import java.util.List;

public class Candidate {
    public long id;
    public int salary;
    public String name;
    public Riding riding;
    public Party party;
    public List<Supporter> supporters;
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
