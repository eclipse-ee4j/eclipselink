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

import jakarta.persistence.Basic;
import jakarta.persistence.Embeddable;

@Embeddable
public final class PatentId implements Serializable {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -1363598159819681503L;
    private int year;
    private String name;

    @Basic
    public String getName() {
        return name;
    }

    public void setName(final String aName) {
        name = aName;
    }

    @Basic
    public int getYear() {
        return year;
    }

    public void setYear(final int aYear) {
        year = aYear;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PatentId)) {
            return false;
        }
        final PatentId other = (PatentId) obj;
        if (other.year != year) {
            return false;
        }
        if (name == null) {
            return other.name == null;
        }
        return year == other.year && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        final int nullNameHashCode = 17 + 37 * year;
        if (null == name) {
            return nullNameHashCode;
        }
        return name.hashCode() + nullNameHashCode;
    }

    @Override
    public String toString() {
        return year + "-" + name;
    }
}
