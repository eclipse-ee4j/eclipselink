/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - July 21/2010 - Initial implementation
package org.eclipse.persistence.testing.jaxb.interfaces;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
