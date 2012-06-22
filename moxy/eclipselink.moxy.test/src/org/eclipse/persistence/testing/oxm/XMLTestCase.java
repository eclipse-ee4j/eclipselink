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
        boolean isEqual = xmlComparer.isNodeEqual(control, test);
        String controlString = "";
        String testString = "";
        
        if (!isEqual) {
            org.eclipse.persistence.platform.xml.XMLTransformer t = 
                org.eclipse.persistence.platform.xml.XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
            java.io.StringWriter controlWriter = new java.io.StringWriter();
            t.transform(control, controlWriter);
            
            t = org.eclipse.persistence.platform.xml.XMLPlatformFactory.getInstance().getXMLPlatform().newXMLTransformer();
            java.io.StringWriter testWriter = new java.io.StringWriter();
            t.transform(test, testWriter);

            controlString = controlWriter.toString();
            testString = testWriter.toString();
        }
        
        assertTrue("Documents are not equal.\nCONTROL:\n" + controlString + "\nTEST:\n" + testString, isEqual);
    }
    
    public XMLComparer getXMLComparer() {
        return xmlComparer;
    }
}
