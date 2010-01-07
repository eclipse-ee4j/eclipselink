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
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.usemaptests;

import java.util.Hashtable;
import javax.xml.namespace.QName;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

/**
 * 
 */
public class AnyAttributeUsingMapTestCases extends XMLMappingTestCases {
    public AnyAttributeUsingMapTestCases(String name) throws Exception {
        super(name);
        setProject(new AnyAttributeUsingMapProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/mappings/anyattribute/usemaptests/multiple_attributes.xml");
    }
    
    public Object getControlObject() {
        Root root = new Root();
        Hashtable any = new Hashtable();
        QName name = new QName("", "first-name");
        any.put(name, "Matt");
        name = new QName("", "last-name");
        any.put(name, "MacIvor");
        root.setAny(any);
        return root;
    }
}
