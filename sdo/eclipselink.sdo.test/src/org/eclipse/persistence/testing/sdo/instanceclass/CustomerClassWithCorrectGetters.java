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
*     bdoughan - August 18/2009 - 1.2 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.instanceclass;

import java.util.List;

public class CustomerClassWithCorrectGetters implements CustomerInterfaceWithCorrectGetters{

    private String name;
    private Address address;
    private List phoneNumber;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(List phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}