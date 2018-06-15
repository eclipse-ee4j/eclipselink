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

package org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk;

public class ScientistPK {
    public int idNumber;
    public String firstName;
    public String lastName;

    public ScientistPK(int idNumber, String firstName, String lastName) {
        this.idNumber = idNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean equals(Object other) {
        if (other instanceof ScientistPK) {
            final ScientistPK otherScientistPK = (ScientistPK) other;
            return (otherScientistPK.firstName.equals(firstName) && otherScientistPK.lastName.equals(lastName) && otherScientistPK.idNumber == idNumber);
        }

        return false;
    }
}
