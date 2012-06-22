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
package org.eclipse.persistence.testing.oxm.inheritance.typetests.compositecollection;

import java.util.List;

public class Dependant {
    private List addresses;

    public Dependant() {
    }

    public void setAddresses(List theAddresses) {
        this.addresses = theAddresses;
    }

    public List getAddresses() {
        return addresses;
    }

    public boolean equals(Object theDependant) {
        if (theDependant instanceof Dependant) {
            return addresses.equals(((Dependant)theDependant).getAddresses());
        }
        return false;
    }

    public String toString() {
        return "Dependant: " + addresses.toString();
    }
}
