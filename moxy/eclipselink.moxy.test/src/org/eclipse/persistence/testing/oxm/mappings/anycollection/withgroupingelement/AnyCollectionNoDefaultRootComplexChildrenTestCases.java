/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement;


/**
 *  @version $Header: AnyCollectionNoDefaultRootComplexChildrenTestCases.java 29-jun-2007.13:21:27 dmahar Exp $
 *  @author  mfobrien
 *  @since   10.1.3.1.0
 */
import java.util.Vector;
import junit.textui.TestRunner;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyCollectionNoDefaultRootComplexChildrenTestCases extends XMLMappingTestCases {
    public AnyCollectionNoDefaultRootComplexChildrenTestCases(String name) throws Exception {
        super(name);
        Project p = new AnyCollectionNoDefaultRootWithGroupingElementProject();
        ((XMLAnyCollectionMapping)p.getDescriptor(Root.class).getMappingForAttributeName("any")).useCollectionClass(java.util.ArrayList.class);
        setProject(p);
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/no_default_root_element_children.xml");
    }

    public boolean isUnmarshalTest() {
        return false;
    }

    public Object getControlObject() {
        Root root = new Root();
        Vector any = new Vector();
        Child child = new Child();
        child.setContent("Child1");
        any.addElement(child);
        child = new Child();
        child.setContent("Child2");
        any.addElement(child);
        // add object vector to root object
        root.setAny(any);
        return root;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement.AnyCollectionNoDefaultRootComplexChildrenTestCases" };
        TestRunner.main(arguments);
    }

}
