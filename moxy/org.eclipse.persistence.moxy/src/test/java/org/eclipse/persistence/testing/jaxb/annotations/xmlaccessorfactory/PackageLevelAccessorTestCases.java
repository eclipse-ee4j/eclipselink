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
//     Matt MacIvor - 2.4 - Initial Implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlaccessorfactory;

import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class PackageLevelAccessorTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlaccessorfactory/customer-package.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlaccessorfactory/customer-package.json";

    public PackageLevelAccessorTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { CustomerPackageLevel.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Object getControlObject() {
        CustomerPackageLevel cust = new CustomerPackageLevel();
        cust.fieldProperty = "fieldPropertyValue";
        cust.setProperty("propertyValue");

        return cust;
    }

    @Override
    protected Map getProperties() throws JAXBException {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.XML_ACCESSOR_FACTORY_SUPPORT, true);

        return properties;
    }

}
