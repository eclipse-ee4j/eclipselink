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
 *     Denise Smith - 2.4 - February 11, 2013
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmlattribute.imports;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.xmlattribute.imports2.IdentifierType;

@XmlRootElement
public class Person {
	  @XmlAttribute
	  private IdentifierType id = null;

	  public String name;
	  
	  public IdentifierType getId() {
	      return id;
	  }

	  public void setId(IdentifierType id) {
	      this.id = id;
	  }
	  
	  public boolean equals(Object obj){
		  if(obj instanceof Person){
			  Person compare = (Person)obj;
			  if(!name.equals(compare.name)){
				  return false;
			  }
			  if(!id.equals(compare.id)){
				  return false;
			  }
			  return true;
		  }
		  return false;
	  }
}
