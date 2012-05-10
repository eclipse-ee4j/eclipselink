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
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement;

/**
 *  @version $Header: AnyAttributeMultipleAttributesTestCases.java 30-apr-2007.15:27:20 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

import java.util.HashMap;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AnyAttributeMultipleAttributesTestCases extends XMLMappingTestCases {
    public AnyAttributeMultipleAttributesTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyAttributeWithoutGroupingElementProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyattribute/withoutgroupingelement/multiple_attributes.xml");
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
