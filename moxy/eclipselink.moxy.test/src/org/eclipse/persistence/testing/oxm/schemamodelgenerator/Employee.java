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
* dmccann - Mar 2/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.schemamodelgenerator;

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
    
    public Employee() {}
}
