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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement;

import java.util.HashMap;
import javax.xml.namespace.QName;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

/**
 * The Descriptor for Root has a Namepace Resolver with "myns" in it
 * The document has some attributes in the anyattribute mapping with a new prefix/uri
 * ie: (in this case ns0, "www.example.com/test.xsd" are not in any NR)
 * <myns:root ns0:first-name="Matt" ns0:last-name="MacIvor"
 *        xmlns:myns="www.example.com/some-dir/some.xsd"
 *        xmlns:ns0="www.example.com/test.xsd"/>
 */
public class AnyAttributeNotInNRTestCases extends XMLMappingTestCases {
    public AnyAttributeNotInNRTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyAttributeWithoutGroupingElementNSProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyattribute/withoutgroupingelement/multiple_attributes_ns_not_in_nr.xml");
    }

    public Object getControlObject() {
        Root root = new Root();
        HashMap any = new HashMap();
        QName name = new QName("www.example.com/test.xsd", "first-name");
        any.put(name, "Matt");
        name = new QName("http://www.w3.org/2000/xmlns/", "myns");
        any.put(name, "www.example.com/some-dir/some.xsd");
        name = new QName("http://www.w3.org/2000/xmlns/", "ns0");
        any.put(name, "www.example.com/test.xsd");
        name = new QName("www.example.com/test.xsd", "last-name");
        any.put(name, "MacIvor");
        root.setAny(any);
        return root;
    }

    public Object getWriteControlObject() {
        Root root = new Root();
        HashMap any = new HashMap();
        QName name = new QName("www.example.com/test.xsd", "first-name");
        any.put(name, "Matt");
        name = new QName("www.example.com/test.xsd", "last-name");
        any.put(name, "MacIvor");
        root.setAny(any);
        return root;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "oracle.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.AnyAttributeNotInNRTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
