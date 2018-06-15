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
//     Oracle - initial API and implementation from Oracle TopLink


package org.eclipse.persistence.testing.models.jpa.inheritance;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;

import javax.persistence.*;

@Entity
@EntityListeners(org.eclipse.persistence.testing.models.jpa.inheritance.listeners.SportsCarListener.class)
@Table(name="CMP3_SPORTS_CAR")
@DiscriminatorValue("SC")
@ExcludeDefaultListeners
public class SportsCar extends Car {
    public static int PRE_PERSIST_COUNT = 0;

    private int maxSpeed;
    private Person user;

    @Column(name="MAX_SPEED")
    public int getMaxSpeed() {
        return maxSpeed;
    }

    @PrePersist
    public void prePersist() {
        ++PRE_PERSIST_COUNT;
    }

    public void setMaxSpeed(int speed) {
        maxSpeed = speed;
    }

    @OneToOne(mappedBy ="car", fetch=LAZY)
    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }
}
