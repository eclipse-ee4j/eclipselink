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
 *     Denise Smith - 2.3
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlattribute.emptynamespace;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="theNamespace")
@XmlRootElement(name="root", namespace="theNamespace")
public class EmptyNamespaceTestObject {

	@XmlAttribute(namespace="")
	public String theTestString;
    
	@XmlElement(namespace="")
	public String theElementTestString;
	
	public boolean equals(Object obj){
		if(obj instanceof EmptyNamespaceTestObject){
			return theTestString.equals(((EmptyNamespaceTestObject)obj).theTestString) && theElementTestString.equals(((EmptyNamespaceTestObject)obj).theElementTestString);
		}
		return false;
	}
}
