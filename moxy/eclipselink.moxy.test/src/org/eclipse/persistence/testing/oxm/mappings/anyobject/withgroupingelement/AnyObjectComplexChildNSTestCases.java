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
package org.eclipse.persistence.testing.oxm.mappings.anyobject.withgroupingelement;


/**
 *  @version $Header: AnyObjectComplexChildNSTestCases.java 07-oct-2005.21:46:02 pkrogh Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class AnyObjectComplexChildNSTestCases extends XMLWithJSONMappingTestCases {
    public AnyObjectComplexChildNSTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyObjectWithGroupingElementProjectNS());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyobject/withgroupingelement/complex_child_ns.xml");
        setControlJSON("org/eclipse/persistence/testing/oxm/mappings/anyobject/withgroupingelement/complex_child_ns.json");
    }

    public Object getControlObject() {
        Root root = new Root();
        Child child = new Child();
        child.setContent("child's text");
        root.setAny(child);

        return root;
    }
}

