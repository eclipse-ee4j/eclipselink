/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa21.advanced.inheritance;

import java.io.*;
import javax.persistence.*;
import org.eclipse.persistence.annotations.Cache;

/**
 * This tests;
 * <ul>
 * <li> the init problem
 * <li> class name indicator usage
 * <li> concreate root class
 * <li> big int as primary key
 */
@Entity
@Table(name="JPA21_PERSON")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorValue("1")
@DiscriminatorColumn(discriminatorType=DiscriminatorType.INTEGER)
@Cache(expiry=100000)
public class Person implements Serializable {
    private Number id;
    private String name;
    private Car car;
    private Engineer bestFriend;
    private Lawyer representitive;

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="PERSON_TABLE_GENERATOR")
    @TableGenerator(
        name="PERSON_TABLE_GENERATOR",
        table="JPA21_INHERITANCE_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="PERSON_SEQ"
    )
    @Column(name="ID")
    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }

    @ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="CAR_ID")
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Column(name="NAME", length=80)
    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }

    @ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="FRIEND_ID")
    public Engineer getBestFriend() {
        return bestFriend;
    }

    public void setBestFriend(Engineer friend) {
        bestFriend = friend;
    }

    @ManyToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="REP_ID", referencedColumnName="ID")
    public Lawyer getRepresentitive() {
        return representitive;
    }

    public void setRepresentitive(Lawyer representitive) {
        this.representitive = representitive;
    }

    public String toString() {
        return this.name;
    }
}
