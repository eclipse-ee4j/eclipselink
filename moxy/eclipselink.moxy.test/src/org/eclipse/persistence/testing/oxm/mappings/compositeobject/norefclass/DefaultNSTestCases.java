/*******************************************************************************
* Copyright (c) 1998, 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - March 11/2010 - 2.0.2 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.norefclass;

import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass.CompositeObjectSelfNoRefClassNSProject;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass.Root;

public class DefaultNSTestCases  extends XMLWithJSONMappingTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/norefclass/DefaultNS.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/norefclass/DefaultNS.json";
    
    public DefaultNSTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setProject(new CustomerProject());
    }

    @Override
    protected Object getControlObject() {
        Customer customer = new Customer();
        customer.setAddress(new Address());
        return customer;
    }

}