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
 *     Denise Smith - 2.4 
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.inheritance;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Child extends Parent {

	private String childThing;
	
	public String getChildThing() {
		return childThing;
	}

	public void setChildThing(String childThing) {
		this.childThing = childThing;
	}

	public String getSomeThing() {
		return someThing;
	}

	public void setSomeThing(String param) {
		someThing = param;		
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Child){
			Child childObj = (Child)obj;
			return childThing.equals(childObj.childThing) && someThing.equals(childObj.someThing);
		}
		return false;	
	}
	

}
