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
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     02/20/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.testing.models.jpa21.advanced.ddl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.MapKeyTemporal;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;

import static javax.persistence.TemporalType.DATE;

@MappedSuperclass
public class Athlete {
    protected Integer age;

    @Column(name="F_NAME")
    protected String firstName;

    @Column(name="L_NAME")
    protected String lastName;

    @ElementCollection
    @Column(name="THE_DATE")
    @Temporal(DATE)
    @MapKeyColumn(name="ACCOMPLISHMENT")
    @CollectionTable(
        name="JPA21_DDL_RUNNER_ACS",
        joinColumns=@JoinColumn(
            name="ATHLETE_ID"),
        foreignKey=@ForeignKey(
            name="Accomplistments_Foreign_Key",
            foreignKeyDefinition="FOREIGN KEY (ATHLETE_ID) REFERENCES JPA21_DDL_RUNNER (ID)"
        )
    )
    // Sub class (Runner) will add convert to both key and value
    protected Map<String, Date> accomplishments;

    @ElementCollection
    @Column(name="ENDORSEMENT")
    @CollectionTable(
        name="JPA21_DDL_ENDORSEMENTS",
        joinColumns=@JoinColumn(
            name="ATHLETE_ID"),
        foreignKey=@ForeignKey(
            name="Endorsements_Foreign_Key",
            foreignKeyDefinition="FOREIGN KEY (ATHLETE_ID) REFERENCES JPA21_DDL_RUNNER (ID)"
        )
    )
    @MapKeyJoinColumn(
        name="ENDORSER_ID",
        foreignKey=@ForeignKey(
            name="Endorsements_Key_Foreign_Key",
            foreignKeyDefinition="FOREIGN KEY (ENDORSER_ID) REFERENCES JPA_DDL_ENDORSER (ID)"
        )
    )
    protected Map<Endorser, Integer> endorsements;

    public Athlete() {
        accomplishments = new HashMap<String, Date>();
        endorsements = new HashMap<Endorser, Integer>();
    }

    public void addAccomplishment(String accomplishment, Date date) {
        accomplishments.put(accomplishment, date);
    }

    public Map<String, Date> getAccomplishments() {
        return accomplishments;
    }

    public Integer getAge() {
        return age;
    }

    public Map<Endorser, Integer> getEndorsements() {
        return endorsements;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setAccomplishments(Map<String, Date> accomplishments) {
        this.accomplishments = accomplishments;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setEndorsements(Map<Endorser, Integer> endorsements) {
        this.endorsements = endorsements;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
