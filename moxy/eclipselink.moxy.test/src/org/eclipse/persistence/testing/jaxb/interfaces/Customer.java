/*******************************************************************************
* Copyright (c) 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - July 21/2010 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.interfaces;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement()
@XmlType(propOrder={"name", "address", "phoneNumbers"})
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