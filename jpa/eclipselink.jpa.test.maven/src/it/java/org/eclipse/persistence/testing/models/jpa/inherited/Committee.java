/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     06/02/2009-2.0 Guy Pelletier
//       - 278768: JPA 2.0 Association Override Join Table
//     09/16/2010-2.2 Guy Pelletier
//       - 283028: Add support for letting an @Embeddable extend a @MappedSuperclass
package org.eclipse.persistence.testing.models.jpa.inherited;

import static javax.persistence.GenerationType.TABLE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity(name="JPA_COMMITTEE")
@Table(name="JPA_COMMITTEE")
public class Committee {
    private Integer id;
    private String description;
    private CommitteeDates committeeDates;

    private List<ExpertBeerConsumer> expertBeerConsumers;
    private List<NoviceBeerConsumer> noviceBeerConsumers;

    public Committee() {
        expertBeerConsumers = new ArrayList<ExpertBeerConsumer>();
        noviceBeerConsumers = new ArrayList<NoviceBeerConsumer>();
    }

    protected void addExpertBeerConsumer(ExpertBeerConsumer expertBeerConsumer) {
        expertBeerConsumers.add(expertBeerConsumer);
    }

    protected void addNoviceBeerConsumer(NoviceBeerConsumer noviceBeerConsumer) {
        noviceBeerConsumers.add(noviceBeerConsumer);
    }

    @Embedded
    public CommitteeDates getCommitteeDates() {
        return committeeDates;
    }

    @ManyToMany(mappedBy="committees")
    public List<ExpertBeerConsumer> getExpertBeerConsumers() {
        return expertBeerConsumers;
    }

    @ManyToMany(mappedBy="committees")
    public List<NoviceBeerConsumer> getNoviceBeerConsumers() {
        return noviceBeerConsumers;
    }

    @Column(name="DESCRIP")
    public String getDescription() {
        return description;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="COMMITTEE_TABLE_GENERATOR")
    @TableGenerator(
        name="COMMITTEE_TABLE_GENERATOR",
        table="CMP3_BEER_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="COMMITTEE_SEQ")
    public Integer getId() {
        return id;
    }

    public void setCommitteeDates(CommitteeDates committeeDates) {
        this.committeeDates = committeeDates;
    }

    public void setExpertBeerConsumers(List<ExpertBeerConsumer> expertBeerConsumers) {
        this.expertBeerConsumers = expertBeerConsumers;
    }

    public void setNoviceBeerConsumers(List<NoviceBeerConsumer> noviceBeerConsumers) {
        this.noviceBeerConsumers = noviceBeerConsumers;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
