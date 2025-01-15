/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     12/12/2008-1.1 Guy Pelletier
//       - 249860: Implement table per class inheritance support.
package org.eclipse.persistence.testing.models.jpa.inheritance;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.TABLE;

@Entity
@Table(name="TPC_SOCIAL_CLUB")
//@Inheritance(strategy=TABLE_PER_CLASS)
public class SocialClub {
    @Id
    @GeneratedValue(strategy=TABLE, generator="SOCIAL_CLUB_TABLE_GENERATOR")
    @TableGenerator(
        name="SOCIAL_CLUB_TABLE_GENERATOR",
        table="CMP3_SOCIAL_CLUB_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="SOCIAL_CLUB_SEQ")
    private Integer id;

    private String name;

    @ManyToMany(mappedBy="socialClubs")
    private List<ContractedPersonel> members;

    public SocialClub() {
        members = new ArrayList<>();
    }

    public void addMember(ContractedPersonel member) {
        getMembers().add(member);
        member.getSocialClubs().add(this);
    }

    public Integer getId() {
        return id;
    }

    public List<ContractedPersonel> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMembers(List<ContractedPersonel> members) {
        this.members = members;
    }

    public void setName(String name) {
        this.name = name;
    }
}
