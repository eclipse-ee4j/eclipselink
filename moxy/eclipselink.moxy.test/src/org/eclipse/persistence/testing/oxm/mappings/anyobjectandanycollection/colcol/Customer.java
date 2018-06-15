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
package org.eclipse.persistence.testing.oxm.mappings.anyobjectandanycollection.colcol;

import java.util.Vector;

public class Customer {
    private Vector anyObject;
    private Vector contactMethods;

    public Customer() {
        super();
    }

    public String toString() {
        return "Customer[" + "\n\tContactMethods: " + getContactMethods() + " anyObject: " + getAnyObject() + "]";
    }

    public Vector getAnyObject() {
        return anyObject;
    }

    public void setAnyObject(Vector anyObject) {
        this.anyObject = anyObject;
    }

    public Vector getContactMethods() {
        return contactMethods;
    }

    public void setContactMethods(Vector contactMethods) {
        this.contactMethods = contactMethods;
    }

    public boolean equals(Object object) {
        if (object instanceof Customer) {
            Customer anObject = (Customer)object;

            // check all null case 0000
            if ((anyObject == null) && (anObject.getAnyObject() == null) &&//
                    (contactMethods == null) && (anObject.getContactMethods() == null)) {
                return true;
            }

            // check case 0101
            if ((anyObject == null) && (anObject.getAnyObject() == null) &&//
                    (contactMethods != null) && (anObject.getContactMethods() != null)) {
                return contactMethods.equals(anObject.getContactMethods());
            }

            // check case 1010
            if ((anyObject != null) && (anObject.getAnyObject() != null) &&//
                    (contactMethods == null) && (anObject.getContactMethods() == null)) {
                return anyObject.equals(anObject.getAnyObject());
            }

            // check case 1111
            if ((anyObject != null) && (anObject.getAnyObject() != null) &&//
                    (contactMethods != null) && (anObject.getContactMethods() != null)) {
                if (anyObject.equals(anObject.getAnyObject())) {
                    return contactMethods.equals(anObject.getContactMethods());
                } else {
                    return false;
                }
            }
            return false;
        } else {
            return false;
        }
    }
}
