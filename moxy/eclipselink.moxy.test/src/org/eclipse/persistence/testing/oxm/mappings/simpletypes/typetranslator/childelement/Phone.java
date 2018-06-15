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
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.typetranslator.childelement;

public class Phone  {
    private Object number;

    public Phone() {}

    public Phone(Object newNumber) {
        number = newNumber;
    }

    public Object getNumber() {
        return number;
    }

    public void setNumber(Object newNumber) {
        number = newNumber;
    }

    public String toString() {
        return "Phone(number=" + ((Integer)number).intValue() + ")";
    }

    public boolean equals(Object object) {
        if(!(object instanceof Phone)) {
            return false;
        }

        if (!(((Phone)object).getNumber().equals(this.getNumber()))) {
            return false;
        }

        return true;
    }
}
