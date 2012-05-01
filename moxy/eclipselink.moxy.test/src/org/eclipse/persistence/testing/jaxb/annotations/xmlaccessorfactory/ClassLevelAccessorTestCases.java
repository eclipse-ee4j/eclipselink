/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Matt MacIvor - 2.4 - Initial Implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlaccessorfactory;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ClassLevelAccessorTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlaccessorfactory/customer-class.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlaccessorfactory/customer-class.json";

    public ClassLevelAccessorTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{CustomerClassOverride.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Object getControlObject() {
        CustomerClassOverride cust = new CustomerClassOverride();
        cust.fieldProperty = "fieldPropertyValue";
        cust.setProperty("propertyValue");
        
        return cust;
    }
}
