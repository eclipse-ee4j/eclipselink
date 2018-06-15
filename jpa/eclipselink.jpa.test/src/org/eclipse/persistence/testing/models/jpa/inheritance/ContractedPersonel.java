/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     12/12/2008-1.1 Guy Pelletier
//       - 249860: Implement table per class inheritance support.
package org.eclipse.persistence.testing.models.jpa.inheritance;

import static javax.persistence.GenerationType.TABLE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import static javax.persistence.InheritanceType.TABLE_PER_CLASS;

@Entity
@Table(name="TPC_PERSONEL")
@Inheritance(strategy=TABLE_PER_CLASS)
public class ContractedPersonel {
    @Id
    @GeneratedValue(strategy=TABLE, generator="PERSONEL_TABLE_GENERATOR")
    @TableGenerator(
        name="PERSONEL_TABLE_GENERATOR",
        table="CMP3_PERSONEL_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="PERSONEL_SEQ")
    private Integer id;
    private String name;

    @ManyToMany
    @JoinTable(
        name="TPC_PERSONEL_CLUB",
        joinColumns=@JoinColumn(name="PERSONEL_ID", referencedColumnName="ID"),
        inverseJoinColumns=@JoinColumn(name="CLUB_ID", referencedColumnName="ID")
    )
    private List<SocialClub> socialClubs = new ArrayList<SocialClub>();

    @ElementCollection
    @CollectionTable(
        name="TPC_NICKNAMES",
        joinColumns=@JoinColumn(name="PERSONEL_ID", referencedColumnName="ID")
    )
    @Column(name="NICKNAME")
    private List<String> nicknames = new ArrayList<String>();

    @Version
    private Integer version;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<SocialClub> getSocialClubs() {
        return socialClubs;
    }

    public Integer getVersion() {
        return version;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSocialClubs(List<SocialClub> socialClubs) {
        this.socialClubs = socialClubs;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<String> getNicknames() {
        return nicknames;
    }

    public void setNicknames(List<String> nicknames) {
        this.nicknames = nicknames;
    }
}

