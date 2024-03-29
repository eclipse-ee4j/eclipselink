/*
 * Copyright (c) 2012, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     10/25/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     07/16/2013-2.5.1 Guy Pelletier
//       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
//     11/06/2014-2.6 Tomas Kraus
//       - 449818: Added mapping for Convert annotation test on ElementCollection of Embeddable class.
package org.eclipse.persistence.testing.models.jpa21.advanced;

import jakarta.persistence.Basic;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converts;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.AccomplishmentConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.AgeConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.CompetitionConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.DateConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.DistanceConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.GenderConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.HealthConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.LevelConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.RunningStatusConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.converters.TimeConverter;
import org.eclipse.persistence.testing.models.jpa21.advanced.enums.Gender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jakarta.persistence.InheritanceType.JOINED;

@Entity
@Inheritance(strategy=JOINED)
@Table(name="JPA21_RUNNER")
@NamedQueries({
    @NamedQuery(name="Runner.listAll", query="SELECT r FROM Runner r")
})
@NamedNativeQueries({
    @NamedNativeQuery(name="RunnerVictoryThis.getById",
        query="SELECT NAME, ID, COMPETITION, VDATE FROM JPA21_RUNNER_VTY WHERE RUNNER_ID=?"),
    @NamedNativeQuery(name="RunnerVictoryLast.getById",
        query="SELECT NAME, ID, COMPETITION, VDATE FROM JPA21_RUNNER_VLY WHERE RUNNER_ID=?")
})
@Convert(attributeName = "accomplishments.key", converter = AccomplishmentConverter.class)
@Convert(attributeName = "accomplishments", converter = DateConverter.class)
@Convert(attributeName = "age", converter = AgeConverter.class)
public class Runner extends Athlete {
    @Id
    @GeneratedValue
    protected Integer id;

    @Basic
    protected List<RunnerTag> tags;

    @Basic
    protected List<String> serials;

    @Convert(converter=GenderConverter.class)
    protected Gender gender;

    @Embedded
    @Converts({
        @Convert(attributeName = "level", converter = LevelConverter.class),
        @Convert(attributeName = "health", converter = HealthConverter.class),
        @Convert(attributeName = "status.runningStatus", converter = RunningStatusConverter.class)
    })
    protected RunnerInfo info;

    @OneToMany(mappedBy="runner")
    @MapKeyJoinColumn(name="TAG_ID")
    protected Map<ShoeTag, Shoe> shoes;

    @ManyToMany
    @JoinTable(
        name="JPA21_RUNNERS_RACES",
        joinColumns=@JoinColumn(
            name="RUNNER_ID",
            referencedColumnName="ID"
        ),
        inverseJoinColumns=@JoinColumn(
            name="RACE_ID",
            referencedColumnName="ID"
        )
    )
    protected List<Race> races;

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

    // Only this collection shall contain values modified by CompetitionConverter.
    @Convert(converter=CompetitionConverter.class, attributeName = "value.competition")
    @ElementCollection
    @CollectionTable(name="JPA21_RUNNER_VTY", joinColumns=@JoinColumn(name="RUNNER_ID"))
    @MapKeyColumn(name="NAME")
    private Map <String, RunnerVictory> victoriesThisYear;

    // This collection shall contain values unmodified.
    @ElementCollection
    @CollectionTable(name="JPA21_RUNNER_VLY", joinColumns=@JoinColumn(name="RUNNER_ID"))
    @MapKeyColumn(name="NAME")
    private Map <String, RunnerVictory> victoriesLastYear;

    public Runner() {
        races = new ArrayList<>();
        personalBests = new HashMap<>();
        shoes = new HashMap<>();
        tags = new ArrayList<>();
    }

    public void addPersonalBest(String distance, String time) {
        personalBests.put(distance, time);
    }

    public void addRace(Race race) {
        races.add(race);
    }

    public void addSerial(String serial) {
        serials.add(serial);
    }

    public void addTag(String tag) {
        tags.add(new RunnerTag(tag));
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

    public List<String> getSerials() {
        return serials;
    }

    public Map<ShoeTag, Shoe> getShoes() {
        return shoes;
    }

    public List<RunnerTag> getTags() {
        return tags;
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

    public void setSerials(List<String> serials) {
        this.serials = serials;
    }

    public void setShoes(Map<ShoeTag, Shoe> shoes) {
        this.shoes = shoes;
    }

    public void setTags(List<RunnerTag> tags) {
        this.tags = tags;
    }

    public Map <String, RunnerVictory> getVictoriesThisYear() {
        return victoriesThisYear;
    }

    public void setVictoriesThisYear(Map <String, RunnerVictory> victoriesThisYear) {
        this.victoriesThisYear = victoriesThisYear;
    }

    public Map <String, RunnerVictory> getVictoriesLastYear() {
        return victoriesLastYear;
    }

    public void setVictoriesLastYear(Map <String, RunnerVictory> victoriesLastYear) {
        this.victoriesLastYear = victoriesLastYear;
    }
}
