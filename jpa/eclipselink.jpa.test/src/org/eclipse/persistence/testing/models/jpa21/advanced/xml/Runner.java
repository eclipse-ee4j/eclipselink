/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     10/25/2012-2.5 Guy Pelletier 
 *       - 374688: JPA 2.1 Converter support
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa21.advanced.xml;

import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Gender;

import java.util.HashMap;
import java.util.Map;

public class Runner extends Athlete {
    protected Integer id;
    protected Gender gender;
    protected RunnerInfo info;
    protected Map<String, String> personalBests;

    public Runner() {
        personalBests = new HashMap<String, String>();
    }
    
    public void addPersonalBest(String distance, String time) {
        personalBests.put(distance, time);
    }
    
    public Gender getGender() { 
        return gender; 
    }
    
    public Integer getId() { 
        return id; 
    }
    
    public RunnerInfo getInfo() {
        return info;
    }
    
    public Map<String, String> getPersonalBests() {
        return personalBests;
    }
    
    public boolean isFemale() {
        return gender.equals(Gender.Female);
    }
    
    public boolean isMale() {
        return gender.equals(Gender.Male);
    }
    
    public void setGender(Gender gender) { 
        this.gender = gender; 
    }
    
    public void setInfo(RunnerInfo info) {
        this.info = info;
    }
    
    public void setIsFemale() {
        this.gender = Gender.Female;
    }
    
    public void setIsMale() {
        this.gender = Gender.Male;
    }
    
    public void setPersonalBests(Map<String, String> personalBests) {
        this.personalBests = personalBests;
    }
}
