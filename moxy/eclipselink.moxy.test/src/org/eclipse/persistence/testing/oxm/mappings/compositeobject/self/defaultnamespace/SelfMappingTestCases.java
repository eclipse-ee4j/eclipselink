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
* dmccann - Nov.19/2008 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.defaultnamespace;

import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.AttributeListOnTargetTestProject;

public class SelfMappingTestCases extends XMLMappingTestCases {
    private final static String INSTANCE_XML = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/defaultnamespace/instance.xml";

    public SelfMappingTestCases(String name) throws Exception {
        super(name);
        setControlDocument(INSTANCE_XML);
        setProject(new SelfMappingTestProject());
    }

    protected Object getControlObject() {
        AddressLines lines = new AddressLines();
        lines.addressLine1 = "101 Main Street";
        lines.addressLine2 = "Apt 514";
        lines.addressLine3 = "P.O. Box 123123";
        lines.addressLine4 = "Suite 1234";
        
        Address address = new Address();
        address.addressLines = lines;
        
        address.attentionOfName = "Jane Doe";
        address.careOfName = "n/a";
        address.city = "Malibu";
        address.state = "CA";
        address.countryCode = "USA";
        address.postalCode = "90914-2938";
        
        return address;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.defaultnamespace.SelfMappingTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
