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
 * Denise Smith - September 15 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.scope;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="classARoot")
public class ClassA {
	private String someValue;

	public String getSomeValue() {
		return someValue;
	}

	public void setSomeValue(String someValue) {
		this.someValue = someValue;
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof ClassA)){
			return false;
		}
		ClassA classAObj = (ClassA)obj;
		
		if(someValue == null){
			if(classAObj.getSomeValue() != null){
				return false;
			}
		}else{
			if(classAObj.getSomeValue() == null){
				return false;
			}
			if(!someValue.equals(classAObj.getSomeValue())){
				return false;
			}
		}		
		
		return true;
	}
}
