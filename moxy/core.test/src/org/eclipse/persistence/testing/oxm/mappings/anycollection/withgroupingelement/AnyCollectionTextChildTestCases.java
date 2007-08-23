/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withgroupingelement;

/**
 *  @version $Header: AnyCollectionTextChildTestCases.java 29-jun-2007.13:21:29 dmahar Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

import java.util.Vector;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyCollectionTextChildTestCases extends XMLMappingTestCases {
    public AnyCollectionTextChildTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyCollectionWithGroupingElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anycollection/withgroupingelement/text_children.xml");
    }
    
    public Object getControlObject() {
        Root root = new Root();
        Vector any = new Vector();
        any.addElement("root's text");
        root.setAny(any);
        return root;
    }
}
