/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Document;

public class XMLTestCase extends junit.framework.TestCase {
    private XMLComparer xmlComparer;
    
    public XMLTestCase(String name) {
        super(name);
        xmlComparer = new XMLComparer();
    }
    
    public void assertXMLIdentical(Document control, Document test) {
        assertTrue("Node " + control + " is not equal to node " + test, xmlComparer.isNodeEqual(control, test));
    }
    
    public XMLComparer getXMLComparer() {
        return xmlComparer;
    }
}
