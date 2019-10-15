/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - Mar 2/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.schemamodelgenerator;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.activation.DataHandler;

public class Employee {
    public String name;                             // XMLDirectMapping - PK
    public Address address;                         // XMLCompositeObjectMapping
    public Address billingAddress;                  // XMLObjectReferenceMapping
    public Collection<PhoneNumber> phoneNumbers;    // XMLCompositeCollectionMapping
    public Collection<Number> projectIDs;           // XMLDirectCollectionMapping
    public Collection<Object> stuff;                // XMLAnyCollectionMapping
    public Object choice;                           // XMLChoiceObjectMapping
    public Collection<Object> choices;              // XMLChoiceCollectionMapping
    public DataHandler data;                        // XMLBinaryDataMapping
    public List<byte[]> bytes;                      // XMLBinaryDataCollectionMapping
    public URL aUrl;

    public Employee() {}
}
