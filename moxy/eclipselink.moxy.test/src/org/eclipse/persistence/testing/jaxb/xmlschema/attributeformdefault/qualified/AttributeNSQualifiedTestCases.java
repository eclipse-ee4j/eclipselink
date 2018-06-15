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
// Denise Smith - February 8, 2013
package org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.qualified;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AttributeNSQualifiedTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschema/attributeformdefault/withAttributeNSQualified.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschema/attributeformdefault/withAttributeNSQualified.json";

    public AttributeNSQualifiedTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{RootWithAttributeNS.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject() {
        RootWithAttributeNS root = new RootWithAttributeNS();
        root.child = "abc";
        return root;
    }
}
