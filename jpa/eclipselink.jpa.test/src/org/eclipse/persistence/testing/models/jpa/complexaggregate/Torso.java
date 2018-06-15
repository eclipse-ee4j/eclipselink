/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     12/17/2010-2.2 Guy Pelletier
//       - 330755: Nested embeddables can't be used as embedded ids
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;

@Embeddable
public class Torso {
    @GeneratedValue
    @Column(name="BODY_COUNT")
    public int count;

    @Embedded
    public Heart heart;

    public Torso() {}

    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof Torso) {
            Torso torso = (Torso) objectToCompare;

            if (count != torso.getCount()) {
                return false;
            }

            return heart.equals(torso.getHeart());
        }

        return false;
    }

    public Torso(int count, Heart heart) {
        this.count = count;
        this.heart = heart;
    }

    public int getCount() {
        return count;
    }

    public Heart getHeart() {
        return heart;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setHeart(Heart heart) {
        this.heart = heart;
    }

}
