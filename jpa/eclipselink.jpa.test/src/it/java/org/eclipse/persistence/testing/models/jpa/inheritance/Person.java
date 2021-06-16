/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink


package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.io.*;
import jakarta.persistence.*;
import org.eclipse.persistence.annotations.Cache;
import static jakarta.persistence.GenerationType.*;
import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.InheritanceType.*;

/**
 * This tests;
 * <ul>
 * <li> the init problem
 * <li> class name indicator usage
 * <li> concreate root class
 * <li> big int as primary key
 */
@Entity
@Table(name="CMP3_PERSON")
@Inheritance(strategy=JOINED)
@DiscriminatorValue("1")
@DiscriminatorColumn(discriminatorType=DiscriminatorType.INTEGER)
@Cache(expiry=100000)
public class Person implements Serializable {
    public Number id;
    public String name;
    public Car car;
    public Engineer bestFriend;
    public Lawyer representitive;

    @Id
    @GeneratedValue(strategy=TABLE, generator="PERSON_TABLE_GENERATOR")
    @TableGenerator(
        name="PERSON_TABLE_GENERATOR",
        table="CMP3_INHERITANCE_SEQ",
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

    @ManyToOne(cascade=PERSIST, fetch=LAZY, optional=false)
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

    @ManyToOne(cascade=PERSIST, fetch=LAZY, optional=false)
    @JoinColumn(name="FRIEND_ID")
    public Engineer getBestFriend() {
        return bestFriend;
    }

    public void setBestFriend(Engineer friend) {
        bestFriend = friend;
    }

    @ManyToOne(cascade=PERSIST, fetch=LAZY, optional=false)
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
