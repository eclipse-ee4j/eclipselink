/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     02/04/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.testing.models.jpa22.advanced.ddl.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converts;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.eclipse.persistence.testing.models.jpa22.advanced.converters.AccomplishmentConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.AgeConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.DateConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.DistanceConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.GenderConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.HealthConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.LevelConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.RunningStatusConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.TimeConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.enums.Gender;

import static jakarta.persistence.InheritanceType.JOINED;

@Entity
@Inheritance(strategy=JOINED)
@Table(name="JPA22_DDL_RUNNER", schema="RUNNER")
@Convert(attributeName = "accomplishments.key", converter = AccomplishmentConverter.class)
@Convert(attributeName = "accomplishments", converter = DateConverter.class)
@Convert(attributeName = "age", converter = AgeConverter.class)
public class Runner extends Athlete {
    @Id
    @GeneratedValue
    protected Integer id;

    @Convert(converter=GenderConverter.class)
    protected Gender gender;

    @Embedded
    @Convert(attributeName = "level", converter = LevelConverter.class)
    @Convert(attributeName = "health", converter = HealthConverter.class)
    @Convert(attributeName = "status.runningStatus", converter = RunningStatusConverter.class)
    protected RunnerInfo info;

    @OneToMany(mappedBy="runner")
    @MapKeyJoinColumn(
        name="TAG_ID",
        foreignKey=@ForeignKey(
            name="Runner_ShoeTag_Foreign_Key",
            foreignKeyDefinition="FOREIGN KEY (TAG_ID) REFERENCES JPA22_DDL_SHOE_TAG (ID)"
        )
    )
    protected Map<ShoeTag, Shoe> shoes;

    @ManyToMany
    @JoinTable(
        name="JPA22_DDL_RUNNERS_RACES",
        schema="RUNNER",
        joinColumns=@JoinColumn(
            name="RUNNER_ID",
            referencedColumnName="ID",
            foreignKey=@ForeignKey(
                name="Runners_Races_Foreign_Key",
                foreignKeyDefinition="FOREIGN KEY (RUNNER_ID) REFERENCES JPA22_DDL_RUNNER (ID)"
            )
        ),
        inverseJoinColumns=@JoinColumn(
            name="RACE_ID",
            referencedColumnName="ID",
            foreignKey=@ForeignKey(
                name="Runners_Races_Inverse_Foreign_Key",
                foreignKeyDefinition="FOREIGN KEY (RACE_ID) REFERENCES JPA22_DDL_RACE (ID)"
            )
        )
    )
    protected List<Race> races;

    @ElementCollection
    @Column(name="TIME")
    @MapKeyColumn(name="DISTANCE")
    @CollectionTable(
        name="JPA22_DDL_RUNNER_PBS",
        schema="RUNNER",
        joinColumns=@JoinColumn(
                name="RUNNER_ID",
                foreignKey=@ForeignKey(
                  name="Runner_PBS_Foreign_Key",
                  foreignKeyDefinition="FOREIGN KEY (RUNNER_ID) REFERENCES JPA22_DDL_RUNNER (ID)"
                ))
    )
    @Convert(attributeName="key", converter = DistanceConverter.class)
    @Convert(converter = TimeConverter.class)
    protected Map<String, String> personalBests;

    public Runner() {
        races = new ArrayList<>();
        personalBests = new HashMap<>();
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
