/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.relationshipmaintenance;

public class FieldLocation {
    protected int id;
    public java.lang.String city;

    /**
     * FieldLocation constructor comment.
     */
    public FieldLocation() {
        super();
    }

    public static FieldLocation example1() {
        FieldLocation instance = new FieldLocation();
        instance.setCity("Ottawa");
        return instance;
    }

    public static FieldLocation example2() {
        FieldLocation instance = new FieldLocation();
        instance.setCity("Toronto");
        return instance;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 7:28:22 PM)
     * @return java.lang.String
     */
    public java.lang.String getCity() {
        return city;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 6:52:23 PM)
     * @return int
     */
    public int getId() {
        return id;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 7:28:22 PM)
     * @param newCity java.lang.String
     */
    public void setCity(java.lang.String newCity) {
        city = newCity;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 6:52:23 PM)
     * @param newId int
     */
    public void setId(int newId) {
        id = newId;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(this) + "(" + id + ", " + System.identityHashCode(this) + ")";
    }
}
