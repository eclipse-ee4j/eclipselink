/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
