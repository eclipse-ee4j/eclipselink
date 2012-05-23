/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

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
