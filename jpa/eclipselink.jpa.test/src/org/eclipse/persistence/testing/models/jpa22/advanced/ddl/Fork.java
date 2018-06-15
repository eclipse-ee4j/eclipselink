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
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
package org.eclipse.persistence.testing.models.jpa22.advanced.ddl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

@Entity
@Table(
    name="JPA22_FORK",
    indexes=@Index(
        // default the name to JPA22_FORK_INDEX_STYLE_COLOR
        columnList="STYLE, COLOR"
    )
)
@SecondaryTable(
    name="JPA22_FORK_COSTS",
    indexes=@Index(
        name="FORK_COSTS_INDEX",
        columnList="COST, RENTAL_COST"
    )
)
public class Fork extends Utensil {
    @Column(name="COST", table="JPA22_FORK_COSTS")
    public Double price;

    @Column(name="RENTAL_COST", table="JPA22_FORK_COSTS")
    public Double rental;

    public String style;
    public String color;

    @ElementCollection
    @CollectionTable(
        name="JPA22_FORK_USES",
        indexes=@Index(
            name="JPA22_FORK_USES_INDEX",
            columnList="DESCRIP"
        )
    )
    @Column(name="DESCRIP")
    public List<String> uses;

    @ManyToMany
    @JoinTable(
        name="JPA22_FORK_FORK_USERS",
        joinColumns=@JoinColumn(name="FORK_ID"),
        inverseJoinColumns=@JoinColumn(name="FORK_USER_ID"),
        indexes=@Index(
            name="JPA22_FORK_USERS_INDEX",
            columnList="FORK_USER_ID, FORK_ID"
         )
    )
    public List<ForkUser> users;

    public Fork() {
        users = new ArrayList<>();
    }

    public void addUser(ForkUser user) {
        users.add(user);
    }

    public String getColor() {
        return color;
    }

    public Double getPrice() {
        return price;
    }

    public Double getRental() {
        return rental;
    }

    public String getStyle() {
        return style;
    }

    public List<ForkUser> getUsers() {
        return users;
    }

    public List<String> getUses() {
        return uses;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setRental(Double rental) {
        this.rental = rental;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setUsers(List<ForkUser> users) {
        this.users = users;
    }

    public void setUses(List<String> uses) {
        this.uses = uses;
    }
}
