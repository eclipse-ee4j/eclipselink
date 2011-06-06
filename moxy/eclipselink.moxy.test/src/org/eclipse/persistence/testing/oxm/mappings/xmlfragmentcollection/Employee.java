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
package org.eclipse.persistence.testing.oxm.mappings.xmlfragmentcollection;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 *  @version $Header: Employee.java 28-may-2007.13:04:10 dmccann Exp $
 *  @author  mmacivor
 *  @since   release specific (what release of product did this appear in)
 */
public class Employee {
    String firstName;
    String lastName;
    public Collection<Node> xmlnodes;
    
    	public Employee() {
		xmlnodes = new java.util.ArrayList();
	}
    
     public boolean equals(Object obj) {
        if(!(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee)obj;
        boolean equal = this.firstName.equals(emp.firstName);
        equal = equal && this.lastName.equals(emp.lastName);
        
        int size =emp.xmlnodes.size();
        int size2 =xmlnodes.size();
        
        if(size != size2) {
          return false;
        }
        Iterator nodesIter = xmlnodes.iterator();
        Iterator empNodesIter = emp.xmlnodes.iterator();
        while(nodesIter.hasNext())
        {
          Node next = (Node)nodesIter.next();
          Node next2 = (Node)empNodesIter.next();
           try {
            if(next2.getNodeType() == next.ATTRIBUTE_NODE && next.getNodeType() == next.ATTRIBUTE_NODE) {
                Attr att1 = (Attr)next2;
                Attr att2 = (Attr)next;
                equal = equal && att1.getNodeValue().equals(att2.getNodeValue());
            } else if(next2.getNodeType() == next.TEXT_NODE && next.getNodeType() == next.TEXT_NODE) {
                Text text1 = (Text)next2;
                Text text2 = (Text)next;
                equal = equal && text1.getNodeValue().equals(text2.getNodeValue());
            } else if(next2.getNodeType() == next.ELEMENT_NODE && next.getNodeType() == next.ELEMENT_NODE) {
                //TODO: Proper comparison. for now just check and and number of children
                Element elem1 = (Element)next2;
                Element elem2 = (Element)next;
                equal = equal && elem1.getNodeName().equals(elem2.getNodeName());
                equal = equal && (elem1.getChildNodes().getLength() == elem2.getChildNodes().getLength());
            } else {
                return false;
            }
        } catch (Exception x) {
            return false;
        }
        }
        
        /*
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
        }*/
        return equal;
        
    }
    
     public String toString() {
        StringWriter writer = new StringWriter();
        writer.write("Employee:");
        writer.write(" " + firstName);
        writer.write(" " + lastName);
        TransformerFactory factory = TransformerFactory.newInstance();
        if(xmlnodes != null){
          Iterator iter = xmlnodes.iterator();
          while(iter.hasNext()){
            Node xmlNode = (Node)iter.next();
            try {
                Transformer tf = factory.newTransformer();
                DOMSource source = new DOMSource(xmlNode);
                StreamResult result = new StreamResult(writer);
                tf.transform(source, result);
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
          }
        }
        return writer.toString();
    }
}
