/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement;


/**
 *  @version $Header: AnyObjectComplexChildNSTestCases.java 07-oct-2005.21:46:02 pkrogh Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
import java.util.Vector;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyObjectComplexChildNSTestCases extends XMLMappingTestCases {
    public AnyObjectComplexChildNSTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyObjectWithGroupingElementProjectNS());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyobject/withgroupingelement/complex_child_ns.xml");
    }

    public Object getControlObject() {
        Root root = new Root();
        Child child = new Child();
        child.setContent("child's text");
        root.setAny(child);

        return root;
    }
}