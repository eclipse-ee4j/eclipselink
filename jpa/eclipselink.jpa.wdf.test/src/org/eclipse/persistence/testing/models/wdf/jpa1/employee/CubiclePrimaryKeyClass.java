/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
