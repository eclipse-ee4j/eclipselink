/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;

public class CubiclePrimaryKeyClass implements Serializable {

    private static final long serialVersionUID = 1L;
    /* composite key example */
    protected Integer floor;
    protected Integer place;

    public CubiclePrimaryKeyClass() {
    }

    public CubiclePrimaryKeyClass(Integer aFloor, Integer aPlace) {
        floor = aFloor;
        place = aPlace;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer aFloor) {
        floor = aFloor;
    }

    public Integer getPlace() {
        return place;
    }

    public void setPlace(Integer aPlace) {
        place = aPlace;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof CubiclePrimaryKeyClass)) {
            return false;
        }

        CubiclePrimaryKeyClass otherKey = ((CubiclePrimaryKeyClass) other);
        if (floor.equals(otherKey.floor) && place.equals(otherKey.place)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return floor.hashCode() + 17 * place.hashCode();
    }

    @Override
    public String toString() {
        return "(floor = " + floor + ", place = " + place + ")";
    }
}
