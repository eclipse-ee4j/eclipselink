/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - Denise Smith February 12, 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelementref.prefix2;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.eclipse.persistence.testing.jaxb.xmlelementref.prefix3.Other;

@XmlRootElement
public class Child {

	@XmlElement
	 public String id;
	
	 @XmlAttribute(required = true)
	 @XmlSchemaType(name = "anyURI")
	 public String href;
	 
	 @XmlElement(namespace = "namespace3")
	 public Other otherThing;
	 
	 public boolean equals(Object obj){
	     if(obj instanceof Child){
	    	 Child compare = (Child)obj;
	    	 return id.equals(compare.id) && href.equals(compare.href) && otherThing.equals(compare.otherThing);
	     }
	     return false;
	 }
}

