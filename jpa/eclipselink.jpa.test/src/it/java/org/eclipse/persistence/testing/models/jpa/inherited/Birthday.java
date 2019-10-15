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
//     04/03/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Embeddable;

@Embeddable
public class Birthday {
    private Integer day;
    private Integer year;
    private Integer month;

    public Birthday() {}

    public boolean equals(Object object) {
        if (object instanceof Birthday) {
            Birthday birthday = (Birthday) object;

            if (! getDay().equals(birthday.getDay())) {
                return false;
            }

            if (! getYear().equals(birthday.getYear())) {
                return false;
            }

            return getMonth().equals(birthday.getMonth());
        }

        return false;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getYear() {
        return year;
    }

    public int hashCode() {
        String hc = year.toString() + month.toString() + day.toString();
        return new Integer(hc).intValue();
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
