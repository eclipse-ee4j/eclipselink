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
 *       - 3746888: JPA 2.1 Converter support
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa21.advanced;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.eclipse.persistence.testing.models.jpa21.advanced.converters.AgeConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.DistanceConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.GenderConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.HealthConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.LevelConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.RunningStatusConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.TimeConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Gender;

@Entity
@Table(name="JPA21_RUNNER")
@Convert(attributeName = "age", converter = AgeConverter.class)
public class Runner extends Athlete {
    @Id
    @GeneratedValue
    protected Integer id;
    
    @Convert(converter=GenderConverter.class)
    protected Gender gender;
    
    @Embedded
    @Converts({
        @Convert(attributeName = "level", converter = LevelConverter.class),
        @Convert(attributeName = "health", converter = HealthConverter.class),
        @Convert(attributeName = "status.runningStatus", converter = RunningStatusConverter.class)
    })
    protected RunnerInfo info;
    
    @ElementCollection
    @Column(name="TIME")
    @MapKeyColumn(name="DISTANCE")
    @CollectionTable(
        name="JPA21_RUNNER_PBS",
        joinColumns=@JoinColumn(name="RUNNER_ID")
    )
    @Converts({
        @Convert(attributeName="key", converter = DistanceConverter.class),
        @Convert(converter = TimeConverter.class)
    })
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
