/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement;

import java.util.HashMap;
import javax.xml.namespace.QName;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement.Root;

public class AnyAttributeMultipleAttributesNSTestCases extends XMLMappingTestCases {
    public AnyAttributeMultipleAttributesNSTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyAttributeWithGroupingElementNSProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyattribute/withgroupingelement/multiple_attributes_ns.xml");
    }

    public Object getControlObject() {
        Root root = new Root();
        HashMap any = new HashMap();
        QName name = new QName("www.example.com/some-dir/some.xsd", "first-name");
        any.put(name, "Matt");        
        name = new QName("www.example.com/some-dir/some.xsd", "last-name");
        any.put(name, "MacIvor");
        root.setAny(any);
        return root;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anyattribute.withgroupingelement.AnyAttributeMultipleAttributesNSTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}