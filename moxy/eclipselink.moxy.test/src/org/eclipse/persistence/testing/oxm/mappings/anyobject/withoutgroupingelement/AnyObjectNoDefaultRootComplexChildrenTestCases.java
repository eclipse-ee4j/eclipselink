/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement;


/**
 *  @version $Header: AnyObjectNoDefaultRootComplexChildrenTestCases.java 28-apr-2006.15:35:47 mfobrien Exp $
 *  @author  mfobrien
 *  @since   10.1.3.1.0
 */
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.Child;
import org.eclipse.persistence.testing.oxm.mappings.anyobject.withoutgroupingelement.Root;

public class AnyObjectNoDefaultRootComplexChildrenTestCases extends XMLWithJSONMappingTestCases {
    public static final  String XML_RESOURCE_PATH = "org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/no_default_root_element_children.xml";
     public static final String JSON_RESOURCE_PATH = "org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/no_default_root_element_children.json";

    public AnyObjectNoDefaultRootComplexChildrenTestCases(String name) throws Exception {
        super(name);
        Project p = new AnyObjectNoDefaultRootWithoutGroupingElementProject();
        setProject(p);
        setControlDocument(XML_RESOURCE_PATH);
        setControlJSON(JSON_RESOURCE_PATH);
    }

    public boolean isUnmarshalTest() {
        return false;
    }

    public Object getControlObject() {
        Root root = new Root();
        Child child = new Child();
        child.setContent("child's text");
        root.setAny(child);
        return root;
    }

}
