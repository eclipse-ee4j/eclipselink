/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

public class ContactMethod {
    private String id;

    public ContactMethod() {}

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean equals(Object theContactMethod) {
        if (theContactMethod instanceof ContactMethod) {
            return id.equals(((ContactMethod)theContactMethod).getId());
        }
        return false;
    }

    public String toString() {
        return "ContactMethod: " + id;
    }
}
