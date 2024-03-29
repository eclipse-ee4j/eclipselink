/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     bdoughan - August 18/2009 - 1.2 - Initial implementation
package org.eclipse.persistence.testing.sdo.instanceclass;

import java.util.List;

public class CustomerClassWithCorrectGetters implements CustomerInterfaceWithCorrectGetters{

    private String name;
    private Address address;
    private List phoneNumber;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Address getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public List getPhoneNumber() {
        return this.phoneNumber;
    }

    @Override
    public void setPhoneNumber(List phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
