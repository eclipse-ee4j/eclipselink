/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - Matt MacIvor - 9/20/2012 - 2.4.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InverseRefChoiceAdapterTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlinversereference/owner.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlinversereference/owner.json";
    
    public InverseRefChoiceAdapterTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ Owner.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);;
    }
    
    public Object getControlObject() {
        Owner owner = new Owner();
        owner.owned = new ArrayList<Owned>();
        Owned owned = new Owned();
        owned.owner = owner;
        owner.owned.add(owned);
        owned = new Owned();
        owned.owner = owner;
        owner.owned.add(owned);
        owned = new Owned();
        owned.owner = owner;
        owner.owned.add(owned);
        return owner;
    }
}