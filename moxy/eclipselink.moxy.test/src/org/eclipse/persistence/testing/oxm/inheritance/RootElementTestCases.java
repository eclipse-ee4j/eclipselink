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
*     bdoughan - January 5/2010 - 2.0.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.inheritance;

import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

/**
 * This test case is to verify the fix for the following bug:
 * Bug 298664 - XMLHelper.INSTANCE.load deserializes xml instance to a wrong 
 *              data object type
 */
public class RootElementTestCases extends XMLMappingTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/inheritance/rootelement.xml";

    public RootElementTestCases(String name) throws Exception {
        super(name);
        setProject(new RootElementProject());
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName("car");
        xmlRoot.setSchemaType(new QName(null, "car"));
        xmlRoot.setObject(new Car());
        return xmlRoot;
    }

    @Override
    public Object getReadControlObject() {
        return new Car();
    }

}