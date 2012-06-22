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
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

public class CanadianAddress extends Address {
    private String postalCode;

    public CanadianAddress() {}

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public boolean equals(Object theAddress) {
        if (super.equals(theAddress) && theAddress instanceof CanadianAddress) {
            return postalCode.equals(((CanadianAddress)theAddress).getPostalCode());
        }
        return false;
    }

    public String toString() {
        return super.toString() + " postalCode:" + postalCode;
    }
}
