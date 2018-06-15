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
// dmccann - Mar 2/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.schemamodelgenerator;

import java.util.Collection;

public class Address {
    public String id;                       // XMLDirectMapping - PK
    public String street;                   // XMLDirectMapping
    public String city;                     // XMLDirectMapping - PK
    public String country;                  // XMLDirectMapping
    public String postalCode;               // XMLDirectMapping
    public Object thingy;                   // XMLAnyObjectMapping
    public Collection<Employee> occupants;  // XMLCollectionReferenceMapping

    public Address() {}
}
