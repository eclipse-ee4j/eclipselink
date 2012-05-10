/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.1 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.inheritance.ns;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class NSTestCases extends XMLWithJSONMappingTestCases {

    public NSTestCases(String name) throws Exception {
        super(name);
        setProject(new NSProject("child"));
        setControlDocument("org/eclipse/persistence/testing/oxm/inheritance/ns.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/inheritance/ns.json");
    }
    
    protected boolean getNamespaceAware(){
    	return true;
    }
    
    protected Map<String, String> getNamespaces(){
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("urn:parent", "parent");
        namespaces.put("urn:child", "child");
        namespaces.put(XMLConstants.SCHEMA_INSTANCE_URL, "xsi");
    	return namespaces;
    }

    
    @Override
    protected NSChild getControlObject() {
        NSChild child = new NSChild();
        child.setParentProp("parentValue");
        child.setChildProp("childValue");
        return child;
    }

    public void testNamespaceConflict() {
        try {
            new XMLContext(new NSProject("parent"));
        } catch(IntegrityException e) {
            return;
        }
        fail();
    }

}