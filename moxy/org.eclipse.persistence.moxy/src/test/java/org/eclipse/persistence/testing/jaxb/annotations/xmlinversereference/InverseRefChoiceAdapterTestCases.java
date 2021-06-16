/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//  - Matt MacIvor - 9/20/2012 - 2.4.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InverseRefChoiceAdapterTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlinversereference/owner.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlinversereference/owner.json";
    private static final String JSON_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlinversereference/ownerschema.json";

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

    public void testJSONSchemaGen() throws Exception{
        InputStream controlSchema = classLoader.getResourceAsStream(JSON_SCHEMA_RESOURCE);
        super.generateJSONSchema(controlSchema);

    }


}
