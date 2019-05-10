/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     10/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     12/07/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.testing.models.jpa21.advanced;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
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
        name="JPA21_RUNNER_ACS",
        joinColumns=@JoinColumn(name="ATHLETE_ID")
    )
    // Sub class (Runner) will add convert to both key and value
    protected Map<String, Date> accomplishments;

    @ElementCollection
    @Column(name="ENDORSEMENT")
    @CollectionTable(
        name="JPA21_ENDORSEMENTS",
        joinColumns=@JoinColumn(name="ATHLETE_ID")
    )
    @MapKeyJoinColumn(name="ENDORSER_ID")
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
