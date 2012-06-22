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
 * Denise Smith - September 10 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.packagelevel.adapters;

import java.util.ArrayList;
import java.util.List;

public class ClassA {
	public String theValue;
	public List<String> theValues;

	public ClassA(){
		theValues = new ArrayList<String>();
	}
	
	public String getTheValue() {
		return theValue;
	}

	public void setTheValue(String theValue) {
		this.theValue = theValue;
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof ClassA)){
			return false;
		}
		ClassA classAObj = (ClassA)obj;
		
		if(theValue == null){
			if(classAObj.getTheValue() != null){
				return false;
			}
		}else{
			if(classAObj.getTheValue() == null){
				return false;
			}
			if(!getTheValue().equals(classAObj.getTheValue())){
				return false;
			}
		}		
		
		return true;
	}
}
