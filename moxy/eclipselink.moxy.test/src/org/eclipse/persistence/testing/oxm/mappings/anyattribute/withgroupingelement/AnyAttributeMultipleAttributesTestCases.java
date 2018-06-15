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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement;

/**
 *  @version $Header: AnyAttributeMultipleAttributesTestCases.java 30-apr-2007.14:04:49 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

import java.io.InputStream;

import java.util.HashMap;
import java.util.Vector;

import javax.xml.namespace.QName;

import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.Root;

public class AnyAttributeMultipleAttributesTestCases extends XMLMappingTestCases {
    public AnyAttributeMultipleAttributesTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyAttributeWithGroupingElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyattribute/withgroupingelement/multiple_attributes.xml");
    }

    public void testXMLToObjectFromSource() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(resourceName);
        StreamSource source = new StreamSource(instream);
        Object testObject = xmlUnmarshaller.unmarshal(source);
        instream.close();
        xmlToObjectTest(testObject);
    }

    public Object getControlObject() {
        Root root = new Root();
        HashMap any = new HashMap();
        QName name = new QName("", "first-name");
        any.put(name, "Matt");
        name = new QName("", "last-name");
        any.put(name, "MacIvor");
        root.setAny(any);
        return root;
    }
}
