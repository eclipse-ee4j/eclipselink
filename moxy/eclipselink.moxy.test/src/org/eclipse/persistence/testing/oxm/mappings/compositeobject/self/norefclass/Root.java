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
 *     Denise Smith - May 8/2009 
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.Employee;
import org.w3c.dom.Node;

public class Root {
	Object theObject;

	public Root(){
		
	}
	
	public Object getTheObject() {
		return theObject;
	}

	public void setTheObject(Object theObject) {
		this.theObject = theObject;
	}
	

    public boolean equals(Object object) {
        try {
            Root theRoot  = (Root)object;
            if (this == theRoot) {
                return true;
            }
            if(theObject == null){
            	return theRoot.getTheObject() == null;            	
            }
            if(theObject instanceof Node){
            	if(theRoot.getTheObject() instanceof Node){
            		//compare Nodes
            		XMLComparer xmlComparer = new XMLComparer();
                    return xmlComparer.isNodeEqual((Node)theObject, (Node)theRoot.getTheObject());
            	}else{
            		return false;
            	}
            }
            return theObject.equals(theRoot.getTheObject());
        } catch (ClassCastException e) {
            return false;
        }        
    }
    
    public String toString() {
    	if(theObject != null){
    		if(theObject instanceof Node){
    			DOMSource source = new DOMSource((Node)theObject);
    			//String s = new String();
    			//StringWriter writer = new StringWriter();
    			ByteArrayOutputStream stream = new ByteArrayOutputStream(); 
    			StreamResult result = new StreamResult(stream);
    			try {
					TransformerFactory.newInstance().newTransformer().transform(source, result);
				} catch (Exception e) {
					e.printStackTrace();	
					return null;
				} 
				
				return stream.toString();
    		}
    		return theObject.toString();
    	}
    	return null;
    }
}
