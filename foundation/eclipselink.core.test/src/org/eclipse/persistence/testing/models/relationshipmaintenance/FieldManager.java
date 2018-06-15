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
package org.eclipse.persistence.testing.models.relationshipmaintenance;

import org.eclipse.persistence.indirection.*;

public class FieldManager {
    public ValueHolderInterface office;
    public int id;
    public java.lang.String name;

    public FieldManager() {
        office = new ValueHolder();
    }

    public static FieldManager example1() {
        FieldManager instance = new FieldManager();
        instance.setName("Guy Deschamps");
        return instance;
    }

    public static FieldManager example2() {
        FieldManager instance = new FieldManager();
        instance.setName("Joe Fielder");
        return instance;
    }

    public int getId() {
        return id;
    }

    public java.lang.String getName() {
        return name;
    }

    public FieldOffice getOffice() {
        return (FieldOffice)office.getValue();
    }

    public void setId(int newId) {
        id = newId;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 7:28:47 PM)
     * @param newName java.lang.String
     */
    public void setName(java.lang.String newName) {
        name = newName;
    }

    public void setOffice(FieldOffice newOffice) {
        office.setValue(newOffice);
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(this) + "(" + id + ", " + System.identityHashCode(this) + ")";
    }
}
