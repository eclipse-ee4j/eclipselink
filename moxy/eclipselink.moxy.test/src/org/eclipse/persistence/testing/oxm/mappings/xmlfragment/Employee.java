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
package org.eclipse.persistence.testing.oxm.mappings.xmlfragment;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 *  @version $Header: Employee.java 21-aug-2007.10:50:49 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */

public class Employee {
    String firstName;
    String lastName;
    public org.w3c.dom.Node xmlNode;
    
    public boolean equals(Object obj) {
        if(!(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee)obj;
        boolean equal = this.firstName.equals(emp.firstName);
        equal = equal && this.lastName.equals(emp.lastName);
        try {
            if(emp.xmlNode.getNodeType() == xmlNode.ATTRIBUTE_NODE && xmlNode.getNodeType() == xmlNode.ATTRIBUTE_NODE) {
                Attr att1 = (Attr)emp.xmlNode;
                Attr att2 = (Attr)xmlNode;
                equal = equal && att1.getNodeValue().equals(att2.getNodeValue());
            } else if(emp.xmlNode.getNodeType() == xmlNode.TEXT_NODE && xmlNode.getNodeType() == xmlNode.TEXT_NODE) {
                Text text1 = (Text)emp.xmlNode;
                Text text2 = (Text)this.xmlNode;
                equal = equal && text1.getNodeValue().equals(text2.getNodeValue());
            } else if(emp.xmlNode.getNodeType() == xmlNode.ELEMENT_NODE && xmlNode.getNodeType() == xmlNode.ELEMENT_NODE) {
                //TODO: Proper comparison. for now just check and and number of children
                Element elem1 = (Element)emp.xmlNode;
                Element elem2 = (Element)xmlNode;
                equal = equal && elem1.getNodeName().equals(elem2.getNodeName());
                equal = equal && (elem1.getChildNodes().getLength() == elem2.getChildNodes().getLength());
            } else {
                return false;
            }
        } catch (Exception x) {
            return false;
        }
        return equal;
        
    }
    
    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write("Employee:");
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            Transformer tf = factory.newTransformer();
            DOMSource source = new DOMSource(this.xmlNode);
            StreamResult result = new StreamResult(writer);
            tf.transform(source, result);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return writer.toString();
    }
    
}
