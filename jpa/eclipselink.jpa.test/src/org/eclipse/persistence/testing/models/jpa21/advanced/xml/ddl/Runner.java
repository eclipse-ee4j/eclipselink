/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converts;
import javax.persistence.ElementCollection;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

import org.eclipse.persistence.testing.models.jpa21.advanced.converters.DistanceConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.TimeConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Gender;

public class Runner extends Athlete {
    protected Integer id;
    protected Gender gender;
    protected RunnerInfo info;
    protected List<Race> races;
    protected Map<ShoeTag, Shoe> shoes;

    // This is mapped here until the JPA schema is corrected. Currently
    // cannot specify a convert with a column in JPA (with XML validation on)
    @ElementCollection
    @Column(name="TIME")
    @MapKeyColumn(name="DISTANCE")
    @CollectionTable(
        name="JPA21_XML_DDL_RUNNER_PBS",
        joinColumns=@JoinColumn(name="RUNNER_ID"),
        foreignKey=@ForeignKey(
                name="FK_JPA21_XML_Runner_PBS",
                foreignKeyDefinition="FOREIGN KEY (RUNNER_ID) REFERENCES JPA21_XML_DDL_RUNNER (ID)")
    )
    @Converts({
        @Convert(attributeName="key", converter = DistanceConverter.class),
        @Convert(converter = TimeConverter.class)
    })
    protected Map<String, String> personalBests;

    public Runner() {
        races = new ArrayList<Race>();
        personalBests = new HashMap<String, String>();
    }

    public void addPersonalBest(String distance, String time) {
        personalBests.put(distance, time);
    }

    public void addRace(Race race) {
        races.add(race);
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

    public List<Race> getRaces() {
        return races;
    }

    public Map<ShoeTag, Shoe> getShoes() {
        return shoes;
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

    public void setRaces(List<Race> races) {
        this.races = races;
    }

    public void setShoes(Map<ShoeTag, Shoe> shoes) {
        this.shoes = shoes;
    }
}
