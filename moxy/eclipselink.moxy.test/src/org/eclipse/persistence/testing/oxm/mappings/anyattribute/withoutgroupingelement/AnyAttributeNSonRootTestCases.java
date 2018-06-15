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
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement;

import java.util.HashMap;
import javax.xml.namespace.QName;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

/**
 * The Descriptor for Root has a Namepace Resolver with "myns" in it
 * The Descriptor for Wrapper has a Namepace Resolver with "myns" in it and for ns0
 * The document has some attributes in the anyattribute mapping with a  prefix/uriof ns0 that is on the root
 * ie: (in this case ns0, "www.example.com/test.xsd" is on the root)
*  <myns:wrapper xmlns:myns="www.example.com/some-dir/some.xsd" xmlns:ns0="www.example.com/test.xsd">
 *   <myns:root ns0:first-name="Matt" ns0:last-name="MacIvor"/>
 * </myns:wrapper>
 */
public class AnyAttributeNSonRootTestCases extends XMLMappingTestCases {
    public AnyAttributeNSonRootTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyAttributeWithoutGroupingElementNSProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyattribute/withoutgroupingelement/multiple_attributes_ns_on_root.xml");
    }

    public Object getControlObject() {
        Wrapper theWrapper = new Wrapper();

        Root root = new Root();
        HashMap any = new HashMap();
        QName name = new QName("www.example.com/test.xsd", "first-name");
        any.put(name, "Matt");
        name = new QName("www.example.com/test.xsd", "last-name");
        any.put(name, "MacIvor");
        root.setAny(any);

        theWrapper.setTheRoot(root);
        return theWrapper;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.AnyAttributeNSonRootTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
