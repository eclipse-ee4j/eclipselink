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
package org.eclipse.persistence.testing.models.jpa22.advanced;

import static javax.persistence.InheritanceType.JOINED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.testing.models.jpa22.advanced.converters.AccomplishmentConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.AgeConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.CompetitionConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.DateConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.DistanceConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.GenderConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.HealthConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.LevelConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.RunningStatusConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.converters.TimeConverter;
import org.eclipse.persistence.testing.models.jpa22.advanced.enums.Gender;

@Entity
@Inheritance(strategy=JOINED)
@Table(name="JPA22_RUNNER")
@NamedQuery(name="Runner.listAll", query="SELECT r FROM Runner r")
@NamedNativeQuery(name="RunnerVictoryThis.getById",
    query="SELECT NAME, ID, COMPETITION, VDATE FROM JPA22_RUNNER_VTY WHERE RUNNER_ID=?")
@NamedNativeQuery(name="RunnerVictoryLast.getById",
    query="SELECT NAME, ID, COMPETITION, VDATE FROM JPA22_RUNNER_VLY WHERE RUNNER_ID=?")
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
    @Convert(attributeName = "level", converter = LevelConverter.class)
    @Convert(attributeName = "health", converter = HealthConverter.class)
    @Convert(attributeName = "status.runningStatus", converter = RunningStatusConverter.class)
    protected RunnerInfo info;

    @OneToMany(mappedBy="runner")
    @MapKeyJoinColumn(name="TAG_ID")
    protected Map<ShoeTag, Shoe> shoes;

    @ManyToMany
    @JoinTable(
        name="JPA22_RUNNERS_RACES",
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
        name="JPA22_RUNNER_PBS",
        joinColumns=@JoinColumn(name="RUNNER_ID")
    )
    @Convert(attributeName="key", converter = DistanceConverter.class)
    @Convert(converter = TimeConverter.class)
    protected Map<String, String> personalBests;

    // Only this collection shall contain values modified by CompetitionConverter.
    @Convert(converter=CompetitionConverter.class, attributeName = "value.competition")
    @ElementCollection
    @CollectionTable(name="JPA22_RUNNER_VTY", joinColumns=@JoinColumn(name="RUNNER_ID"))
    @MapKeyColumn(name="NAME")
    private Map <String, RunnerVictory> victoriesThisYear;

    // This collection shall contain values unmodified.
    @ElementCollection
    @CollectionTable(name="JPA22_RUNNER_VLY", joinColumns=@JoinColumn(name="RUNNER_ID"))
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
