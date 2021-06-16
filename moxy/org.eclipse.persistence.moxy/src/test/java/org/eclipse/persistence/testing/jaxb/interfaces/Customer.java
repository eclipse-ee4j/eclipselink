/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     bdoughan - July 21/2010 - Initial implementation
package org.eclipse.persistence.testing.jaxb.interfaces;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement()
@XmlType(propOrder={"name", "address", "phoneNumbers","interfaceWithNoFactoryMethod","interfaceWithNoFactoryMethods"})
public interface Customer {

    String getName();
    void setName(String name);

    Address getAddress();
    void setAddress(Address address);

    @XmlElement(name="phone-number")
    List<PhoneNumber> getPhoneNumbers();
    void setPhoneNumbers(List<PhoneNumber> phoneNumbers);

    InterfaceWithNoFactoryMethod getInterfaceWithNoFactoryMethod();
    void setInterfaceWithNoFactoryMethod(InterfaceWithNoFactoryMethod arg);

    List<InterfaceWithNoFactoryMethod> getInterfaceWithNoFactoryMethods();
    void setInterfaceWithNoFactoryMethods(List<InterfaceWithNoFactoryMethod> arg);

}
