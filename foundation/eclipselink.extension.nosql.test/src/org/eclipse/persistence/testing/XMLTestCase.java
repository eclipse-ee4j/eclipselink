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

import java.lang.reflect.Array;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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
    
    protected void compareArrays(Object controlValue, Object testValue) {
        assertTrue("Test array is not an Array", testValue.getClass().isArray());
        int controlSize = Array.getLength(controlValue);
        assertTrue("Control and test arrays are not the same length", controlSize == Array.getLength(testValue));
        for(int x=0; x<controlSize; x++) {
            Object controlItem = Array.get(controlValue, x);
            Object testItem = Array.get(testValue, x);
            if(null == controlItem) {
                assertEquals(null, testItem);
                Class controlItemClass = controlItem.getClass();
                if(controlItemClass.isArray()) {
                    compareArrays(controlItem, testItem);
                } else {
                    assertEquals(controlItem, testItem);
                }
            }
        }
    }

    protected void compareValues(Object controlValue, Object testValue){
        if(controlValue instanceof Node && testValue instanceof Node) {
            assertXMLIdentical(((Node)controlValue).getOwnerDocument(), ((Node)testValue).getOwnerDocument());
        } else if(controlValue.getClass().isArray()){
            compareArrays(controlValue, testValue);
        } else {
            assertEquals(controlValue, testValue);
        }
    }
    
    public XMLComparer getXMLComparer() {
        return xmlComparer;
    }
}
